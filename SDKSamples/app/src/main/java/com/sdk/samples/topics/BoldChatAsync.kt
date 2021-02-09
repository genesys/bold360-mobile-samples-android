package com.sdk.samples.topics

import com.common.topicsbase.History
import com.common.utils.ChatForm.defs.ChatType

open class BoldChatAsync : History() {

    override var chatType: String = ChatType.Async

}