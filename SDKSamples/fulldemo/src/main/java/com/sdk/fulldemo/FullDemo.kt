package com.sdk.fulldemo

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.common.chatComponents.NotificationsReceiver
import com.common.chatComponents.customProviders.ContinuityAccountHandler
import com.common.chatComponents.customProviders.CustomFormProvider
import com.common.chatComponents.customProviders.CustomTTSAlterProvider
import com.common.chatComponents.handover.CustomHandoverHandler
import com.common.topicsbase.RestorationContinuity
import com.common.utils.live.createPickerIntent
import com.common.utils.live.toFileUploadInfo
import com.common.utils.loginForms.accountUtils.ExtraParams
import com.integration.core.FileUploadInfo
import com.integration.core.InQueueEvent
import com.integration.core.StateEvent
import com.nanorep.convesationui.structure.FriendlyDatestampFormatFactory
import com.nanorep.convesationui.structure.HandoverHandler
import com.nanorep.convesationui.structure.components.TTSReadAlterProvider
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.convesationui.structure.controller.ChatNotifications
import com.nanorep.convesationui.structure.controller.FormProvider
import com.nanorep.nanoengine.model.configuration.*
import com.nanorep.nanoengine.nonbot.EntitiesProvider
import com.nanorep.sdkcore.model.StatementScope
import com.nanorep.sdkcore.model.SystemStatement
import com.nanorep.sdkcore.utils.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import com.sdk.common.R

open class FullDemo : RestorationContinuity() {

//    private var singletonSamplesViewModelFactory = SingletonSamplesViewModelFactory( SamplesViewModel.getInstance() )

    override val extraFormsParams = super.extraFormsParams.apply {
        add(ExtraParams.UsingContext)
        add(ExtraParams.Welcome)
    }

    private var uploadFile: MenuItem? = null

//  <editor-fold desc=">>>>> Chat initialization <<<<<" >

    private val notificationsReceiver = NotificationsReceiver()

    private var accountProvider: ContinuityAccountHandler? = null
    private var handoverHandler: HandoverHandler? = null
    private var ttsAlterProvider: TTSReadAlterProvider? = null
    private var formProvider: FormProvider? = null

    private var entitiesProvider: EntitiesProvider? = null

    private fun initializeProviders() {
        // Configuring a custom account provider that supports continuity :
        accountProvider = ContinuityAccountHandler()

        // Configuring a custom TTS alter provider :
        ttsAlterProvider = CustomTTSAlterProvider()

        // Configuring a custom form provider :
        formProvider = CustomFormProvider(this.weakRef())

        // Configuring a custom handover handler :
        handoverHandler = CustomHandoverHandler(baseContext)

        // Uncomment to init the Balance Entities provider handler :
        // entitiesProvider = BalanceEntitiesProvider()

    }

    override fun createChatSettings(): ConversationSettings {
        initializeProviders()
        return super.createChatSettings()
            .voiceSettings(VoiceSettings(VoiceSupport.HandsFree))
            .enableMultiRequestsOnLiveAgent(true)
            .datestamp(true, FriendlyDatestampFormatFactory(this))
            .timestampConfig(
                true, TimestampStyle(
                    "dd.MM hh:mm:ss", 10,
                    Color.parseColor("#33aa33"), null
                )
            )
    }

    @ExperimentalCoroutinesApi
    override fun getChatBuilder(): ChatController.Builder? {
        return super.getChatBuilder()?.apply {
            accountProvider?.let { accountProvider(it) }
            formProvider?.let { formProvider(it) }
            handoverHandler?.let { chatHandoverHandler(it) }
            entitiesProvider?.let { entitiesProvider(it) }
            ttsAlterProvider?.let { ttsReadAlterProvider(it) }
        }
    }

    override fun onChatUIDetached() {
        destructMenu?.isVisible = true
        enableMenu(destructMenu, hasChatController())
        super.onChatUIDetached()
    }

    override fun destructChat() {
        chatController.destruct()
    }

