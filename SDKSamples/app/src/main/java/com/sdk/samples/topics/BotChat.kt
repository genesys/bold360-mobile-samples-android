package com.sdk.samples.topics

import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.convesationui.structure.providers.ChatUIProvider
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.bot.BotAccount

open class BotChat : BasicChat() {

    protected val account: BotAccount by lazy {
        defaultBotAccount
    }
        @JvmName("account") get

    override fun getAccount(): Account {
        return account
    }

    override fun getBuilder(): ChatController.Builder {
        return super.getBuilder().apply {
            chatUIProvider(ChatUIProvider(this@BotChat).apply {
                chatInputUIProvider.uiConfig.showUpload = false
            })
        }
    }

    companion object{

        val formalBotAccount = BotAccount(
            "", "jio",
            "Staging_Updated", "mobilestaging", null
        )

        val testAccount = BotAccount(
            "", "jio",
            "Staging_Updated", "mobilestaging", null
        )

        val defaultBotAccount = formalBotAccount
    }
}