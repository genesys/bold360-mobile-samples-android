package com.sdk.samples.topics


import android.util.Log
import com.integration.core.LastReceivedMessageId
import com.integration.core.SenderId
import com.integration.core.applicationId
import com.integration.core.userInfo
import com.nanorep.convesationui.async.AsyncAccount
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.convesationui.structure.handlers.AccountSessionListener
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.AccountInfo
import com.nanorep.nanoengine.model.conversation.SessionInfoConfigKeys.LastReceivedMessageId
import com.nanorep.sdkcore.utils.Completion
import com.sdk.samples.topics.base.RestorationContinuity
import com.sdk.utils.accountUtils.ChatType
import com.sdk.utils.loginForms.RestoreState
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
class AsyncChatContinuity : RestorationContinuity(), AccountSessionListener {

    override val chatType: String
        get() = ChatType.Async

    private var senderId: String = ""
    private var lastReceivedMessageId: String = ""

    override fun getAccount(): Account? {
        return restoreAccount() ?: super.getAccount()
    }

    override val onAccountData: (account: Account?, restoreState: RestoreState, extraData: Map<String, Any?>?) -> Unit
        get() = { account, restoreState, extraData ->
            chatProvider.account = account
            chatProvider.restoreState = restoreState
            chatProvider.extraData = extraData

            createChat()
        }

    private fun restoreAccount(): Account? {

        return (super.getAccount() as AsyncAccount).takeIf { chatProvider.restoreState.restorable }?.apply {
            info.let {
                it.SenderId = senderId.toLongOrNull()
                it.LastReceivedMessageId = lastReceivedMessageId
            }
        }
    }

    /**
     * setting the accountProvider in order to receive account related updates, and be able to restore chats.
     */
    @ExperimentalCoroutinesApi
    override fun getChatBuilder(): ChatController.Builder? {
        return super.getChatBuilder()?.accountProvider(this)
    }

//<editor-fold desc=">>>>> AccountSessionListener implementation [2, 3]<<<<<" >

    override fun provide(info: AccountInfo, callback: Completion<AccountInfo>) {
        callback.onComplete((info as? AsyncAccount)?.let { restoreAccount() } ?: info)
    }

    override fun update(account: AccountInfo) {
        try {
            Log.d(ASYNC_TAG, "onUpdate: got to update account senderId ${account.getInfo().SenderId}")

            account.getInfo().SenderId?.let {
                senderId = "$it"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onConfigUpdate(account: AccountInfo, updateKey: String, updatedValue: Any?) {
        try {
            Log.d(ASYNC_TAG, "onConfigUpdate: got to update $updateKey with $updatedValue")
            when (updateKey) {
                LastReceivedMessageId -> lastReceivedMessageId = (updatedValue as? String) ?: ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

//    </editor-fold>

    fun AsyncAccount.log(): String {
        return "Account: [apiKey:$apiKey],[applicationId:${info.applicationId}],[userId:${info.userInfo.userId}]," +
                " [senderId:${info.SenderId}],[lastMessage:${info.LastReceivedMessageId}]"
    }
}