    /**
     *   A Broadcast which triggers Interruption to the chat.
     *   This is used to stop the voice recognition/readout during phone actions
     */
    fun initInterfaceReceiver() {

        LocalBroadcastManager.getInstance(baseContext).registerReceiver(
            object : BroadcastReceiver() {

                override fun onReceive(context: Context, intent: Intent) {
                    Log.d("callAction", "Got broadcast on call action")
                    if (chatController.isActive) {
                        chatController.onChatInterruption()
                    }
                }
            }, IntentFilter("android.CHAT_CALL_ACTION"))
    }


    // Runs on the first creation of the ChatController
    // Afterwards the Chat is being restored/created via the "reloadForms" method
    override fun createChat() {

        // Uncomment to register a Phone call broadcast to trigger onChatInterruption.
        // initInterfaceReceiver()

        // Creates the chat controller
        super.createChat()

        // Registers the app to the wanted chat Notifications
        if ( hasChatController() ) {
            chatController.apply {
                subscribeNotifications(
                    notificationsReceiver,
                    ChatNotifications.PostChatFormSubmissionResults,
                    ChatNotifications.UnavailabilityFormSubmissionResults,
                    Notifications.UploadEnd,
                    Notifications.UploadStart,
                    Notifications.UploadProgress,
                    Notifications.UploadFailed,
                    Notifications.VoiceStopRequest,
                    Notifications.ChatInterruption
                )
            }
        }
    }

//  </editor-fold>

//  <editor-fold desc=">>>>> ChatEventListener implementation <<<<<" >

    override fun onChatStateChanged(stateEvent: StateEvent) {

        Log.d(
            FULL_DEMO_Sample_TAG,
            "onChatStateChanged: state " + stateEvent.state + ", scope = " + stateEvent.scope
        )

        when (stateEvent.state) {

            StateEvent.Preparing -> destructMenu?.isVisible = false

            StateEvent.Started -> {
                enableMenu(endMenu, chatController.hasOpenChats())
                if (stateEvent.scope == StatementScope.BoldScope) {
                    enableMenu(uploadFile, chatController.isEnabled(ChatFeatures.FileUpload))
                }
            }

            StateEvent.InQueue -> {
                (stateEvent as? InQueueEvent)?.position?.run {
                    Log.i(FULL_DEMO_Sample_TAG, "user is waiting in queue event: user position = $this")
                }
            }

            StateEvent.Unavailable -> lifecycleScope.launch {
                toast(baseContext, stateEvent.state, Toast.LENGTH_SHORT)
            }

            StateEvent.ChatWindowDetached -> onChatUIDetached()

            StateEvent.Ending, StateEvent.Ended -> {
                enableMenu(uploadFile, false)
                if (!chatController.hasOpenChats()) {
                    removeChatFragment()
                }
            }

        }
    }

    override fun onUrlLinkSelected(url: String) {
        // sample code for handling given link
        try {
            Log.d(FULL_DEMO_Sample_TAG, ">> got url link selection: [$url]")

            val intent = Intent(Intent.ACTION_VIEW).apply {
                if (isFileUrl(url)) {
                   /* val uri = FileProvider.getUriForFile(
                       // baseContext, BuildConfig.APPLICATION_ID + ".provider",
                        File(url)
                    )*/

                //    setDataAndType(uri, "*/*")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                } else {
                    data = Uri.parse(url)
                }
            }

            startActivity(intent)

        } catch (e: Exception) {
            Log.w(FULL_DEMO_Sample_TAG, ">> Failed to activate link on default app: " + e.message)
            toast(
                this,
                ">> got url: [$url]",
                Toast.LENGTH_SHORT,
                background = ColorDrawable(Color.GRAY)
            )
        }
    }

    private fun isFileUrl(url: String): Boolean {
        return url.startsWith("/")
    }

    override fun onUploadFileRequest() {
        uploadFileRequest()
    }

    //-> previous listener method signature @Override onPhoneNumberNavigation(@NonNull String phoneNumber) {
    override fun onPhoneNumberSelected(phoneNumber: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phoneNumber")
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Log.w(ChatTag, ">> Failed to activate phone dialer default app: " + e.message)
        }
    }

    /***
     * starts the file upload process. asks for user permissions to browse storage and display the
     * file picker.
     */
    private fun uploadFileRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                FILE_UPLOAD_REQUEST_CODE
            )

        } else {
            startPickerActivity()
        }
    }

