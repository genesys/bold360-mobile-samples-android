package com.sdk.samples.topics

import android.util.Log
import com.integration.core.StateEvent
import com.nanorep.nanoengine.Account
import com.nanorep.sdkcore.utils.NRError
import com.nanorep.sdkcore.utils.toast
import com.sdk.samples.common.toAccount

open class ChatRestore : History() {

    private var account: Account? = null

    override fun getAccount(): Account {
        return account!!
    }

    private fun onRestore() {

        if (!hasChatController()) {
            toast(this@ChatRestore,
                "Failed to restore chat\nerror: there is no chat to restore")
        } else {
            try {
                account?.getGroupId()?.let {
                    historyRepository.targetId = it
                }

                chatController.restoreChat(account = account)

            } catch (ex: IllegalStateException) {
                onError(NRError(ex))
            }
        }
    }

    fun onCreate() {

        try {
            if(hasChatController()) {
                historyRepository.targetId = account?.getGroupId()
            }
            createChat()
        } catch (ex: IllegalStateException) {
            onError(NRError(ex))
        }
    }

    override fun startChat() {
        account = intent.getSerializableExtra("account")?.toAccount()

        if (intent.getBooleanExtra("isRestore", false)) onRestore() else onCreate()
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
}