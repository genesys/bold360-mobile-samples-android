package com.sdk.samples.topics

import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.bot.BotAccount
import com.sdk.samples.topics.handover.MyHandoverHandler

open class Handover : BotChat() {

    override fun getAccount(): Account {
        return BotAccount("8bad6dea-8da4-4679-a23f-b10e62c84de8", "jio",
            "Staging_Updated", "qa07", null)
    }
    
    override fun getBuilder(): ChatController.Builder {

        val settings = createChatSettings()

        return ChatController.Builder(this)
            .chatEventListener(this)
            .conversationSettings(settings)
            .chatHandoverHandler(MyHandoverHandler(this))
    }
}