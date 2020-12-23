package com.sdk.samples.topics

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.integration.bold.boldchat.core.FormData
import com.integration.bold.boldchat.core.LanguageChangeRequest
import com.integration.bold.boldchat.visitor.api.Form
import com.integration.core.*
import com.integration.core.annotations.FormType
import com.nanorep.convesationui.async.AsyncAccount
import com.nanorep.convesationui.bold.ui.ChatFormViewModel
import com.nanorep.convesationui.bold.ui.FormListener
import com.nanorep.convesationui.structure.FriendlyDatestampFormatFactory
import com.nanorep.convesationui.structure.UploadNotification
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.convesationui.structure.controller.ChatNotifications
import com.nanorep.convesationui.structure.controller.FormProvider
import com.nanorep.convesationui.structure.handlers.AccountSessionListener
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.AccountInfo
import com.nanorep.nanoengine.model.configuration.*
import com.nanorep.nanoengine.model.conversation.SessionInfoConfigKeys
import com.nanorep.sdkcore.model.StatementScope
import com.nanorep.sdkcore.model.SystemStatement
import com.nanorep.sdkcore.utils.*
import com.sdk.samples.BuildConfig
import com.sdk.samples.R
import com.sdk.samples.topics.FullDemo.Companion.FULL_DEMO_TAG
import com.sdk.utils.CustomForm
import com.sdk.utils.live.toFileUploadInfo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.io.File

class FullDemo : ChatRestore() {

    private var uploadFile: MenuItem? = null

//  <editor-fold desc=">>>>> Providers initialization <<<<<" >

    private val notificationsReceiver = NotificationsReceiver()
    private var accountProvider: AccountHandler? = null
    private var formProvider: FormProvider? = null
    private var phoneReceiver: BroadcastReceiver? = null

    init {

        // unComment to apply a custom form provider :
        /*  formProvider = ChatFormProvider(
                 fragmentManager = {
                     supportFragmentManager
                 },
                 onFormPresent = { formType ->

                 })*/

        // Uncomment to register Phone call broadcast to trigger onChatInterruption.
        // A Broadcast which triggers Interruption to the chat.
        // This is used to stop the voice recognition/readout during phone actions.
        /*phoneReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Log.d("callAction", "Got broadcast on call action")
                if (chatController.isActive) {
                    chatController.onChatInterruption()
                }
            }
        }*/

        accountProvider = AccountHandler()


    }

//  </editor-fold>

//  <editor-fold desc=">>>>> Chat initialization <<<<<" >

    override fun createChatSettings(): ConversationSettings {
        return super.createChatSettings()
            .voiceSettings(VoiceSettings(VoiceSupport.HandsFree))
            .enableMultiRequestsOnLiveAgent(true)
            .timestampConfig(
                true, TimestampStyle(
                    "dd.MM hh:mm:ss", 10,
                    Color.parseColor("#33aa33"), null
                )
            )
            .datestamp(true, FriendlyDatestampFormatFactory(this))
    }

    @ExperimentalCoroutinesApi
    override fun getChatBuilder(): ChatController.Builder? {
        return super.getChatBuilder()?.apply {
            accountProvider?.let { accountProvider(it) }
            formProvider?.let { formProvider(it) }
        }
    }

    override fun onChatUIDetached() {
        super.onChatUIDetached()
        destructMenu?.isVisible = true
        enableMenu(destructMenu, hasChatController())
    }

    override fun destructChat() {
        chatController.destruct()
    }

    override fun startChat() {

        phoneReceiver?.let {
            LocalBroadcastManager.getInstance(baseContext).registerReceiver(
                it, IntentFilter("android.CHAT_CALL_ACTION")
            )
        }

        super.startChat()

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

            StateEvent.Unavailable -> lifecycleScope.launch {
                toast(baseContext, stateEvent.state, Toast.LENGTH_SHORT)
            }

            StateEvent.ChatWindowDetached ->
                onChatUIDetached()

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
                    val uri = FileProvider.getUriForFile(
                        baseContext, BuildConfig.APPLICATION_ID + ".provider",
                        File(url)
                    )

                    setDataAndType(uri, "*/*")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                } else {
                    data = Uri.parse(url)
                }
            }

            startActivity(intent)

        } catch (e: Exception) {
            Log.w(FULL_DEMO_TAG, ">> Failed to activate link on default app: " + e.message)
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
            openFilePicker()
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
                    openFilePicker()
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
                Log.i(FULL_DEMO_TAG, "got Upload results: $uploadResult")

                uploadResult.error?.run {
                    if (NRError.Canceled != reason) {
                        chatController.post(SystemStatement(description ?: reason ?: errorCode))
                    }
                }
            }
        }
    }

    private fun openFilePicker() {

        val intent = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            Intent(Intent.ACTION_GET_CONTENT)

        } else {
            Intent(Intent.ACTION_OPEN_DOCUMENT).run {
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }

        }.apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }

        try {
            startActivityForResult(
                Intent.createChooser(intent, "Select files to upload"),
                FILE_UPLOAD_REQUEST_CODE
            )

        } catch (e: ActivityNotFoundException) {
            toast(baseContext, getString(R.string.FileChooserError), Toast.LENGTH_LONG)
        }

    }

    private fun getUploadSizeLimit(scope: StatementScope): Int = when (scope) {
        StatementScope.BoldScope -> 25 * 1024 * 1024
        else -> -1
    }

//  </editor-fold>

