package com.sdk.samples.topics

import com.common.topicsbase.BasicChat
import com.common.utils.forms.defs.ChatType
import com.nanorep.sdkcore.utils.toast

open class BotChat : BasicChat() {

    override var chatType: String = ChatType.Bot

    override fun onUploadFileRequest() {
        toast(this@BotChat, "The file upload action is not available for this sample.")
    }
}