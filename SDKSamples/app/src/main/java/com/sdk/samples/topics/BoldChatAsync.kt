package com.sdk.samples.topics

import com.common.topicsbase.History
import com.common.utils.loginForms.dynamicFormPOC.defs.ChatType
import com.common.utils.loginForms.dynamicFormPOC.toAsyncAccount
import com.nanorep.nanoengine.Account

open class BoldChatAsync : History() {

    override val account: Account
        get() = accountData.toAsyncAccount()

    override var chatType = ChatType.Async

}