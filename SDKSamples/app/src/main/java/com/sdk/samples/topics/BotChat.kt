package com.sdk.samples.topics

import com.common.chatComponents.customProviders.withId
import com.common.topicsbase.BasicChat
import com.common.utils.loginForms.dynamicFormPOC.FormDataFactory
import com.common.utils.loginForms.dynamicFormPOC.defs.ChatType
import com.common.utils.loginForms.dynamicFormPOC.toBotAccount
import com.google.gson.JsonArray
import com.nanorep.nanoengine.Account
import com.nanorep.sdkcore.utils.toast

open class BotChat : BasicChat() {

    override fun validateData(): Boolean {

        accountData.entrySet().forEachIndexed { index, field ->

            when (field.key) {
                "account" -> if (field.value?.asString.isNullOrEmpty()) kotlin.run {
                    onInvalidAccount.invoke(index, "Account name cannot be empty...")
                    return false
                }
            }
        }

        return super.validateData()
    }

    override val formFields: JsonArray
        get() = FormDataFactory.createForm(ChatType.Bot)

    override fun getAccount(): Account = accountData.toBotAccount().withId(this)

    override fun onUploadFileRequest() {
        toast(this@BotChat, "The file upload action is not available for this sample.")
    }

}