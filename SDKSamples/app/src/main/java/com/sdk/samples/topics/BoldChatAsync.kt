package com.sdk.samples.topics

import com.nanorep.convesationui.async.AsyncAccount
import com.nanorep.nanoengine.Account

open class BoldChatAsync : History() {

    override fun getAccount(): Account {
        return viewModel.account as AsyncAccount
    }

}