package com.sdk.samples.topics


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.integration.core.LastReceivedMessageId
import com.integration.core.SenderId
import com.integration.core.applicationId
import com.nanorep.convesationui.async.AsyncAccount
import com.nanorep.convesationui.structure.SingleLiveData
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.convesationui.structure.handlers.AccountSessionListener
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.AccountInfo
import com.nanorep.nanoengine.model.conversation.SessionInfoConfigKeys.LastReceivedMessageId
import com.nanorep.sdkcore.utils.Completion
import com.sdk.samples.R
import kotlinx.android.synthetic.main.activity_bot_chat.*
import kotlinx.android.synthetic.main.async_chat_config.*

/*
Async continuity is enabled by:

[1]. providing UserInfo with the same userId over the AsyncAccount (see: BoldChatAsync)

[2]. implementing AccountInfoProvider to listen to account updates. SenderId is important
     for restoring previous chats, and getting missed messages.

[3]. implementing AccountSessionListener to listen to session details updates. LastReceivedMessageId is
     important to fetch only missed messages.

-----------------------------------------------
missed messages = messages that were sent from the agent to the user while the user was off.
*/

class ChatData(val apiKey:String, val applicationId:String, val restore:Boolean)

class ChatViewModel : ViewModel() {
    var apiKey: String = ""
    var applicationId: String = ""

    val onStart = SingleLiveData<ChatData>()
    internal fun startChat(chatData: ChatData) {
        this.onStart.value = chatData
    }
}

private const val TAG = "async"

/**
 * Enables restore and reconnect of last async chat.
 */
open class AsyncChatContinuity : BoldChatAsync() /*[1]*/ {

    /**
     * Handles account save/recover and listens on chat to account updates
     */
    private lateinit var accountRecovery: AsyncAccountRecovery

    private lateinit var chatViewModel: ChatViewModel

    /**
     * setting the accountProvider in order to receive account related updates, and be able to restore chats.
     */
    override fun getBuilder(): ChatController.Builder {
        return super.getBuilder().accountProvider(accountRecovery)
    }

    override fun startChat() {
        openAsyncForm()
    }

    override fun getAccount(): Account {
        return accountRecovery.restoreAccount() ?: account
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        accountRecovery = AsyncAccountRecovery(this)

        chatViewModel = ViewModelProvider(this).get(ChatViewModel::class.java).apply {

            onStart.observe(this@AsyncChatContinuity, Observer<ChatData?> { data ->
                // Start Chat was pressed:
                data?.apiKey?.takeUnless { it.isBlank() }?.run {

                    // update saved account with chat params
                    accountRecovery.saveAccount(data.restore,this, data.applicationId)

                    Log.d(TAG, "creating chat with account ${(getAccount() as AsyncAccount).asString()}")

                    createChat()
                }
            })
        }
    }

    /**
     * Opens the async account details form
     */
    private fun openAsyncForm() {
        chatViewModel.apiKey = accountRecovery.apiKey
        chatViewModel.applicationId = accountRecovery.applicationId

        supportFragmentManager.beginTransaction().add(chat_view.id, AsyncChatForm())
            .addToBackStack(null).commit()
    }
}


/**
 * Async account details form
 */
class AsyncChatForm : Fragment() {

    private val chatViewModel: ChatViewModel by lazy {
        ViewModelProvider(activity!!).get(ChatViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.async_chat_config, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        chatViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        apiKey_edit.setText(chatViewModel.apiKey)
        application_edit.setText(chatViewModel.applicationId)

        start_button.setOnClickListener {

            chatViewModel.startChat(ChatData(apiKey_edit.text.toString(), application_edit.text.toString(), restore_switch.isChecked))
        }
    }
}

/**
 * last chat details save/recover mechanism based on shared preferences.
 * Also implements AccountSessionListener to be able to get session related updates from the SDK
 * while chatting like "LastReceivedMessageId", which is needed for retrieving missed messages.
 */
class AsyncAccountRecovery(var context: Context) : AccountSessionListener {

    private val sharedPrefs: SharedPreferences = context.getSharedPreferences("async_shared", 0)

    val apiKey: String
        get() = sharedPrefs.getString("apiKey", "") ?: ""

    val applicationId: String
        get() = sharedPrefs.getString("applicationId", "") ?: ""

    /**
     * Save provided chat details to the shared preferences.
     */
    fun saveAccount(restore: Boolean, apiKey: String, appId: String, senderId: Long? = null, lastMessage: String? = null) {
        sharedPrefs.run {

            val canRestore = restore && this@AsyncAccountRecovery.apiKey.equals(apiKey)

            edit().apply {
                putString("apiKey", apiKey)
                putString("applicationId", appId)

                /* override session details, SenderId and LastReceivedMessageId if:
                   1. new values were provided
                   2. apiKey is different from the saved account key
                   3. restore is disabled.
                 */
                (senderId ?: 0L.takeUnless { canRestore })?.let { putLong(SenderId, it) }
                (lastMessage ?: "".takeUnless { canRestore })?.let { putString(LastMessageId, lastMessage ?: "") }
                apply()
            }
        }
    }

    private fun saveAccount(account: AsyncAccount) {
        saveAccount(true, account.apiKey, account.info.applicationId, account.info.SenderId, account.info.LastReceivedMessageId)
    }

    /**
     * restore saved account from the shared preferences if exists
     */
    fun restoreAccount(aApiKey: String? = null, appId: String? = null): AsyncAccount? {
        return apiKey.takeUnless { it.isBlank() || aApiKey?.equals(it) == false }?.let { key ->
            AsyncAccount(key).apply {
                info.applicationId = appId?:applicationId
                info.SenderId = sharedPrefs.getLong(SenderId, 0L).takeUnless { it == 0L }
                info.LastReceivedMessageId = sharedPrefs.getString(LastMessageId, "")

                Log.v(TAG, "restoring account ${this.asString()}")
            }
        }
    }


//<editor-fold desc=">>>>> AccountSessionListener implementation [2, 3]<<<<<" >

    override fun provide(info: AccountInfo, callback: Completion<AccountInfo>) {

        return callback.onComplete(restoreAccount(info.getApiKey(), info.getInfo().applicationId) ?: let {
            (info as? AsyncAccount)?.run{it.saveAccount(this)}
            info
        })
    }

    override fun update(account: AccountInfo) {
        try {
            Log.d(TAG, "onUpdate: got to update account senderId ${account.getInfo().SenderId}")

            account.getInfo().SenderId?.let{
                sharedPrefs.edit().putLong(SenderId, it).apply() // updates SenderId in shared preferences
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onConfigUpdate(account: AccountInfo, updateKey: String, updatedValue: Any?) {
        try {
            Log.d(TAG, "onConfigUpdate: got to update $updateKey with $updatedValue")
            when(updateKey){
                // updates LastReceivedMessageId in shared preferences
                LastReceivedMessageId -> sharedPrefs.edit().putString(LastMessageId, updatedValue as String).apply()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

//</editor-fold>

    companion object{
        const val LastMessageId = "LastMessageId"
        const val SenderId = "SenderId"
    }
}


fun AsyncAccount.asString() : String {
    return "Account: [apiKey:$apiKey],[applicationId:${info.applicationId},[senderId:${info.SenderId}" +
            "[lastMessage:${info.LastReceivedMessageId}]"
}

