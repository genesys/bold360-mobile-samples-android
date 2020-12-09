package com.sdk.samples.topics

import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.nanoengine.Account

open class BoldChat : BasicChat() {

    override fun getAccount(): Account {
        return viewModel.account as BoldAccount
    }

}