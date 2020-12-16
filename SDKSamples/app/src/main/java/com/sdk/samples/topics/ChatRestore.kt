package com.sdk.samples.topics

import android.util.Log
import com.nanorep.sdkcore.utils.NRError
import com.nanorep.sdkcore.utils.toast

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

                chatController = chatProvider.getChatController()

                getAccount()?.getGroupId()?.let {
                    chatProvider.updateHistoryRepo(targetId = it)
                }

                chatProvider.restore()

            } catch (ex: IllegalStateException) {
                onError(NRError(ex))
            } catch (ex: NullPointerException) {
                onError(NRError(ex))
            }
        }
    }

    fun onCreate() {

        getAccount()?.let { account ->
            try {
                if (hasChatController()) {
                    chatProvider.updateHistoryRepo(targetId = account.getGroupId())
                }
                super.createChat()

            } catch (ex: IllegalStateException) {
                onError(NRError(ex))
            }
        } ?: kotlin.run {
            toast(
                this@ChatRestore,
                "Cannot create chat without a valid account"
            )
            finish()
        }
    }

    override fun startChat() {

        val restoreState = accountProvider.restoreState

        if (restoreState.restoreRequest) onRestore() else onCreate()
    }

    override fun onChatClose() {
        Log.i("RestoreSample", "ChatController hadn't been destructed")
        finish()
    }
}