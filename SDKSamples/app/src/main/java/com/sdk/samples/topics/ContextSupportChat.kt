package com.sdk.samples.topics

import com.common.utils.chatForm.FormFieldFactory

class ContextSupportChat : BotChat() {

    override var extraDataFields: () -> List<FormFieldFactory.FormField> = {

        listOf(
            FormFieldFactory.ContextBlock()
        )
    }
}