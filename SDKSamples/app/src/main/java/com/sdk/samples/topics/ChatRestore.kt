package com.sdk.samples.topics

import com.common.topicsbase.RestorationContinuity
import com.common.utils.accountUtils.ChatType
import com.common.utils.loginForms.RestoreState
import com.nanorep.nanoengine.Account
import com.nanorep.sdkcore.utils.NRError

open class ChatRestore : RestorationContinuity() {

    override val chatType: String
        get() = ChatType.None

    override val onAccountData: (account: Account?, restoreState: RestoreState, extraData: Map<String, Any?>?) -> Unit
        get() = { account, restoreState, extraData ->

            chatProvider.account = account
            chatProvider.restoreState = restoreState
            chatProvider.extraData = extraData

            startChat()
        }

    private fun restoreChat() {

        if (!hasChatController()) {

            onRestoreFailed("There is no chat to restore")

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

    override fun createChat() {

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

            onRestoreFailed("Cannot create chat without a valid restorable account")

        }
    }

    override fun startChat() {
        if ( chatProvider.restoreState.restoreRequest ) restoreChat() else createChat()
    }

}