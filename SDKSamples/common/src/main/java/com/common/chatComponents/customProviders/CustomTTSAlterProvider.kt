package com.common.chatComponents.customProviders

import com.nanorep.convesationui.bot.CarouselReadoutMessage
import com.nanorep.convesationui.structure.components.ReadRequest
import com.nanorep.convesationui.structure.components.TTSReadAlterProvider

class CustomTTSAlterProvider: TTSReadAlterProvider {

    override fun alter(readRequest: ReadRequest, callback: (ReadRequest) -> Unit) {

        readRequest.readoutMessage.apply {

            // Persistent options Specific prefix override:
            persistentOptions?.forEachIndexed { index, option ->
                option.prefix = "persistent option no. ${index + 1}"
            }

            // Quick options Specific prefix override :
            quickOptions?.forEachIndexed { index, option ->
                option.prefix = "quick option no. ${index + 1}"
            }

            /* // Uncomment to override the general prefix of the options

            // Persistent options General prefix override:
            setPersistentPrefix("Persistent Option")

            // Quick options General prefix override :
            setQuickPrefix("Quick Option")

            */

            // Uncomment to override the message body
            // body = "Altered message Body"

            // For General prefix for carousel options:
            (this as? CarouselReadoutMessage)?.setPrefixToItemsOptions("Carousel Option")
        }

        // OR:
//            readRequest.readoutResult = "Response text was altered"

        callback.invoke(readRequest)

    }
}