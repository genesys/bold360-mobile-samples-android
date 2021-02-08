package com.sdk.samples.topics

import com.common.topicsbase.History
import com.common.utils.forms.defs.ChatType
import com.nanorep.sdkcore.utils.toast

open class BotChatHistory : History() {

    override var chatType: String = ChatType.Bot

    override fun onUploadFileRequest() {
        toast(this@BotChatHistory, "The file upload action is not available for this sample.")
    }
}