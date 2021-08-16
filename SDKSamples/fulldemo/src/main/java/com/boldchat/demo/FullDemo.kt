package com.boldchat.demo

import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.common.chatComponents.NotificationsReceiver
import com.common.chatComponents.customProviders.ContinuityAccountHandler
import com.common.chatComponents.customProviders.CustomTTSAlterProvider
import com.common.chatComponents.handover.CustomHandoverHandler
import com.common.topicsbase.RestorationContinuity
import com.common.utils.SecurityInstaller
import com.common.utils.chatForm.FormFieldFactory
import com.common.utils.chatForm.SampleRepository
import com.common.utils.chatForm.defs.ChatType
import com.common.utils.chatForm.defs.DataKeys
import com.common.utils.live.UploadFileChooser
import com.common.utils.live.onUploads
import com.common.utils.parseSecurityError
import com.common.utils.toast
import com.integration.core.InQueueEvent
import com.integration.core.StateEvent
import com.nanorep.convesationui.structure.FriendlyDatestampFormatFactory
import com.nanorep.convesationui.structure.HandoverHandler
import com.nanorep.convesationui.structure.components.TTSReadAlterProvider
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.convesationui.structure.controller.ChatNotifications
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.model.configuration.ChatFeatures
import com.nanorep.nanoengine.model.configuration.ConversationSettings
import com.nanorep.nanoengine.model.configuration.TimestampStyle
import com.nanorep.nanoengine.model.configuration.VoiceSettings
import com.nanorep.nanoengine.model.configuration.VoiceSupport
import com.nanorep.nanoengine.nonbot.EntitiesProvider
import com.nanorep.sdkcore.model.StatementScope
import com.nanorep.sdkcore.utils.Notifications
import com.nanorep.sdkcore.utils.runMain
import com.nanorep.sdkcore.utils.toast
import com.sdk.common.R
import kotlinx.coroutines.ExperimentalCoroutinesApi

class FullDemo : RestorationContinuity() {

    private val securityInstaller = SecurityInstaller()

    override val extraDataFields: (() -> List<FormFieldFactory.FormField>)?
    get() = {
        (super.extraDataFields?.invoke()?.toMutableList() ?: mutableListOf()).apply {
            add(FormFieldFactory.TextInputField( ChatType.Bot, DataKeys.Welcome, "", "Welcome message id", false ))
            add(FormFieldFactory.ContextBlock())
        }
    }

    private var uploadFile: MenuItem? = null

//  <editor-fold desc=">>>>> Chat initialization <<<<<" >

    private val notificationsReceiver = NotificationsReceiver()

    private var accountProvider: ContinuityAccountHandler? = null
    private var handoverHandler: HandoverHandler? = null
    private var ttsAlterProvider: TTSReadAlterProvider? = null

    private var entitiesProvider: EntitiesProvider? = null

    //!- needs to be initiated before Activity's onResume method since it registers to permissions requests
    private val uploadFileChooser = UploadFileChooser(this, 25 * 1024 * 1024)


    private fun initializeProviders() {

        // Configuring a custom account provider that supports continuity :
        accountProvider = ContinuityAccountHandler( sampleFormViewModel.continuityRepository )

        // Configuring a custom TTS alter provider :
        ttsAlterProvider = CustomTTSAlterProvider()

        // Configuring a custom handover handler :
        handoverHandler = CustomHandoverHandler(this)

        // Uncomment to init the Balance Entities provider handler :
        // entitiesProvider = BalanceEntitiesProvider()

        initInterruptionsReceiver()
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

    /**
     *   A Broadcast which triggers Interruption to the chat.
     *   This is used to stop the voice recognition/readout during phone actions
     */
    private fun initInterruptionsReceiver() {

        LocalBroadcastManager.getInstance(this).registerReceiver(
            object : BroadcastReceiver() {

                override fun onReceive(context: Context, intent: Intent) {
                    Log.d("callAction", "Got broadcast on call action")
                    if (chatController.isActive) {
                        chatController.onChatInterruption()
                    }
                }
            }, IntentFilter("android.CHAT_CALL_ACTION")
        )
    }

    override fun prepareAccount(): Account? {
        return super.prepareAccount()?.apply {
            accountProvider?.prepareAccount(this)
        }
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
            FULL_DEMO_TAG,
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
                    Log.i(FULL_DEMO_TAG, "user is waiting in queue event: user position = $this")
                }
            }

            StateEvent.Unavailable -> runMain {
                toast(stateEvent.state, Toast.LENGTH_SHORT)
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
            Log.d(FULL_DEMO_TAG, ">> got url link selection: [$url]")

            val intent = Intent(Intent.ACTION_VIEW).apply {
                if (isFileUrl(url)) {
                   /* val uri = FileProvider.getUriForFile(
                       // this, BuildConfig.APPLICATION_ID + ".provider",
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
            Log.w(FULL_DEMO_TAG, ">> Failed to activate link on default app: " + e.message)
            super.onUrlLinkSelected(url)
        }
    }

    private fun isFileUrl(url: String): Boolean {
        return url.startsWith("/")
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
    override fun onUploadFileRequest() {
        // sets the selected file info handling method
        // and activates the files selection process
        uploadFileChooser.apply {
            onUploadsReady = chatController::onUploads
            open()
        }
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
                onUploadFileRequest()
                return true
            }
        }

        return true
    }

//  </editor-fold>

//  <editor-fold desc=">>>>> Lifecycle handling <<<<<" >

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findViewById<TextView>(R.id.topic_title).visibility = View.GONE
    }

    override fun onPostResume() {
        super.onPostResume()

        securityInstaller.update(this){ errorCode ->
            val msg = parseSecurityError(errorCode)
            toast(this, msg)
            Log.e(SecurityInstaller.SECURITY_TAG, ">> $msg")
        }
    }

    override fun onStop() {
        if (isFinishing && hasChatController()) { chatController.unsubscribeNotifications(notificationsReceiver) }
        super.onStop()
    }

    // Avoids sample finish animation:
    override fun overridePendingTransition(enterAnim: Int, exitAnim: Int) {}


//  </editor-fold>

    companion object {
        const val FULL_DEMO_TAG = "FullDemo"
    }
}