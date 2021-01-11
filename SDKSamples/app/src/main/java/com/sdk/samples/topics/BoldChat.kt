package com.sdk.samples.topics

import com.common.topicsbase.BasicChat
import com.common.utils.loginForms.accountUtils.ChatType

open class BoldChat : BasicChat() {

    override val chatType: String
        get() = ChatType.Live

}