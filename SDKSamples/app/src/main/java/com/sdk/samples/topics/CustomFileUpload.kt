package com.sdk.samples.topics

import android.view.Gravity
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import com.common.utils.live.UploadFileChooser
import com.common.utils.live.onUploads
import com.integration.core.StateEvent
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.convesationui.structure.providers.ChatUIProvider
import com.nanorep.nanoengine.model.configuration.ChatFeatures
import com.nanorep.sdkcore.utils.px
import com.nanorep.sdkcore.utils.toast
import com.sdk.samples.R

/**
 * Demonstrates how to provide a costumed upload trigger, and do a full
 * upload flow to a live agent.
 */
class CustomFileUpload : BoldChatAvailability() {

    private lateinit var imageButton: ImageButton

    //!- needs to be initiated before Activity's onResume method since it registers to permissions requests
    private val uploadFileChooser = UploadFileChooser(this, 1024 * 1024 * 37)


    override fun startSample(isStateSaved: Boolean) {
        initUploadButton()

        super.startSample(isStateSaved)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        destructMenu?.isVisible = true
        return super.onCreateOptionsMenu(menu)
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

        findViewById<ViewGroup>(R.id.chat_root).addView(imageButton, 0,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                val margin = 3.px
                setMargins(margin, margin, margin, margin)
                gravity = Gravity.END
            })
    }
    //</editor-fold>

    //<editor-fold desc="Custom upload: step 2: Disable the SDKs upload button">
    override fun getChatBuilder(): ChatController.Builder? {
        return super.getChatBuilder()?.apply {
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
                enableMenu(destructMenu, true)

                // !- first, make sure the Upload feature is enabled
                if(chatController.isEnabled(ChatFeatures.FileUpload)) {
                    imageButton.visibility = View.VISIBLE
                } else {
                    toast(this, getString(R.string.file_transfer_not_enabled))
                }
            }

            StateEvent.Ended, StateEvent.ChatWindowDetached -> imageButton.visibility = View.GONE
        }
    }
    //</editor-fold>

    //<editor-fold desc="Activating file upload process">
    /**
     * Called by the SDK when upload request was activated from the SDK UI implementation
     * (i.e. upload button on the input field)
     *
     * If the upload process is being activated by apps custom view, this method will not be called.
     */
    override fun onUploadFileRequest() {
        uploadFileRequest()
    }

    /**
     * starts the files selection and upload process
     */
    private fun uploadFileRequest() {

        uploadFileChooser.apply {
            onUploadsReady = chatController::onUploads
            open()

            /**
            * next steps are done by open() methods:
            * step 4: Opens file browsing activity for files selection
            * step 5: Converts selected files to FileUploadInfo objects [UploadFileChooser.handleFileUploads]
            * step 6: Start Upload for each created FileUploadInfo [ChatController.onUploads]
            * step 7: listens to upload results [ChatController.onUploads] line:182
            */
        }
    }
    //</editor-fold>

    companion object {
        const val TAG = "CustomUploadSample"
    }
}

