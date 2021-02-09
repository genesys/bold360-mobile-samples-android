package com.sdk.samples.topics

import com.common.utils.chat_form.FormFieldFactory

class ContextSupportChat : BotChat() {

    override var extraDataFields: () -> List<FormFieldFactory.FormField> = {

        listOf(
            FormFieldFactory.ContextBlock()
        )
    }
}