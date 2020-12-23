package com.sdk.samples.topics

import com.nanorep.nanoengine.Account
import com.nanorep.sdkcore.utils.NRError
import com.sdk.samples.common.accountUtils.ChatType
import com.sdk.samples.common.loginForms.RestoreState
import com.sdk.samples.topics.base.RestorationContinuity

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

    private fun restoreChat(): Account? {

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
        return null
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