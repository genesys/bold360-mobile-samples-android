package com.common.chatComponents.customProviders

import com.nanorep.convesationui.bot.CarouselReadoutMessage
import com.nanorep.convesationui.structure.components.ReadRequest
import com.nanorep.convesationui.structure.components.TTSReadAlterProvider

class CustomTTSAlterProvider: TTSReadAlterProvider {

    override fun alter(readRequest: ReadRequest, callback: (ReadRequest) -> Unit) {
        readRequest.readoutMessage.setPersistentPrefix("Persistent Option");
        (readRequest.readoutMessage as? CarouselReadoutMessage)?.setPrefixToItemsOptions("Carousel Option");
        readRequest.readoutMessage.setQuickPrefix("Quick Option");

        // OR:
//            readRequest.readoutResult = "Response text was altered"

        callback.invoke(readRequest)

    }
}