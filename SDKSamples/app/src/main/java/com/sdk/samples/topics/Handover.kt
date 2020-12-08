package com.sdk.samples.topics

import com.nanorep.convesationui.structure.controller.ChatController
import com.sdk.samples.topics.handover.MyHandoverHandler

open class Handover : BotChat() {

    override fun getBuilder(): ChatController.Builder {

        val settings = createChatSettings()

        return ChatController.Builder(this)
            .chatEventListener(this)
            .conversationSettings(settings)
            .chatHandoverHandler(MyHandoverHandler(this))
    }
}