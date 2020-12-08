package com.sdk.samples.topics

import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.bot.BotAccount
import com.nanorep.sdkcore.utils.toast
import com.sdk.samples.common.toAccount
import com.sdk.samples.topics.extra.withId

open class BotChat : BasicChat() {

    override fun getAccount(): Account {
        return (intent.getSerializableExtra("account")?.toAccount() as? BotAccount ?: Accounts.defaultBotAccount).withId(this)
    }

    override fun onUploadFileRequest() {
        toast(this@BotChat, "The file upload action is not available for this sample.")
    }

}