package com.sdk.samples.topics

import com.common.utils.handover.MyHandoverHandler
import com.nanorep.convesationui.structure.controller.ChatController

open class Handover : BotChat() {

    override fun getChatBuilder(): ChatController.Builder? {
        return super.getChatBuilder()?.chatHandoverHandler( MyHandoverHandler(this) )
    }
}