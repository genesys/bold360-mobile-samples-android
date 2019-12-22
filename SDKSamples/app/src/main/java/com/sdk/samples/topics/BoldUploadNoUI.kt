package com.sdk.samples.topics

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.integration.bold.BoldChat
import com.integration.bold.BoldChatListener
import com.integration.bold.boldchat.visitor.api.SessionParam
import com.integration.core.BoldLiveUploader
import com.integration.core.FileUploadInfo
import com.integration.core.Uploader
import com.integration.core.annotations.FileType
import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.sdkcore.utils.runMain
import com.nanorep.sdkcore.utils.weakRef
import com.sdk.samples.R
import kotlinx.android.synthetic.main.activity_bot_chat.topic_title
import kotlinx.android.synthetic.main.activity_upload_no_ui.*
import java.io.ByteArrayOutputStream


class BoldUploadNoUI : AppCompatActivity(), BoldChatListener {

    private val CAMERA_CODE = 99
    private val PERMISSION_REQUEST_CODE = 100

    private val account =
        BoldAccount("2300000001700000000:2278936004449775473:sHkdAhpSpMO/cnqzemsYUuf2iFOyPUYV").apply {
            skipPrechat()
        }

    private lateinit var boldChat: BoldChat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_no_ui)

        topic_title.text = intent.getStringExtra("title")

    }

    override fun onStart() {
        super.onStart()

        startChat()
    }

    open fun startChat() {
        createChat()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    private fun createChat() {

        boldChat = BoldChat().apply {

            visitorInfo = account.info
            wListener = this@BoldUploadNoUI.weakRef()

            try {
                prepare(this@BoldUploadNoUI, account.apiKey,
                    sessionParams = mapOf(SessionParam.IncludeBranding to "true",
                        SessionParam.IncludeLayeredBranding to "true",
                        SessionParam.IncludeChatWindowSettings to "true"))

            } catch (ex: IllegalStateException) {
                Log.e("BoldUploadNoUI", "failed to create bold session")
                finish()
            }
        }

        boldChat.start()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        if (supportFragmentManager.backStackEntryCount == 0) {
            finish()
        }
    }

    override fun visitorInfoUpdated() {

        runMain {
            progress_bar.visibility = View.GONE
            take_a_picture.visibility = View.VISIBLE
        }

        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        requestCode.takeIf { it == PERMISSION_REQUEST_CODE &&  grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED }?.run {
            take_a_picture.setOnClickListener {
                startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), CAMERA_CODE)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_CODE && resultCode == RESULT_OK) {
            val bitmap = data?.extras?.get("data") as? Bitmap
            bitmap?.run {
                val stream = ByteArrayOutputStream()
                compress(Bitmap.CompressFormat.JPEG, 100, stream)
                val fileUploadInfo = FileUploadInfo()
                fileUploadInfo.content = stream.toByteArray()
                fileUploadInfo.name = "Test"
                fileUploadInfo.type = FileType.Picture

                val uploader: Uploader = BoldLiveUploader()

/*                uploader.upload(fileUploadInfo, boldChat.sessionInfo(),  null) { uploadResult: UploadResult? ->

                    uploadResult?.run {
                        error?.run {
                            Log.e("upload", toString())
                            Assert.fail()
                        } ?: kotlin.run {
                            Log.i("upload","${data?.toString()} uploaded")
                        }
                    } ?: kotlin.run {
                        Log.e("upload", "null response")
                        Assert.fail()
                    }


                    try{
                        cont.resume(Unit)
                    }catch (ex:IllegalStateException){}

                }*/
            }
        }
    }
}

