package com.sdk.samples.topics

import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.bot.BotAccount

open class BotChat : BasicChat() {

    protected val account: BotAccount by lazy {
        defaultBotAccount
    }
        @JvmName("account") get

    override fun getAccount(): Account {
        return account
    }

    companion object{
        val defaultBotAccount = BotAccount(
            "",
            "nanorep",
            "English",
            "" //https://eu1-1.nanorep.com/console/login.html
        )

        val testAccount = BotAccount(
            "", "jio",
            "Staging_Updated", "mobilestaging", null
        )
    }
}