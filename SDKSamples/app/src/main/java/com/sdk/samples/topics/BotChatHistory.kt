package com.sdk.samples.topics

import com.common.topicsbase.History
import com.common.utils.chatForm.defs.ChatType

open class BotChatHistory : History() {

    override var chatType: String = ChatType.Bot

}