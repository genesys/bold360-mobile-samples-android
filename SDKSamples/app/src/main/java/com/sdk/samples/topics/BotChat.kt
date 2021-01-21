package com.sdk.samples.topics

import com.common.chatComponents.customProviders.withId
import com.common.topicsbase.BasicChat
import com.common.utils.forms.FormDataFactory
import com.common.utils.forms.defs.ChatType
import com.common.utils.forms.toBotAccount
import com.google.gson.JsonArray
import com.nanorep.nanoengine.Account
import com.nanorep.sdkcore.utils.toast

open class BotChat : BasicChat() {

    override val account: Account
        get() = accountData.toBotAccount().withId(this)

    override val chatType: String
        get() = ChatType.Bot

    override val formFieldsData: JsonArray
        get() = FormDataFactory.createForm(ChatType.Bot)

    override fun onUploadFileRequest() {
        toast(this@BotChat, "The file upload action is not available for this sample.")
    }
}