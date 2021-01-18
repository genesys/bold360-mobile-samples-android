package com.sdk.samples.topics

import com.common.topicsbase.BasicChat
import com.common.utils.loginForms.dynamicFormPOC.defs.ChatType

open class BoldChat : BasicChat() {

    override var chatType = ChatType.Live

}