package com.common.chatComponents.customProviders

import com.common.utils.chatForm.ContinuityRepository
import com.nanorep.convesationui.structure.handlers.AccountInfoProvider
import com.nanorep.nanoengine.AccountInfo
import com.nanorep.nanoengine.bot.BotAccount
import com.nanorep.sdkcore.utils.Completion

/**
 * Samples basic AccountInfoProvider implementation by App, used in the "Live chat escalation from ai & Prechat
 * values".
 */
open class SimpleAccountProvider : AccountInfoProvider {

    private var accounts: MutableMap<String, AccountInfo> = mutableMapOf()

    override fun update(account: AccountInfo) {
        accounts[account.getApiKey()]?.getInfo()?.update(account.getInfo())
    }

    override fun provide(account: AccountInfo, callback: Completion<AccountInfo>) {
        accounts[account.getApiKey()]?.let {
            account.getInfo().update(it.getInfo())
            account
        } ?: addAccount(account)

        callback.onComplete(account)

        /*  >>> uncomment to enable passing preconfigured encrypted info, on chat creation,
                for origin validation, if your account demands it.
                Replace current text with your Secured string.

                callback.onComplete((account as? BoldAccount)?.apply {
                    info.securedInfo = "this is an encrypted content. Don't read"
                }?:account)
        */
    }

    protected open fun addAccount(account: AccountInfo) {
        accounts[account.getApiKey()] = account
    }

}

open class SimpleAccountWithIdProvider(private val repository: ContinuityRepository ) : SimpleAccountProvider() {
    
    override fun update(account: AccountInfo) {
        super.update(account)
        (account as? BotAccount)?.let { repository.saveSessionToken("botUserId_${account.account}", account.userId) }
    }

    fun prepareAccount(account: AccountInfo){
        (account as? BotAccount)?.let{ it.userId = repository.getSessionToken("botUserId_${account.account}") }
    }
}

