package com.sdk.samples.topics

import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.bot.BotAccount
import com.sdk.samples.topics.extra.BalanceEntitiesProvider

open class EntitiesProviderChat : BotChat() {

    override fun getAccount(): Account {
        return (super.getAccount() as BotAccount).apply {
            entities = arrayOf("USER_ACCOUNTS")
        }
    }

    override fun getBuilder(): ChatController.Builder {
        return super.getBuilder().entitiesProvider(BalanceEntitiesProvider())
    }

}