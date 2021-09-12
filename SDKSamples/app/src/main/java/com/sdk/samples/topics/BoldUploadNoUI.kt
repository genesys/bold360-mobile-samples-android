package com.sdk.samples.topics

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
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
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import com.integration.core.annotations.ErrorCodes.INPUT_OUTPUT_ERROR
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

    lateinit var activityLauncher: ActivityResultLauncher<Intent>

    override val containerId: Int
        get() = R.id.upload_view

    private val uploader by lazy {
        BoldLiveUploader()
    }

    private val getPermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results: Map<String, Boolean> ->
        enableTakeAPic(
            results.all { entry -> entry.value }  // if all permissions were granted
        )
    }

    override fun getViewBinding(): ActivityUploadNoUiBinding =
        DataBindingUtil.setContentView(this, R.layout.activity_upload_no_ui)

    private var boldChat: BoldChat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.topicTitle.text = intent.getStringExtra("title")

        activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == Activity.RESULT_OK) {
                (result.data?.extras?.get("data") as? Bitmap)?.run {
                    uploadBitmap(this)
                }
            }
        }
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
        finishIfLast()
    }

    override fun chatUnavailable(formData: UnavailabilityData?) {
        super.chatUnavailable(formData)

        runMain {
            toast( getString(R.string.chat_unavailable), Toast.LENGTH_SHORT)
        }

        if (!isFinishing) {
            finish()
        }
    }

    override fun error(code: Int, message: String?, data: Any?) {
        runMain {
            message?.let { toast(it) }
            if (code == INPUT_OUTPUT_ERROR && !isFinishing) finish()
        }
    }

    override fun chatCreated(formData: PreChatData?) {
        // Since we skip the prechat, the form data is null and we call the boldChat?.start() again, to actually start the chat after it was created..
        boldChat?.start()
    }

    override fun chatStarted() {
        runMain {

            binding.progressBar.visibility = View.INVISIBLE
            binding.takeAPicture.visibility = View.VISIBLE
            binding.uploadDefaultImage.visibility = View.VISIBLE

        }

        getPermissions.launch(arrayOf(android.Manifest.permission.CAMERA))

        binding.uploadDefaultImage.setOnClickListener {
            ( ContextCompat.getDrawable( this, R.drawable.sample_image) as? BitmapDrawable)?.bitmap?.run { uploadBitmap(this) }
        }

    }

    private fun enableTakeAPic(enable: Boolean) {
        if (enable) {
            binding.takeAPicture.isEnabled = true
            binding.takeAPicture.setOnClickListener {
                activityLauncher.launch( Intent(MediaStore.ACTION_IMAGE_CAPTURE) )
            }
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

    override fun onBackPressed() {
        boldChat?.end()
        super.onBackPressed()
    }

    companion object {
        const val TAG = "NoUIUploadSample"

        const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }
}

