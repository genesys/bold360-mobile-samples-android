package com.sdk.samples.topics

import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.bot.BotAccount

open class BotChat : BasicChat() {

    protected val account: BotAccount by lazy {
        BotAccount(
            "",
            "nanorep",
            "English",
            "" //https://eu1-1.nanorep.com/console/login.html
        )
    }
        @JvmName("account") get

    override fun getAccount(): Account<*> {
        return account
    }

}