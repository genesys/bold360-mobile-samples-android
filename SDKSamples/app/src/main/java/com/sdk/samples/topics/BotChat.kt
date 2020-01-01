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
            "8bad6dea-8da4-4679-a23f-b10e62c84de8", "jio",
            "Staging_Updated", "qa07", null
        )
    }
}