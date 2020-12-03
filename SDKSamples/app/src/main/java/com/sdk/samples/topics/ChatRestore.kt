package com.sdk.samples.topics

import android.util.Log
import android.view.View
import com.integration.core.StateEvent
import com.nanorep.nanoengine.Account
import com.nanorep.sdkcore.utils.NRError
import kotlinx.android.synthetic.main.restore_layout.*


interface IRestoreSettings {
    fun onCreate(account: Account)
    fun onRestore(account: Account?)
}

open class ChatRestore : History(), IRestoreSettings {

    private var account: Account? = null

    override fun onRestore(account: Account?) {

        this.account = account

        try {
            account?.getGroupId()?.let {
                historyRepository.targetId = it
            }

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