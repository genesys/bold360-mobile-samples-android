package com.sdk.samples.topics

import com.common.topicsbase.BasicChat
import com.common.utils.chat_form.defs.ChatType
import com.nanorep.sdkcore.utils.toast

open class BotChat : BasicChat() {

    override var chatType: String = ChatType.Bot

    override fun onUploadFileRequest() {
        toast(this@BotChat, "The file upload action is not available for this sample.")
    }
}