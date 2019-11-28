package com.sdk.samples.topics

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.integration.core.FileUploadInfo
import com.integration.core.StateEvent
import com.integration.core.UploadResult
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.convesationui.structure.providers.ChatUIProvider
import com.nanorep.nanoengine.model.configuration.ChatFeatures
import com.nanorep.sdkcore.model.SystemStatement
import com.nanorep.sdkcore.utils.ErrorException
import com.nanorep.sdkcore.utils.NRError
import com.nanorep.sdkcore.utils.px
import com.nanorep.sdkcore.utils.toast
import com.sdk.samples.R
import com.sdk.samples.topics.ui.live.toFileUploadInfo
import kotlinx.android.synthetic.main.activity_bot_chat.*
import java.util.*

/**
 * Demonstrate how to provide a costumed upload trigger, and do a full
 * upload flow to a live agent.
 */
class CustomFileUpload : BoldChatAvailability() {

    private lateinit var imageButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initUploadButton()
    }

    //<editor-fold desc="Custom upload: step 1: Create your custom upload trigger">
    private fun initUploadButton() {
        imageButton = ImageButton(this).apply {
            setImageResource(R.drawable.outline_publish_black_24)
            setOnClickListener {
                uploadFileRequest()
            }
            visibility = View.GONE

        }

        chat_root.addView(imageButton, 0,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                val margin = 3.px
                setMargins(margin, margin, margin, margin)
                gravity = Gravity.RIGHT
            })
    }
    //</editor-fold>

    //<editor-fold desc="Custom upload: step 2: Disable the SDKs upload button">
    override fun getBuilder(): ChatController.Builder {
        return super.getBuilder().apply {
            chatUIProvider(ChatUIProvider(this@CustomFileUpload).apply {
                chatInputUIProvider.uiConfig.showUpload = false
            })
        }
    }
    //</editor-fold>

    //<editor-fold desc="Custom upload: step 3: Display the custom upload trigger to the customer">
    override fun onChatStateChanged(stateEvent: StateEvent) {
        super.onChatStateChanged(stateEvent)

        when (stateEvent.state) {
            StateEvent.Started -> {
                // !- first, make sure the Upload feature is enabled
                if(chatController.isEnabled(ChatFeatures.FileUpload)) {
                    imageButton.visibility = View.VISIBLE
                }
            }
            StateEvent.Ended, StateEvent.ChatWindowDetached -> imageButton.visibility = View.GONE
        }
    }
    //</editor-fold>

    // the method that will be triggered when the SDK passes upload requests after
    // user pressed the "default" upload button
    // if you have your own upload trigger u don't need to implement this.
    override fun onUploadFileRequest() {
        uploadFileRequest()
    }

    //<editor-fold desc="Custom upload: step 4: React to upload trigger activation">
    // Open the source from which the user will find the file to upload
    private fun uploadFileRequest() {
        val result: Int = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        if (result != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_EXTERNAL_PERMISSION_CODE)
        } else {
            FilePicker(this).openFilePicker()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            READ_EXTERNAL_PERMISSION_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    FilePicker(this).openFilePicker()
                }
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="Custom upload: step 5: Create a FileUploadInfo for every selected file/content and activate the upload">
    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (requestCode == FILE_UPLOAD_REQUEST_CODE) {
            if (resultCode != Activity.RESULT_OK || resultData == null) {
                Log.w(TAG, "no files were selected to be uploaded")
                return
            }
            handleFileUploads(resultData)
        }
    }

    private fun addChosen(uri: Uri, chosenUploadsTarget: ArrayList<FileUploadInfo>) {
        val fileSizeLimit = 1024 * 1024 * 37
        try {
            uri.toFileUploadInfo(this, fileSizeLimit)?.let { chosenUploadsTarget.add(it) }

        } catch (ex: ErrorException) {
            if (NRError.IllegalStateError == ex.error.reason) {
                Log.e(TAG, "file path is invalid")
            }

            chatController.post(
                SystemStatement(
                    ex.error.description ?: getString(R.string.upload_failure_general)
                )
            )
        }
    }

    // go over selected files and converts them to FileUploadInfo
    // and passes for upload
    private fun handleFileUploads(resultData: Intent) {
        val chosenUploadsTarget = arrayListOf<FileUploadInfo>()

        resultData.data?.run {
            addChosen(this, chosenUploadsTarget)

        } ?: resultData.clipData?.run {
            val itemCount = itemCount
            for (i in 0 until itemCount) {
                addChosen(getItemAt(i).uri, chosenUploadsTarget)
            }
        }

        chosenUploadsTarget.forEach { uploadInfo ->
            chatController.uploadFile(uploadInfo, this::onUploadResults)
        }
    }
    //</editor-fold>

    //<editor-fold desc="Custom upload: step 6: listen to upload results">
    // when upload is done by the SDK the results are passed to the upload callback
    private fun onUploadResults(results: UploadResult) {
        Log.i(TAG, "got Upload results:$results")
        val error = results.error
        if (error != null) {
            if (NRError.Canceled != error.reason) {
                val msg = error.description
                chatController.post(SystemStatement(msg ?: error.reason!!))
            }
        }
    }
    //</editor-fold>


    class FilePicker(private val activity: Activity) {
        fun openFilePicker() {
            if (activity.isFinishing || Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                Log.w(TAG, "request for file picker display is discarded")
                if(!activity.isFinishing){
                    toast(activity, "File browsing is supported only on API 19+", background = ColorDrawable(Color.GRAY))
                }
                return
            }
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "*/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            try {
                ActivityCompat.startActivityForResult(
                    activity,
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_UPLOAD_REQUEST_CODE, null
                )
            } catch (e: ActivityNotFoundException) {
                toast(activity, activity.getString(R.string.FileChooserError), Toast.LENGTH_LONG)
            }
        }
    }

    companion object {
        const val TAG = "CustomUploadSample"

        const val READ_EXTERNAL_PERMISSION_CODE = 111
        const val FILE_UPLOAD_REQUEST_CODE = 222
    }
}