//  <editor-fold desc=">>>>> Custom FormProvider implementation <<<<<" >

    inner class CustomFormProvider : FormProvider {

        // prevent the lose of inner references, when being used within the submitForm observer.
        private var formListener: FormListener? = null

        override fun presentForm(formData: FormData, @NonNull callback: FormListener) {

            //////////////////////////////////////
            //-> Pass null to in order to indicate the SDK to display its provided postchat from:
            //////////////////////////////////////
            if (formData.formType == FormType.PostChatForm) {
                callback.onComplete(null)
                return
            }

            //////////////////////////////////////
            // -> Other forms will be customized:
            //////////////////////////////////////

            this.formListener = callback

            /* form fragment arguments passed to it view ViewModel class.
               We're using the [com.nanorep.convesationui.bold.ui.ChatFormViewModel] provided by the SDK,
               since it fits our needs.
             */
            ViewModelProvider(this@FullDemo).get(ChatFormViewModel::class.java).apply {

                //-> sets the form data (fields, branding) on the ViewModel for the CustomForm fragment to use
                onFormData(formData)

                //-> sets an observer to listen to form submission results.
                observeSubmission(this@FullDemo,
                    Observer { event ->
                        Log.e(Custom_Form, "Got form submission event ${event?.state}")

                        val isCanceled = event?.state == StateEvent.Canceled
                        if (isCanceled) {
                            formListener?.onCancel(formData.formType)

                        } else {
                            formListener?.onComplete(event?.data as? Form)
                        }
                    })

                observeLanguageChanges(this@FullDemo, Observer { languageChange ->
                    val language = languageChange?.first

                    Log.i("CustomForm", "Prechat: Language change detected: [${language ?: ""}]")

                    language?.run {
                        formListener?.onLanguageRequest(
                            LanguageChangeRequest(
                                this,
                                formData
                            )
                        ) { result ->
                            // update the forms `FormData` object, to trigger UI display update.
                            result.formData?.let {
                                this@apply.onFormData(it)
                            }

                            // on request callback passing language change approval results
                            languageChange.second.invoke(
                                result.error == null,
                                result.error?.toString()
                            )
                        }
                    }
                })
            }

            val fragment = CustomForm.create()

            supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(
                    R.anim.right_in,
                    R.anim.right_out,
                    R.anim.right_in,
                    R.anim.right_out
                )
                .add(R.id.basic_chat_view, fragment, Custom_Form)
                .addToBackStack(Custom_Form)
                .commit()
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
                uploadFileRequest();
                return true;
            }
        }

        return true
    }

//  </editor-fold>

//  <editor-fold desc=">>>>> Lifecycle handling <<<<<" >

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
            if (resultCode != RESULT_OK || resultData == null) {
                Log.w(FULL_DEMO_TAG, "no file was selected to be uploaded")
                return
            }

            handleFileUploads(resultData)
        }
    }

//  </editor-fold>

    companion object {
        const val FULL_DEMO_TAG = "FullDemo"
        const val Custom_Form = "CustomForm"
        private const val FILE_UPLOAD_REQUEST_CODE = 111
    }
}

//  </editor-fold>

////////////////////////////////////////

//  <editor-fold desc=">>>>> Providers implementations <<<<<" >

/**
 * An account provider that supports chat continuity
 */
class AccountHandler : AccountSessionListener {

    private var senderId: String = ""
    private var lastReceivedMessageId: String = ""

    private val accounts: MutableMap<String, AccountInfo> = mutableMapOf()

    private fun addAccount(account: AccountInfo) {
        accounts[account.getApiKey()] = account
    }

    private fun continueAsync(account: Account? = null): Account? {

        return account?.apply {
            info.let {
                it.SenderId = senderId.toLongOrNull()
                it.LastReceivedMessageId = lastReceivedMessageId
            }
        }
    }

    override fun provide(info: AccountInfo, callback: Completion<AccountInfo>) {
        val account = accounts[info.getApiKey()]
        callback.onComplete((account as? AsyncAccount)?.let { continueAsync(account) } ?: info)
    }

    override fun update(account: AccountInfo) {

        accounts[account.getApiKey()]?.run {

            account.getInfo().SenderId?.let {
                senderId = "$it"
            }

            update(account)

        } ?: kotlin.run {
            addAccount(account)
        }


    }

    override fun onConfigUpdate(account: AccountInfo, updateKey: String, updatedValue: Any?) {
        try {
            Log.d(FULL_DEMO_TAG, "onConfigUpdate: got to update $updateKey with $updatedValue")
            when (updateKey) {
                SessionInfoConfigKeys.LastReceivedMessageId -> lastReceivedMessageId =
                    (updatedValue as? String) ?: ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

////////////////////////////////////////


internal class NotificationsReceiver : Notifiable {

    override fun onNotify(notification: Notification, dispatcher: DispatchContinuation) {
        when (notification.notification) {
            ChatNotifications.PostChatFormSubmissionResults, ChatNotifications.UnavailabilityFormSubmissionResults -> {
                val results = notification.data as FormResults?
                if (results != null) {
                    Log.i(
                        FULL_DEMO_TAG, "Got notified for form results for form: " +
                                results.data +
                                if (results.error != null) ", with error: " + results.error!! else ""
                    )

                } else {
                    Log.w(FULL_DEMO_TAG, "Got notified for form results but results are null")
                }
            }

            Notifications.UploadEnd,
            Notifications.UploadStart,
            Notifications.UploadProgress,
            Notifications.UploadFailed -> {
                val uploadNotification = notification as UploadNotification
                Log.d(
                    FULL_DEMO_TAG, "Got upload event ${uploadNotification.notification} on " +
                            "file: ${uploadNotification.uploadInfo.name}"
                )
            }
        }
    }
}
//  </editor-fold>
