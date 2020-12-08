package com.sdk.samples.topics

import com.nanorep.nanoengine.Account
import com.sdk.samples.common.toAccount

open class BoldChatAsync : History() {

    override fun getAccount(): Account {
        return (intent.getSerializableExtra("account"))?.toAccount() ?: Accounts.defaultAsyncAccount
    }

}