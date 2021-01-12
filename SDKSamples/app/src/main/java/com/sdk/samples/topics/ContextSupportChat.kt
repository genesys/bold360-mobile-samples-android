package com.sdk.samples.topics

import com.common.utils.loginForms.accountUtils.ExtraParams
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.bot.BotAccount

class ContextSupportChat : BotChat() {

    override var formsParams = ExtraParams.UsingContext

    override fun getAccount(): Account {
        return (super.getAccount() as BotAccount).apply {
            contexts = mapOf(
                "ContextKey1" to "ContextValue1",
                "ContextKey2" to "ContextValue2"
            )
        }
    }
}