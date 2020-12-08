package com.sdk.samples.topics

import com.nanorep.nanoengine.Account
import com.sdk.samples.common.ChatType
import com.sdk.samples.common.toAccount

open class BoldChat : BasicChat() {

    override fun getAccount(): Account {
        return (intent.getSerializableExtra("account"))?.toAccount(ChatType.LiveChat) ?: Accounts.defaultBoldAccount
    }

}