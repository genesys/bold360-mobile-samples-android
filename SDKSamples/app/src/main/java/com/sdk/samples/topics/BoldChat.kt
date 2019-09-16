package com.sdk.samples.topics

import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.nanoengine.Account

open class BoldChat : BasicChat() {

    protected val account:BoldAccount by lazy {
        BoldAccount("2300000001700000000:2279148490312878292:grCCPGyzmyITEocnaE+owvjtbasV16eV")
    }
    @JvmName("account") get

    override fun getAccount(): Account {
        return account
    }

}