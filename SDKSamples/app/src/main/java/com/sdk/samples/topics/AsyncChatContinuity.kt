package com.sdk.samples.topics


import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.integration.async.core.UserInfo
import com.integration.core.LastReceivedMessageId
import com.integration.core.SenderId
import com.integration.core.applicationId
import com.integration.core.userInfo
import com.nanorep.convesationui.async.AsyncAccount
import com.nanorep.convesationui.structure.SingleLiveData
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.convesationui.structure.handlers.AccountSessionListener
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.AccountInfo
import com.nanorep.nanoengine.model.conversation.SessionInfoConfigKeys.LastReceivedMessageId
import com.nanorep.sdkcore.utils.Completion
import com.nanorep.sdkcore.utils.toast
import com.sdk.samples.R
import kotlinx.android.synthetic.main.activity_bot_chat.*
import kotlinx.android.synthetic.main.async_chat_config.*
import kotlin.reflect.KProperty

/*
Async continuity is enabled by:

[1]. providing UserInfo with the same userId over the AsyncAccount

[2]. implementing AccountInfoProvider to listen to account updates. SenderId is important
     for restoring previous chats, and getting missed messages.

[3]. implementing AccountSessionListener to listen to session details updates. LastReceivedMessageId is
     important to fetch only missed messages.

-----------------------------------------------
missed messages = messages that were sent from the agent to the user while the user was off.
*/

class ChatData(val account: AsyncAccount, val restore: Boolean)

class ChatViewModel : ViewModel() {
    var account: AsyncAccount? = null

    val onStart = SingleLiveData<ChatData>()
    internal fun startChat(chatData: ChatData) {
        this.onStart.value = chatData
    }
}

private const val TAG = "async"
private const val ASYNC_FORM = "async_form"

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

        accountRecovery = AsyncAccountRecovery(this)

        chatViewModel = ViewModelProvider(this).get(ChatViewModel::class.java).apply {

            onStart.observe(this@AsyncChatContinuity, Observer<ChatData?> { data ->
                //-> Start Chat was pressed:
                data?.run {
                    accountRecovery.saveAccount(this)

                    Log.d(TAG, "creating chat with account ${(getAccount() as AsyncAccount).log()}")

                    createChat()
                }
            })
        }

        super.onCreate(savedInstanceState)
    }

    /**
     * Opens the async account details form
     */
    private fun openAsyncForm() {
        chatViewModel.account = getAccount() as AsyncAccount//accountRecovery.restoreAccount()

        Log.d("async", "Open async form ")
        supportFragmentManager.takeIf { it.findFragmentByTag(ASYNC_FORM) == null }?.beginTransaction()?.add(chat_view.id, AsyncChatForm(), ASYNC_FORM)
            ?.addToBackStack(null)?.commit()
    }

    override fun onChatClose() {
        // For this sample we don't want to end the chat on UI close
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

        //-> init views values with provided account
        apiKey_edit.setText(chatViewModel.account?.apiKey ?: "")
        application_edit.setText(chatViewModel.account?.info?.applicationId ?: "")

        user_edit.filters = arrayOf(InputFilter.LengthFilter(19))

        chatViewModel.account?.info?.userInfo?.let{
            user_edit.setText(it.userId)
            user_first.setText(it.firstName)
            user_last.setText(it.lastName)
        }

        restore_switch.isChecked = chatViewModel.account != null

        start_button.setOnClickListener {

            generateAccount()?.run {
                chatViewModel.startChat(ChatData(this, restore_switch.isChecked))
            }
        }
    }

    /**
     * validates account fields and create account for chat start
     */
    private fun generateAccount(): AsyncAccount? {
        val apiKey = apiKey_edit.text.toString()
        val applicationId = application_edit.text.toString()
        val userId = user_edit.text.toString()
        val userFName = user_first.text.toString()
        val userLName = user_last.text.toString()

        val error = when {
            apiKey.isBlank() -> getString(R.string.missing_apikey)
            applicationId.isBlank() -> getString(R.string.missing_applicationId)
            userId.isBlank() -> getString(R.string.restore_chat_with_no_user).takeIf { restore_switch.isChecked }
            else -> null
        }

        return error?.let {
            context?.run { toast(this, it, background = ColorDrawable(Color.RED)) }
            return null

        } ?: AsyncAccount(apiKey, applicationId).apply {
            // if user id is empty, AsyncAccount provides a auto generated id.
            userId.takeUnless { it.isBlank() }?.let {
                info.userInfo = UserInfo(it).apply {
                    firstName = userFName
                    lastName = userLName
                }
            }
        }
    }
}

/////////////////////////////////////////////////////////////////////////

//<editor-fold desc=">>> Account save & recover <<<" >


/**
 * last chat details save/recover mechanism based on shared preferences.
 * Also implements AccountSessionListener to be able to get session related updates from the SDK
 * while chatting like "LastReceivedMessageId", which is needed for retrieving missed messages.
 */
class AsyncAccountRecovery(var context: Context) : AccountSessionListener {

    private val sharedPrefs: SharedPreferences = context.getSharedPreferences("async_shared", 0)

