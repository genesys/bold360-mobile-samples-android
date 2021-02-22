package com.sdk.samples.topics

import com.common.chatComponents.customProviders.CustomFormProvider
import com.nanorep.convesationui.structure.controller.ChatController

class BoldCustomChatForm : BoldChatAvailability() {

    private val formProvider = CustomFormProvider(this)

    override fun getChatBuilder(): ChatController.Builder? {
        return super.getChatBuilder()?.formProvider(formProvider)
    }

}