package com.sdk.samples.topics

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import com.integration.core.StateEvent
import com.nanorep.convesationui.async.AsyncAccount
import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.convesationui.structure.HandoverAccount
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.bot.BotAccount
import com.nanorep.sdkcore.model.StatementScope
import com.nanorep.sdkcore.utils.NRError
import com.sdk.samples.R
import kotlinx.android.synthetic.main.activity_bot_chat.*
import kotlinx.android.synthetic.main.restore_layout.*

open class ChatRestore : History(), IRestoreSettings {

    private var account: Account? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prepareUI()
    }

    override fun onRestore(account: Account?) {

        this.account = account

        try {
            historyRepository.targetId = account?.getGroupId()
            chatController.restoreChat(account = account)

        } catch (ex: IllegalStateException) {
            onError(NRError(ex))
        }
    }

    override fun onCreate(account: Account) {
        this.account = account

        try {
            if(hasChatController()) {
                historyRepository.targetId = account.getGroupId()
            }
            createChat()
        } catch (ex: IllegalStateException) {
            onError(NRError(ex))
        }
    }

    private fun prepareUI() {
        supportFragmentManager.beginTransaction()
            .add(chat_view.id, RestoreFragment(), topic_title.text.toString())
            .addToBackStack(null)
            .commit()
    }

    override fun createChat() {
        setLoading(true)
        super.createChat()

        restore_chat.isEnabled = true
    }

    override fun startChat() {
        // should not start chat here.
    }

    override fun getAccount(): Account {
        return account!!
    }

    override fun onChatLoaded() {
        setLoading(false)
    }

    override fun onChatStateChanged(stateEvent: StateEvent) {
        Log.d("Chat event", "chat in state: ${stateEvent.state}")

        when (stateEvent.state) {
            StateEvent.Idle, StateEvent.ChatWindowDetached -> {
                if (supportFragmentManager.backStackEntryCount > 1)
                    onBackPressed()
            }

            else -> super.onChatStateChanged(stateEvent)
        }
    }

    private fun setLoading(loading: Boolean) {
        progressBar.visibility = if (loading) View.VISIBLE else View.INVISIBLE
    }
}


interface IRestoreSettings {
    fun onCreate(account: Account)
    fun onRestore(account: Account?)
    fun hasChatController(): Boolean
}


class RestoreFragment : Fragment() {

    private var restoreSettings: IRestoreSettings? = null
    private var selectedAccount: Account? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.restore_layout, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        restoreSettings = context as? IRestoreSettings
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        create_chat.setOnClickListener {
            selectedAccount?.run { restoreSettings?.onCreate(this) }
        }

        restore_chat.setOnClickListener {
            restoreSettings?.onRestore(selectedAccount)
        }

        chat_action_group.setOnCheckedChangeListener { group, checkedId ->
            selectedAccount = getAccount(getCheckedRadio()?.tag as? String)
            enableChatAction()
        }

        chat_action_group.check(bot_radio.id)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        enableChatAction()
    }

    private fun getAccount(accountName: String?): Account? =

        when (accountName) {
            "bot_chat" -> Accounts.defaultBotAccount

            "bold_chat" -> Accounts.defaultBoldAccount

            "async_chat" -> Accounts.defaultAsyncAccount

            else -> null
        }

    private fun enableChatAction() {
        create_chat.isEnabled = selectedAccount != null
        restore_chat.isEnabled = restoreSettings?.hasChatController() ?: false
    }

    private fun getCheckedRadio() =
        view?.findViewById<RadioButton>(chat_action_group.checkedRadioButtonId)
}