package com.sdk.samples.topics

import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.BotAccount

class CustomedWelcomeBotChat : BotChat() {

    override fun getAccount(): Account {
        return (super.getAccount() as BotAccount).apply {
            welcomeMessage = Customed_WM
        }
    }

    companion object{
        const val Customed_WM = "1009687422"
        const val Disable_WM = BotAccount.None

    }
}