    operator fun getValue(thisRef: Any?, prop: KProperty<*>): String {
        return sharedPrefs.getString(prop.name, "") ?: ""
    }

    operator fun setValue(thisRef: Any?, prop: KProperty<*>, value: String?) {
        sharedPrefs.edit().putString(prop.name, value).apply()
    }

    private val apiKey: String by this

    private val applicationId: String by this

    private val userId: String by this
    private val userFirst: String by this
    private val userLast: String by this

    private var senderId: String by this

    private var lastReceivedMessageId: String by this

    /**
     * Save provided chat details to the shared preferences.
     */
    fun saveAccount(chatData: ChatData, senderId: Long? = null, lastMessage: String? = null) {
        sharedPrefs.run {

            edit().apply {
                with<AsyncAccount, Unit>(chatData.account, {

                    val canRestore = chatData.restore && this@AsyncAccountRecovery.apiKey.equals(this.apiKey) &&
                            this@AsyncAccountRecovery.applicationId.equals(info.applicationId) &&
                            this@AsyncAccountRecovery.userId.equals(info.userInfo.userId)

                    Log.d(TAG, "selected account details ${if (canRestore) "will be used for chat restore"
                    else "will be used for a new chat creation."} ")

                    if (!canRestore) {
                        putString(this@AsyncAccountRecovery::apiKey.name, apiKey)
                        putString(this@AsyncAccountRecovery::applicationId.name, info.applicationId)
                        putString(this@AsyncAccountRecovery::userId.name, info.userInfo.userId)
                        putString(this@AsyncAccountRecovery::userFirst.name, info.userInfo.firstName)
                        putString(this@AsyncAccountRecovery::userLast.name, info.userInfo.lastName)
                    }

                    /* override session details, SenderId and LastReceivedMessageId if:
                       1. new values were provided
                       2. apiKey is different from the saved account key
                       3. restore is disabled.
                     */
                    (senderId?.toString() ?: "".takeUnless { canRestore })?.let {
                        putString(this@AsyncAccountRecovery::senderId.name, it)
                    }
                    (lastMessage ?: "".takeUnless { canRestore })?.let {
                        putString(this@AsyncAccountRecovery::lastReceivedMessageId.name, lastMessage ?: "")
                    }
                })
                apply()
            }
        }
    }

    private fun saveAccount(account: AsyncAccount) {
        saveAccount(ChatData(account, true), account.info.SenderId, account.info.LastReceivedMessageId)
    }

    /**
     * restores saved account from the shared preferences if exists
     */
    fun restoreAccount(account: AsyncAccount? = null): AsyncAccount? {

        val restored = when {
            account == null -> {
                apiKey.takeUnless { it.isBlank() }?.let {
                    // recover account from the sharedPreferences with no changes.
                    AsyncAccount(it, applicationId).apply {
                        info.userInfo = UserInfo(this@AsyncAccountRecovery.userId).apply {
                            firstName = this@AsyncAccountRecovery.userFirst
                            lastName = this@AsyncAccountRecovery.userLast
                        }
                        info.SenderId = senderId.toLongOrNull()
                        info.LastReceivedMessageId = lastReceivedMessageId
                    }
                }
            }

            // in case of missing required values:
            apiKey.let { it.isBlank() || !account.apiKey.equals(it) } -> null
            applicationId.let { it.isBlank() || !account.info.applicationId.equals(it) } -> null
            userId.let { it.isBlank() || !account.info.userInfo.userId.equals(it) } -> null

            // updates the provided account with saved data.
            else -> {
                account.apply {
                    info.SenderId = senderId.toLongOrNull()
                    info.LastReceivedMessageId = lastReceivedMessageId
                }
            }
        }

        Log.v(TAG, "Restored account => ${account?.log() ?: "not available"}")

        return restored
    }



//<editor-fold desc=">>>>> AccountSessionListener implementation [2, 3]<<<<<" >

    override fun provide(info: AccountInfo, callback: Completion<AccountInfo>) {

        return callback.onComplete((info as? AsyncAccount)?.let { restoreAccount() } ?: let {
            (info as? AsyncAccount)?.run { it.saveAccount(this) }
            info
        })
    }

    override fun update(account: AccountInfo) {
        try {
            Log.d(TAG, "onUpdate: got to update account senderId ${account.getInfo().SenderId}")

            account.getInfo().SenderId?.let {
                senderId = "$it" // updates SenderId in shared preferences
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onConfigUpdate(account: AccountInfo, updateKey: String, updatedValue: Any?) {
        try {
            Log.d(TAG, "onConfigUpdate: got to update $updateKey with $updatedValue")
            when (updateKey) {
                // updates LastReceivedMessageId in shared preferences
                LastReceivedMessageId -> lastReceivedMessageId = (updatedValue as? String) ?: ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

//</editor-fold>

}

//</editor-fold>


fun AsyncAccount.log(): String {
    return "Account: [apiKey:$apiKey],[applicationId:${info.applicationId}],[userId:${info.userInfo.userId}]," +
            " [senderId:${info.SenderId}],[lastMessage:${info.LastReceivedMessageId}]"
}

