package com.sdk.samples.topics

import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.bot.BotAccount
import com.nanorep.sdkcore.utils.toast
import com.sdk.samples.topics.extra.BalanceEntitiesProvider
import com.sdk.samples.topics.extra.withId

class ContextSupportChat : BotChat() {

    override fun getAccount(): Account {
        return (super.getAccount() as BotAccount).apply {
            contexts = mapOf(
                "ContextKey1" to "ContextValue1",
                "ContextKey2" to "ContextValue2"
            )
        }
    }
}