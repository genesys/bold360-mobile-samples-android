package com.sdk.samples.topics

import com.nanorep.convesationui.structure.controller.ChatController
import com.sdk.utils.handover.MyHandoverHandler

open class Handover : BotChat() {

    override fun getChatBuilder(): ChatController.Builder? {
        return super.getChatBuilder()?.chatHandoverHandler( MyHandoverHandler(this) )
    }
}