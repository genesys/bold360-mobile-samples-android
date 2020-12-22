package com.sdk.samples.topics

import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.bot.BotAccount
import com.sdk.samples.common.customProviders.BalanceEntitiesProvider

open class EntitiesProviderChat : BotChat() {

    override fun getAccount(): Account {
        return (super.getAccount() as BotAccount).apply {
            entities = arrayOf("USER_ACCOUNTS")
        }
    }

    override fun getChatBuilder(): ChatController.Builder? {
        return super.getChatBuilder()?.entitiesProvider( BalanceEntitiesProvider() )
    }

}