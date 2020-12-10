package com.sdk.samples.topics

import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.bot.BotAccount
import com.sdk.samples.topics.extra.withId

class CustomedWelcomeBotChat : BotChat() {

    override fun getAccount(): Account {

        return (viewModel.account as BotAccount).withId(this)
    }

    companion object{
        const val Customed_WM = "1009689562" //"1009687422"

        const val TestEnv_WM = "871383332"

        // use the following to prevent welcome message to appear on chat
        const val Disable_WM = BotAccount.None
    }
}
