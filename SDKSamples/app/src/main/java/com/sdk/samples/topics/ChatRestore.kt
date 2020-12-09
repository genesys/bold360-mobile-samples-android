package com.sdk.samples.topics

import android.util.Log
import com.integration.core.StateEvent
import com.nanorep.nanoengine.Account
import com.nanorep.sdkcore.utils.NRError
import com.nanorep.sdkcore.utils.toast
import com.sdk.samples.topics.history.HistoryRepository
import com.sdk.samples.topics.history.RoomHistoryProvider

class ChatRestore : History() {

    override fun getAccount(): Account {
        return viewModel.account
    }

    override fun hasChatController(): Boolean {
        return viewModel.chatController?.takeIf { !it.wasDestructed }?.let { true } ?: false
    }

    private fun onRestore() {

        if (!hasChatController()) {
            toast(this@ChatRestore,
                "Failed to restore chat\nerror: there is no chat to restore")
        } else {
            try {
                getAccount().getGroupId()?.let {
                    historyRepository.targetId = it
                }

                chatController.restoreChat(account = getAccount())

            } catch (ex: IllegalStateException) {
                onError(NRError(ex))
            }
        }
    }

    fun onCreate() {

        try {
            if(hasChatController()) {
                historyRepository.targetId = getAccount().getGroupId()
            }
            createChat()

            viewModel.chatController = chatController

        } catch (ex: IllegalStateException) {
            onError(NRError(ex))
        }
    }

    override fun startChat() {

        historyRepository = HistoryRepository(RoomHistoryProvider(this, getAccount().getGroupId()))

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