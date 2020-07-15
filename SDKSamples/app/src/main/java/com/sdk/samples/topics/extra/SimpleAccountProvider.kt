package com.sdk.samples.topics.extra

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.integration.core.LastReceivedMessageId
import com.integration.core.SenderId
import com.integration.core.userInfo
import com.nanorep.convesationui.async.AsyncAccount
import com.nanorep.convesationui.structure.handlers.AccountInfoProvider
import com.nanorep.nanoengine.AccountInfo
import com.nanorep.nanoengine.bot.BotAccount
import com.nanorep.nanoengine.model.conversation.SessionInfo
import com.nanorep.nanoengine.model.conversation.SessionInfoConfigKeys
import com.nanorep.sdkcore.utils.Completion

open class SimpleAccountProvider(val context: Context) : AccountInfoProvider {

    var accounts: MutableMap<String, AccountInfo> = mutableMapOf()

    override fun update(account: AccountInfo) {
        accounts[account.getApiKey()]?.getInfo()?.update(account.getInfo())
        updateAccount(account)
    }

   private fun updateAccount(account: AccountInfo) {
        if (account is AsyncAccount) {
            updateAsyncSession(account.getInfo())
        } else if (account is BotAccount) {
            updateBotSession(account)
        }
    }

    private fun updateBotSession(account: BotAccount) {
        try {
            val preferences: SharedPreferences = context.getSharedPreferences(
                "bot_chat_session",
                Context.MODE_PRIVATE
            )
            val edit = preferences.edit()
            edit.putString("botUserId_" + account.account, account.userId)
            edit.apply()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun updateAsyncSession(info: SessionInfo) {
        try {
            val preferences: SharedPreferences = context.getSharedPreferences(
                "async_chat_session",
                Context.MODE_PRIVATE
            )
            val edit = preferences.edit()
            val userInfo = info.userInfo
           /* Log.d(
                app.com.nanoconversationdemo.fragment.MainFragment.TAG_MainFragment,
                "updateAsyncSession: userInfo: $userInfo"
            )*/
            edit.putString("userInfo", Gson().toJson(userInfo))
            val senderId = info.SenderId
            if (senderId != null) {
              /*  Log.d(
                    app.com.nanoconversationdemo.fragment.MainFragment.TAG_MainFragment,
                    "updateAsyncSession: senderId: $senderId"
                )*/
                edit.putLong(SessionInfoConfigKeys.SenderId, senderId)
            }
            val lastReceivedMessageId = info.LastReceivedMessageId
           /* Log.d(
                app.com.nanoconversationdemo.fragment.MainFragment.TAG_MainFragment,
                "updateAsyncSession: lastReceivedMessageId: $lastReceivedMessageId"
            )*/
            edit.putString(SessionInfoConfigKeys.LastReceivedMessageId, lastReceivedMessageId)
            edit.apply()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun provide(account: AccountInfo, callback: Completion<AccountInfo>) {
        accounts[account.getApiKey()]?.let{
            account.getInfo().update(it.getInfo())
            account
        }?:let{
            addAccount(account)
        }
        callback.onComplete(account)
    }

    protected open fun addAccount(account: AccountInfo) {
        accounts[account.getApiKey()] = account
    }
}