package com.sdk.samples.topics

import com.common.utils.loginForms.accountUtils.FormsParams

class CustomedWelcomeBotChat : BotChat() {

    override var formsParams = FormsParams.Welcome

   /* override fun getAccount_old(): Account {
        return (super.getAccount_old() as BotAccount).withId(this).apply {
            // use the following to prevent welcome message to appear on chat
            // welcomeMessage = BotAccount.None
        }
    }*/
}
