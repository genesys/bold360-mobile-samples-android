package com.sdk.samples.topics

import com.common.utils.chatForm.FormFieldFactory

class ContextSupportChat : BotChat() {

    override val extraDataFields: (() -> List<FormFieldFactory.FormField>)
    get() = { listOf( FormFieldFactory.ContextBlock() ) }
}