package com.sdk.samples.topics

import com.common.topicsbase.History
import com.common.utils.loginForms.accountUtils.ChatType

open class BoldChatAsync : History() {

    override val chatType: String
        get() = ChatType.Async

}