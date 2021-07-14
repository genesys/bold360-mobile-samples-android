package com.sdk.samples.topics

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.common.topicsbase.SampleActivity
import com.common.utils.chatForm.defs.ChatType
import com.common.utils.toast
import com.integration.bold.BoldChat
import com.integration.bold.BoldChatListener
import com.integration.bold.boldchat.core.PostChatData
import com.integration.bold.boldchat.core.PreChatData
import com.integration.bold.boldchat.core.UnavailabilityData
import com.integration.bold.boldchat.visitor.api.SessionParam
import com.integration.core.BoldLiveUploader
import com.integration.core.FileUploadInfo
import com.integration.core.UploadResult
import com.integration.core.annotations.FileType
import com.integration.core.skipPrechat
import com.nanorep.nanoengine.model.conversation.SessionInfo
import com.nanorep.sdkcore.utils.runMain
import com.nanorep.sdkcore.utils.weakRef
import com.sdk.samples.R
import com.sdk.samples.databinding.ActivityUploadNoUiBinding
import java.io.ByteArrayOutputStream

//TODO: upgrade [BLD-49041]

class BoldUploadNoUI : SampleActivity<ActivityUploadNoUiBinding>(), BoldChatListener {

    override var chatType: String = ChatType.Live

    override val containerId: Int
        get() = R.id.upload_view

    private val uploader by lazy {
        BoldLiveUploader()
    }

    override fun getViewBinding(): ActivityUploadNoUiBinding =
        DataBindingUtil.setContentView(this, R.layout.activity_upload_no_ui)

    private var boldChat: BoldChat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (binding.samplesToolbar as? Toolbar)?.let {
            setSupportActionBar(it)
        }

        binding.topicTitle.text = intent.getStringExtra("title")
    }

    override fun startSample() {
        createChat()

    }

    private fun createChat() {

        boldChat = BoldChat().apply {

            visitorInfo = (account?.info ?: SessionInfo()).apply { skipPrechat = true }
            wListener = this@BoldUploadNoUI.weakRef()

            try {
                prepare(
                    this@BoldUploadNoUI, account?.apiKey ?: "",
                    sessionParams = mapOf(
                        SessionParam.IncludeBranding to "true",
                        SessionParam.IncludeLayeredBranding to "true",
                        SessionParam.IncludeChatWindowSettings to "true"
                    )
                )

            } catch (ex: IllegalStateException) {
                Log.e(TAG, "failed to create bold session")
                finish()
            }

        }.start()

    }

    override fun chatEnded(formData: PostChatData?) {

        if (supportFragmentManager.backStackEntryCount == 0) {
            finish()
        }
    }

    override fun chatUnavailable(formData: UnavailabilityData?) {
        super.chatUnavailable(formData)

        runMain { toast(getString(R.string.chat_unavailable), background = ColorDrawable(Color.GRAY)) }

        if (!isFinishing) {
            finish()
        }
    }

    override fun chatCreated(formData: PreChatData?) {
        boldChat?.start()
    }

    override fun visitorInfoUpdated() {
        runMain {
            binding.progressBar.visibility = View.INVISIBLE
            binding.takeAPicture.visibility = View.VISIBLE
            binding.uploadDefaultImage.visibility = View.VISIBLE
        }

        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        requestCode.takeIf { it == CAMERA_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED }
            ?.run {
                binding.takeAPicture.setOnClickListener {
                    startActivityForResult(
                        Intent(MediaStore.ACTION_IMAGE_CAPTURE),
                        CAMERA_REQUEST_CODE
                    )
                }
            } ?: kotlin.run {
            binding.takeAPicture.isEnabled = false
        }

        binding.uploadDefaultImage.setOnClickListener {
            (ContextCompat.getDrawable(
                this,
                R.drawable.sample_image
            ) as? BitmapDrawable)?.bitmap?.run { uploadBitmap(this) }

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        (data?.extras?.get("data") as? Bitmap)?.takeIf { requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK }?.run {
                uploadBitmap(this)
        }
    }

    private fun uploadBitmap(bitmap: Bitmap) {

        val progressController = ProgressController(binding.uploadContainer)

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

        val fileUploadInfo = FileUploadInfo().apply {
            content = stream.toByteArray()
            name = "Test"
            type = FileType.Picture
        }

        uploader.upload(
            fileUploadInfo,
            boldChat?.sessionInfo(),

            { currentProgress ->

                currentProgress.takeIf { it == 100 }?.run {
                    progressController.updateProgress(this)
                } ?: kotlin.run {
                    progressController.updateText(getString(R.string.upload_completed))
                }
            }

        ) { uploadResult: UploadResult? ->

            uploadResult?.run {

                error?.run {
                    Log.e(TAG, toString())

                } ?: kotlin.run {

                    progressController.updateView(false)

                    Log.i(TAG, "${data?.toString()} uploaded")
                }

            } ?: kotlin.run {
                Log.e(TAG, "null response")
            }
        }
    }

    internal class ProgressController(private val container: ViewGroup){

        private val progressBar: ProgressBar?
        private val uploadTitle: TextView?

        private val context: Context = container.context

        init {

            progressBar = (LayoutInflater.from(context).inflate(R.layout.upload_progress, container, false) as? ProgressBar)?.apply {
                val progressDrawable: Drawable = progressDrawable.mutate()
                progressDrawable.setColorFilter(
                    Color.BLUE,
                    PorterDuff.Mode.SRC_IN
                )
                setProgressDrawable(progressDrawable)
            }

            uploadTitle = TextView(context).apply {
                layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                text = context.getString(R.string.upload_in_progress)
                gravity = Gravity.CENTER_HORIZONTAL
            }

            updateView(true)

        }

        fun updateView(show: Boolean) {

            runMain {

                if (show) {
                    container.addView(uploadTitle)
                    container.addView(progressBar)
                } else {
                    container.removeView(uploadTitle)
                    container.removeView(progressBar)
                }
            }
        }

        fun updateProgress(progress: Int) {
            runMain {
                progressBar?.progress = progress
            }
        }

        fun updateText(text: String) {
            runMain {
                uploadTitle?.text = text
            }
        }
    }

    companion object {
        const val TAG = "NoUIUploadSample"

        const val CAMERA_REQUEST_CODE = 99
        const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }
}

