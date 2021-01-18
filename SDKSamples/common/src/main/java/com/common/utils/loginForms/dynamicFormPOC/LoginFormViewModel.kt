package com.common.utils.loginForms.dynamicFormPOC

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.common.utils.loginForms.DataController
import com.common.utils.loginForms.LoginData
import com.common.utils.loginForms.RestoreState
import com.common.utils.loginForms.dynamicFormPOC.defs.ChatType
import com.google.gson.*
import com.nanorep.nanoengine.Account

class LoginFormViewModel : ViewModel(), DataController {

    private var formFields = ""

    fun setFormFields(formFields: String) {
        this.formFields = formFields
    }

    internal fun getFormFields() : JsonArray {
        return try {
            JsonParser.parseString(formFields).asJsonArray
        } catch (e: JsonSyntaxException) {
            JsonArray()
        }
    }

    private var accountId: String = ""

    var accountData: JsonObject? = null

    val loginData: MutableLiveData<LoginData> = MutableLiveData()

    override var restorable: Boolean = false
    override var restoreRequest: Boolean = false
    override var extraData: Map<String, Any?>? = null

    override var formsParams = 0

    override var chatType: String = ChatType.None

    private var sharedDataHandler = JsonSharedDataHandler()

    override fun getJsonAccount(context: Context?): JsonObject? {
        if (accountData == null) {
            accountData = (context?.let { sharedDataHandler.getAccount(it, accountId) }.orDefault(chatType))
        }
        return accountData
    }

    override fun updateGenericAccount(context: Context?, accountData: JsonObject, extraData: Map<String, Any?>?) {

        this.accountData = accountData
        accountId = accountData.getGroupId()

        this.extraData = extraData

        context?.let {
            restorable = sharedDataHandler.isRestorable(
                it, accountId)

            sharedDataHandler.saveAccount(it, accountData.toNeededInfo(chatType), accountId)
        }
    }

    fun onStartChat(account: Account?) {
        loginData.value = object : LoginData {
            override var account = account
            override var extraData = this@LoginFormViewModel.extraData
            override var restoreState = RestoreState(restoreRequest, restorable)
        }
    }
}