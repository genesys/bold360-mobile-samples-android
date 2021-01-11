package com.common.chatComponents.customProviders

import android.util.Log
import com.integration.core.LastReceivedMessageId
import com.integration.core.SenderId
import com.nanorep.convesationui.async.AsyncAccount
import com.nanorep.convesationui.structure.handlers.AccountSessionListener
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.AccountInfo
import com.nanorep.nanoengine.model.conversation.SessionInfoConfigKeys
import com.nanorep.sdkcore.utils.Completion


/**
 * AccountSessionListener implementation.
 * An account provider that supports chat continuity.
 */
class ContinuityAccountHandler : AccountSessionListener {

    var senderId: String = ""
    var lastReceivedMessageId: String = ""

    private val accounts: MutableMap<String, AccountInfo> = mutableMapOf()

    private fun addAccount(account: AccountInfo) {
        accounts[account.getApiKey()] = account
    }

    private fun continueAsync(account: Account? = null): Account? {

        return account?.apply {
            info.let {
                it.SenderId = senderId.toLongOrNull()
                it.LastReceivedMessageId = lastReceivedMessageId
            }
        }
    }

    override fun provide(info: AccountInfo, callback: Completion<AccountInfo>) {
        val account = accounts[info.getApiKey()]
        callback.onComplete((account as? AsyncAccount)?.let { continueAsync(account) } ?: info )
    }

    override fun update(account: AccountInfo) {

        accounts[account.getApiKey()]?.run {

            account.getInfo().SenderId?.let {
                senderId = "$it"
            }

            update(account)

        } ?: kotlin.run {
            addAccount(account)
        }


    }

    override fun onConfigUpdate(account: AccountInfo, updateKey: String, updatedValue: Any?) {
        try {
            Log.d("AccountSessionListener", "onConfigUpdate: got to update $updateKey with $updatedValue")
            when (updateKey) {
                SessionInfoConfigKeys.LastReceivedMessageId -> lastReceivedMessageId =
                    (updatedValue as? String) ?: ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
