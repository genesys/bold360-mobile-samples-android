package com.sdk.samples.topics

import com.common.chatComponents.customProviders.withId
import com.common.utils.loginForms.accountUtils.FormsParams
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.bot.BotAccount

class CustomedWelcomeBotChat : BotChat() {

    override var formsParams = FormsParams.Welcome

    override fun getAccount(): Account {
        return (super.getAccount() as BotAccount).withId(this).apply {
            // use the following to prevent welcome message to appear on chat
            // welcomeMessage = BotAccount.None
        }
    }
}
