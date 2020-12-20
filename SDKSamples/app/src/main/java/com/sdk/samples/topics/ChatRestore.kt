package com.sdk.samples.topics

import android.util.Log
import android.widget.Toast
import com.nanorep.sdkcore.utils.NRError
import com.nanorep.sdkcore.utils.getCurrent
import com.nanorep.sdkcore.utils.toast
import com.nanorep.sdkcore.utils.weakRef
import com.sdk.samples.common.accountUtils.ChatType
import com.sdk.samples.common.loginForms.AccountFormController
import com.sdk.samples.common.loginForms.RestoreForm
import kotlinx.android.synthetic.main.activity_basic.*

class ChatRestore : History() {

    private fun onRestore() {

        if (!hasChatController()) {
            toast(
                this@ChatRestore,
                "Failed to restore chat\nerror: there is no chat to restore",
                Toast.LENGTH_SHORT
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
                "Cannot create chat without a valid account",
                Toast.LENGTH_SHORT
            )
            finish()
        }
    }

    override fun startChat() {

        val restoreState = chatProvider.restoreState

        if (restoreState.restoreRequest) onRestore() else onCreate()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.getCurrent()?.tag == RestoreForm.TAG) {
            finish()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    override fun onChatUIDetached() {
        reloadForms()
    }

    private fun reloadForms() {
        Log.i("RestoreSample", "ChatController hadn't been destructed")
        val accountFormController = AccountFormController(basic_chat_view.id, supportFragmentManager.weakRef())
        accountFormController.updateChatType(ChatType.None, null){ account, restoreState, extraData ->
            chatProvider.account = account
            chatProvider.restoreState = restoreState
            chatProvider.extraData = extraData

            if (restoreState.restoreRequest) onRestore() else onCreate()
        }
    }
}