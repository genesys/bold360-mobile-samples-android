package com.sdk.samples.topics

import android.annotation.SuppressLint
import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.nanoengine.Account

class BoldChat : BasicChat() {
    override fun getAccount(): Account {
        return BoldAccount("2300000001700000000:2279148490312878292:grCCPGyzmyITEocnaE+owvjtbasV16eV")
    }
}