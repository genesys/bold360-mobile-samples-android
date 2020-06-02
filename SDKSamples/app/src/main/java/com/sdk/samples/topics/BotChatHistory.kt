package com.sdk.samples.topics

import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.bot.BotAccount
import com.nanorep.sdkcore.utils.toast

open class BotChatHistory : History() {

    protected val account: BotAccount by lazy {
        Accounts.defaultBotAccount
    }
        @JvmName("account") get

    override fun getAccount(): Account {
        return account
    }

    override fun onUploadFileRequest() {
        toast(this@BotChatHistory, "The file upload action is not available for this sample.")
    }

}