package com.sdk.samples.topics

import com.common.chatComponents.customProviders.withId
import com.common.topicsbase.BasicChat
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.bot.BotAccount
import com.nanorep.sdkcore.utils.toast

open class BotChat : BasicChat() {

    // !! To be created by Factory
    override val formFields: String
        get() = "[{\"type\":2,\"name\":\"account\",\"value\":\"\",\"label\":\"Account name\",\"hint\":\"Account name\"}," +
                "{\"type\":2,\"name\":\"kb\",\"value\":\"\",\"label\":\"Knowledge Base\",\"hint\":\"Account name\"}," +
                "{\"type\":2,\"name\":\"apiKey\",\"value\":\"\",\"label\":\"Api Key\",\"hint\":\"Account name\"}," +
                "{\"type\":2,\"name\":\"domain\",\"value\":\"\",\"label\":\"Server\",\"hint\":\"Account name\"}]"


    override fun getAccount(): Account {
        return (super.getAccount() as BotAccount).withId(this)
    }

    override fun onUploadFileRequest() {
        toast(this@BotChat, "The file upload action is not available for this sample.")
    }

}