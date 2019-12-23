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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.sdkcore.utils.runMain
import com.nanorep.sdkcore.utils.toast
import com.nanorep.sdkcore.utils.weakRef
import com.sdk.samples.R
import kotlinx.android.synthetic.main.activity_bot_chat.topic_title
import kotlinx.android.synthetic.main.activity_upload_no_ui.*
import java.io.ByteArrayOutputStream


class BoldUploadNoUI : AppCompatActivity(), BoldChatListener {

    private val account =
        BoldAccount("2300000001700000000:2278936004449775473:sHkdAhpSpMO/cnqzemsYUuf2iFOyPUYV").apply {
            skipPrechat()
        }

    private val uploader by lazy {
        BoldLiveUploader()
    }

    private var boldChat: BoldChat? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_no_ui)

        topic_title.text = intent.getStringExtra("title")

        createChat()

    }

    private fun createChat() {

        boldChat = BoldChat().apply {

            visitorInfo = account.info
            wListener = this@BoldUploadNoUI.weakRef()

            try {
                prepare(
                    this@BoldUploadNoUI, account.apiKey,
                    sessionParams = mapOf(
                        SessionParam.IncludeBranding to "true",
                        SessionParam.IncludeLayeredBranding to "true",
                        SessionParam.IncludeChatWindowSettings to "true"
                    )
                )

            } catch (ex: IllegalStateException) {
                Log.e("BoldUploadNoUI", "failed to create bold session")
                finish()
            }

        }.create()

    }

    override fun chatEnded(formData: PostChatData?) {

        if (supportFragmentManager.backStackEntryCount == 0) {
            finish()
        }
    }

    override fun chatUnavailable(formData: UnavailabilityData?) {
        super.chatUnavailable(formData)

        if (!isFinishing) {
            runMain {
                toast(this, "Chat unavailable", background = ColorDrawable(Color.GRAY))
            }
            finish()
        }
    }

    override fun chatCreated(formData: PreChatData?) {
        boldChat?.start()
    }

    override fun visitorInfoUpdated() {
        runMain {
            progress_bar.visibility = View.GONE
            take_a_picture.visibility = View.VISIBLE
        }

        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {

        requestCode.takeIf { it == CAMERA_PERMISSION_REQUEST_CODE &&  grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED }?.run {
            take_a_picture.setOnClickListener {
                startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), CAMERA_REQUEST_CODE)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            (data?.extras?.get("data") as? Bitmap)?.run {
                uploadBitmap(this)
            }
        } else {
            (ContextCompat.getDrawable(this, R.drawable.sample_image) as? BitmapDrawable)?.bitmap?.run { uploadBitmap(this) }
        }
    }

    private fun uploadBitmap(bitmap: Bitmap) {

        val progressController = ProgressController(upload_container)

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
                    progressController.updateText("Upload Completed")
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

    override fun finish() {
        super.finish()

        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    override fun onBackPressed() {

        boldChat?.end() ?: kotlin.run {

            if (supportFragmentManager.backStackEntryCount == 0) {
                finish()
            }
        }

        super.onBackPressed()

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
            progressBar?.progress = progress
        }

        fun updateText(text: String) {
            uploadTitle?.text = text
        }
    }

    companion object {
        const val TAG = "NoUIUploadSample"

        const val CAMERA_REQUEST_CODE = 99
        const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }
}

