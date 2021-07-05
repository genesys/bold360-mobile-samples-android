package com.sdk.samples.topics

import com.common.topicsbase.History
import com.common.utils.chatForm.defs.ChatType
import com.common.utils.toast

open class BotChatHistory : History() {

    override var chatType: String = ChatType.Bot

    override fun onUploadFileRequest() {
        toast("The file upload action is not available for this sample.")
    }
}