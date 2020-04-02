package com.sdk.samples.topics

import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.bot.BotAccount
import com.nanorep.sdkcore.utils.toast

open class BotChat : BasicChat() {

    protected val account: BotAccount by lazy {
        defaultBotAccount
    }
        @JvmName("account") get

    override fun getAccount(): Account {
        return account
    }

    override fun onUploadFileRequest() {
        toast(this@BotChat, "The file upload action is not available for this sample.")
    }

    companion object {

        val formalBotAccount: BotAccount
            get() = BotAccount(
                "",
                "nanorep",
                "English",
                "" //https://eu1-1.nanorep.com/console/login.html
            )

        val defaultBotAccount: BotAccount
            get() = formalBotAccount
    }
}