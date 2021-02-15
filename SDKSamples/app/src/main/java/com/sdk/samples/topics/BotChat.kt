package com.sdk.samples.topics

import com.common.topicsbase.BasicChat
import com.common.utils.chatForm.defs.ChatType
import com.nanorep.sdkcore.utils.toast

open class BotChat : BasicChat() {

    override var chatType: String = ChatType.Bot

    override fun onUploadFileRequest() {
        toast(baseContext, "The file upload action is not available for this sample.")
    }

}