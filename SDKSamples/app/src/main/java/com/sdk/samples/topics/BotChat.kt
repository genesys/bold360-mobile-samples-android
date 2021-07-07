package com.sdk.samples.topics

import com.common.topicsbase.BasicChat
import com.common.utils.chatForm.defs.ChatType

open class BotChat : BasicChat() {

    override var chatType: String = ChatType.Bot

}