package com.sdk.samples.topics

import android.widget.Toast
import com.nanorep.nanoengine.Account
import com.nanorep.sdkcore.utils.NRError
import com.nanorep.sdkcore.utils.toast
import com.sdk.samples.common.accountUtils.ChatType
import com.sdk.samples.common.loginForms.RestoreState
import com.sdk.samples.topics.base.RestorationContinuity
import kotlinx.android.synthetic.main.activity_basic.*

class ChatRestore : RestorationContinuity() {

    override val chatType: String
        get() = ChatType.None

    override val onAccountData: (account: Account?, restoreState: RestoreState, extraData: Map<String, Any?>?) -> Unit

        get() = { account, restoreState, extraData ->

            chatProvider.account = account
            chatProvider.restoreState = restoreState
            chatProvider.extraData = extraData

            if (restoreState.restoreRequest) restore() else create()

        }

    private fun restore(): Account? {
        if (!hasChatController()) {
            toast(
                this@ChatRestore,
                "Failed to restore chat\nerror: there is no chat to restore",
                Toast.LENGTH_SHORT
            )
            finishIfLast()
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
        return null
    }

    private fun create() {

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
                "Cannot create chat without a valid restorable account",
                Toast.LENGTH_SHORT
            )
            finishIfLast()
        }
    }

    override fun startChat() {

        val restoreState = chatProvider.restoreState

        if (restoreState.restoreRequest) restore() else create()
    }

}