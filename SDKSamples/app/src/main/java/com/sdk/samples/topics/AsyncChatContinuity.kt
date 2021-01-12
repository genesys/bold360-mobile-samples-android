package com.sdk.samples.topics

import com.common.chatComponents.customProviders.ContinuityAccountHandler
import com.common.topicsbase.RestorationContinuity
import com.common.utils.loginForms.accountUtils.ChatType
import com.integration.core.LastReceivedMessageId
import com.integration.core.SenderId
import com.integration.core.applicationId
import com.integration.core.userInfo
import com.nanorep.convesationui.async.AsyncAccount
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.nanoengine.Account
import kotlinx.coroutines.ExperimentalCoroutinesApi

/*
Async continuity is enabled by:

[1]. providing UserInfo with the same userId over the AsyncAccount

[2]. implementing AccountInfoProvider to listen to account updates. SenderId is important
     for restoring previous chats, and getting missed messages.

[3]. implementing AccountSessionListener to listen to session details updates. LastReceivedMessageId is
     important to fetch only missed messages.

-----------------------------------------------
missed messages = messages that were sent from the agent to the user while the user was off.
*/

private const val ASYNC_TAG = "async"

/**
 * Enables restore and reconnect of last async chat.
 */
class AsyncChatContinuity : RestorationContinuity() {

    private val accountHandler = ContinuityAccountHandler()

    override val chatType: String
        get() = ChatType.Async

    override fun getAccount(): Account? {
        return super.getAccount()?.apply { continueLast(this as AsyncAccount) }
    }

    /**
     * Continues the last chat with this account (if available)
     */
    private fun continueLast(account: AsyncAccount) {
        account.takeIf { chatProvider.loginData.restoreState.restorable }?.apply {
            info.let {
                it.SenderId = accountHandler.senderId.toLongOrNull()
                it.LastReceivedMessageId = accountHandler.lastReceivedMessageId
            }
        }
    }

    /**
     * Configure the accountProvider in order to receive account related updates, and be able to restore chats.
     */
    @ExperimentalCoroutinesApi
    override fun getChatBuilder(): ChatController.Builder? {
        return super.getChatBuilder()?.accountProvider(accountHandler)
    }

    fun AsyncAccount.log(): String {
        return "Account: [apiKey:$apiKey],[applicationId:${info.applicationId}],[userId:${info.userInfo.userId}]," +
                " [senderId:${info.SenderId}],[lastMessage:${info.LastReceivedMessageId}]"
    }
}