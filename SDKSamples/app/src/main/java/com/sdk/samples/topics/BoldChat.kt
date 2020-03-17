package com.sdk.samples.topics

import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.nanoengine.Account
import com.nanorep.sdkcore.utils.toast

open class BoldChat : BasicChat() {

    protected val account:BoldAccount by lazy {
        defaultBoldAccount
    }
    @JvmName("account") get

    override fun getAccount(): Account {
        return account
    }

    override fun onUrlLinkSelected(url: String) {
        toast(this, "got link: $url")
    }

    companion object{
        val formalBoldAccount = BoldAccount("2300000001700000000:2278936004449775473:sHkdAhpSpMO/cnqzemsYUuf2iFOyPUYV") // Mobile
        val fameBoldAccount = BoldAccount("2300000001700000000:2279148490312878292:grCCPGyzmyITEocnaE+owvjtbasV16eV") // Fame

        val defaultBoldAccount = fameBoldAccount
    }
}