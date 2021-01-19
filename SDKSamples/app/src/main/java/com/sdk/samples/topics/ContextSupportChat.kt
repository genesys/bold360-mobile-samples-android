package com.sdk.samples.topics

import com.common.utils.loginForms.accountUtils.FormsParams
import com.common.utils.loginForms.dynamicFormPOC.toBotAccount
import com.nanorep.nanoengine.Account

class ContextSupportChat : BotChat() {

    override val account: Account
        get() = accountData.toBotAccount().apply {
            contexts = mapOf(
                "ContextKey1" to "ContextValue1",
                "ContextKey2" to "ContextValue2"
            )
        }

    override var formsParams = FormsParams.UsingContext

    /*override fun getAccount_old(): Account {
        return (super.getAccount_old() as BotAccount).apply {
            contexts = mapOf(
                "ContextKey1" to "ContextValue1",
                "ContextKey2" to "ContextValue2"
            )
        }
    }*/
}