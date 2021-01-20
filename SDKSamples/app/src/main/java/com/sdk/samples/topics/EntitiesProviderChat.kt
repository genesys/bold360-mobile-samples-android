package com.sdk.samples.topics

import com.common.chatComponents.customProviders.BalanceEntitiesProvider
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.bot.BotAccount
//
//open class EntitiesProviderChat : BotChat() {
//
//    override val account: Account
//        get() = (super.account as BotAccount).apply {
//            entities = arrayOf("USER_ACCOUNTS")
//        }
//
//    /*override fun getAccount_old(): Account {
//        return (super.getAccount_old() as BotAccount).apply {
//            entities = arrayOf("USER_ACCOUNTS")
//        }
//    }*/
//
//    override fun getChatBuilder(): ChatController.Builder? {
//        return super.getChatBuilder()?.entitiesProvider( BalanceEntitiesProvider() )
//    }
//
//}