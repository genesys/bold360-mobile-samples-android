package com.common.utils.loginForms

import com.integration.core.securedInfo
import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.nanoengine.Account

interface LoginData {

    /**
     * The Account (Bot/Bold/Async)
     */
    val account: Account?

    /**
     * Extra Account parameters to be submitted for the sample
     */
    val extraData: Map<String, Any?>?

    /**
     * The RestoreState of the account
     */
    val restoreState: RestoreState

    fun prepareAccount(securesInfo: String): Account? {
        return account?.apply {
            (this as? BoldAccount)?.let {
                it.info.securedInfo = securesInfo
            }
        }
    }
}