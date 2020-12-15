package com.sdk.samples.topics

import android.util.Log
import com.nanorep.sdkcore.utils.NRError
import com.nanorep.sdkcore.utils.toast
import com.sdk.samples.topics.history.HistoryRepository
import com.sdk.samples.topics.history.RoomHistoryProvider

class ChatRestore : History() {

    private fun onRestore() {

        if (!hasChatController()) {
            toast(
                this@ChatRestore,
                "Failed to restore chat\nerror: there is no chat to restore"
            )
            finish()
        } else {
            try {

                getAccount().getGroupId()?.let {
                    historyRepository.targetId = it
                }

                chatProvider.restore()

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

        } catch (ex: IllegalStateException) {
            onError(NRError(ex))
        }
    }

    override fun startChat() {

        historyRepository = HistoryRepository(RoomHistoryProvider(this, getAccount().getGroupId()))

        val restoreState = accountProvider.restoreState

        when {
            !restoreState.restoreRequest -> onCreate()
            restoreState.restoreRequest && restoreState.restorable -> onRestore()
            else -> {
                toast(this@ChatRestore, "Account is not restorable")
                finish()
            }
        }
    }

    override fun onChatClose() {
        Log.i("RestoreSample", "ChatController hadn't been destructed")
        finish()
    }
}