package com.sdk.samples.topics

import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.bot.BotAccount
import com.sdk.samples.topics.extra.withId

class CustomedWelcomeBotChat : BotChat() {

    override fun getAccount(): Account {
        return (super.getAccount() as BotAccount).withId(this).apply {
            // use the following to prevent welcome message to appear on chat
            // welcomeMessage = BotAccount.None
        }
    }
}