//  </editor-fold>

//  <editor-fold desc=">>>>> Custom FileUpload implementation <<<<<" >

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            FILE_UPLOAD_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startPickerActivity()
                }
            }
        }
    }

    private fun handleFileUploads(resultData: Intent) {
        val chosenUploadsTarget = ArrayList<FileUploadInfo>()

        val fileSizeLimit = getUploadSizeLimit(chatController.getScope())

        val fileUri = resultData.data

        fun addChosen(uri: Uri) {
            try {
                uri.toFileUploadInfo(this, fileSizeLimit)?.let { chosenUploadsTarget.add(it) }

            } catch (ex: ErrorException) {
                chatController.post(
                    SystemStatement(
                        ex.error.description
                            ?: getString(R.string.upload_failure_general)
                    )
                )
            }
        }

        if (fileUri == null) {
            val clipData = resultData.clipData
            if (clipData != null) {
                val itemCount = clipData.itemCount
                for (i in 0 until itemCount) {
                    addChosen(clipData.getItemAt(i).uri)
                }
            }
        } else {
            addChosen(fileUri)
        }

        for (uploadInfo in chosenUploadsTarget) {
            chatController.uploadFile(uploadInfo) { uploadResult ->
                Log.i(FULL_DEMO_Sample_TAG, "got Upload results: $uploadResult")

                uploadResult.error?.run {
                    if (NRError.Canceled != reason) {
                        chatController.post(SystemStatement(description ?: reason ?: errorCode))
                    }
                }
            }
        }
    }

    private fun startPickerActivity() {
        createPickerIntent{
            try {
                startActivityForResult(
                    Intent.createChooser(intent, "Select files to upload"),
                    FILE_UPLOAD_REQUEST_CODE
                )

            } catch (e: ActivityNotFoundException) {
                toast(baseContext, getString(R.string.FileChooserError), Toast.LENGTH_LONG)
            }
        }
    }

    private fun getUploadSizeLimit(scope: StatementScope): Int = when (scope) {
        StatementScope.BoldScope -> 25 * 1024 * 1024
        else -> -1
    }

//  </editor-fold>

//  <editor-fold desc=">>>>> Menu items customization <<<<<" >

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        super.onCreateOptionsMenu(menu)

        this.uploadFile = menu?.findItem(R.id.upload_file)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        super.onOptionsItemSelected(item)

        when (item.itemId) {
            R.id.upload_file -> {
                uploadFileRequest();
                return true;
            }
        }

        return true
    }

//  </editor-fold>

//  <editor-fold desc=">>>>> Lifecycle handling <<<<<" >

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findViewById<TextView>(com.sdk.fulldemo.R.id.topic_title).visibility = View.GONE
    }

    private fun clearAllResources() {
        try {
            chatController.run {
                unsubscribeNotifications(notificationsReceiver)
                terminateChat()
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    override fun onStop() {
        if (isFinishing) { clearAllResources() }
        super.onStop()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)

        if (requestCode == FILE_UPLOAD_REQUEST_CODE) {
            resultData?.takeIf { resultCode == RESULT_OK }?.run {
                handleFileUploads(this)
            } ?: kotlin.run { Log.w(FULL_DEMO_Sample_TAG, "no file was selected to be uploaded") }
        }
    }

    // Avoids sample finish animation:
    override fun overridePendingTransition(enterAnim: Int, exitAnim: Int) {}

    // Clears the used view holder
    override fun onDestroy() {
//        singletonSamplesViewModelFactory.clear()
        super.onDestroy()
    }


//  </editor-fold>

    companion object {
        const val FULL_DEMO_Sample_TAG = "FullDemoSample"
        private const val CUSTOM_FORM_TAG = "CustomForm"
        private const val FILE_UPLOAD_REQUEST_CODE = 111
    }
}

//  </editor-fold>