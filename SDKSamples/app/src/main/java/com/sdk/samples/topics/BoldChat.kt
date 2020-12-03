package com.sdk.samples.topics

import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.nanoengine.Account

open class BoldChat : BasicChat() {

    protected val account: BoldAccount by lazy {
        Accounts.defaultBoldAccount
    }
    @JvmName("account") get

    override fun getAccount(): Account {
        return account
    }

}