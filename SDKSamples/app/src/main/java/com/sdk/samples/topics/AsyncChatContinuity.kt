package com.sdk.samples.topics

import com.common.chatComponents.customProviders.ContinuityAccountHandler
import com.common.topicsbase.RestorationContinuity
import com.common.utils.ChatForm.FormFieldFactory
import com.common.utils.ChatForm.defs.ChatType
import com.common.utils.ChatForm.defs.DataKeys
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

/**
 * Enables restore and reconnect of last async chat.
 */

class AsyncChatContinuity : RestorationContinuity() {

    override var chatType: String = ChatType.Async

    private val accountHandler = ContinuityAccountHandler()

    override var extraDataFields: () -> List<FormFieldFactory.FormField> = { listOf(
                FormFieldFactory.TextInputField(DataKeys.AppId, "", "Application ID", false),
                FormFieldFactory.TextInputField(DataKeys.UserId, "", "UserId", false),
                FormFieldFactory.EmailInputField(DataKeys.Email, "", "Email", false),
                FormFieldFactory.TextInputField(DataKeys.FirstName, "", "First Name", false),
                FormFieldFactory.TextInputField(DataKeys.LastName, "", "Last Name", false))
        }

    /**
     * Continues the last chat with this account (if available)
     */
    private fun continueLast(account: AsyncAccount) {
        account.takeIf { accountController.isRestorable(baseContext, ChatType.Async) }?.apply {
            info.let {
                it.SenderId = accountHandler.senderId.toLongOrNull()
                it.LastReceivedMessageId = accountHandler.lastReceivedMessageId
            }
        }
    }

    override fun prepareAccount(): Account = (account as AsyncAccount).apply { continueLast(this) }

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
