package com.sdk.samples.topics

import com.common.topicsbase.BasicChat
import com.common.utils.loginForms.dynamicFormPOC.defs.ChatType
import com.common.utils.loginForms.dynamicFormPOC.toLiveAccount
import com.nanorep.nanoengine.Account

open class BoldChat : BasicChat() {

    override val account: Account
        get() = accountData.toLiveAccount()

   /* override fun getAccount_old(): Account {
        return account
    }*/

    override var chatType = ChatType.Live

}