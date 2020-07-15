package com.sdk.samples.topics.extra

import android.content.Context
import android.content.SharedPreferences
import com.nanorep.convesationui.structure.handlers.AccountInfoProvider
import com.nanorep.nanoengine.AccountInfo
import com.nanorep.nanoengine.bot.BotAccount
import com.nanorep.sdkcore.utils.Completion
import java.lang.ref.WeakReference

open class SimpleAccountProvider() : AccountInfoProvider {

    var accounts: MutableMap<String, AccountInfo> = mutableMapOf()

    override fun update(account: AccountInfo) {
        accounts[account.getApiKey()]?.getInfo()?.update(account.getInfo())
    }

    override fun provide(account: AccountInfo, callback: Completion<AccountInfo>) {
        accounts[account.getApiKey()]?.let{
            account.getInfo().update(it.getInfo())
            account
        }?: addAccount(account)
        callback.onComplete(account)
    }

    protected open fun addAccount(account: AccountInfo) {
        accounts[account.getApiKey()] = account
    }

}

class SimpleAccountWithIdProvider(private val context: WeakReference<Context>): SimpleAccountProvider() {

    override fun update(account: AccountInfo) {
        super.update(account)
        (account as? BotAccount)?.let { updateBotSession(it) }
    }

    private fun updateBotSession(account: BotAccount) {
        context.get()?.run {
            try {
                val preferences: SharedPreferences = getSharedPreferences(
                    "bot_chat_session",
                    Context.MODE_PRIVATE
                )
                preferences.edit().putString("botUserId_" + account.account, account.userId).apply()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }
}

