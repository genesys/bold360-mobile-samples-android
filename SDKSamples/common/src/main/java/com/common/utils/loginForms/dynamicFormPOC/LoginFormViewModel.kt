package com.common.utils.loginForms.dynamicFormPOC

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.common.utils.loginForms.DataController
import com.common.utils.loginForms.LoginData
import com.common.utils.loginForms.RestoreState
import com.common.utils.loginForms.dynamicFormPOC.defs.ChatType
import com.google.gson.*

class LoginFormViewModel : ViewModel(), DataController {

    var formFields = JsonArray()

    private var accountId: String = ""

    var accountData: JsonObject = JsonObject()
        set(value) {
            field = value
            updateExtraData()
        }

    val loginData: MutableLiveData<LoginData> = MutableLiveData()

    override var restorable: Boolean = false
    override var restoreRequest: Boolean = false
    override var extraData: MutableMap<String, Any?>? = mutableMapOf()

    override var formsParams = 0

    override var chatType: String = ChatType.None

    private var sharedDataHandler = JsonSharedDataHandler()

    override fun getJsonAccount(context: Context?): JsonObject {
        if (accountData.size() == 0) {
            accountData =
                (context?.let {
                    sharedDataHandler.getSavedAccount(
                        it,
                        chatType.takeIf { type -> type != ChatType.None })
                }.orDefault(chatType))
        }
        return accountData
    }

    private fun updateExtraData() {

        accountData.entrySet().filter {
            it.key == JsonSharedDataHandler.preChat_deptCode_key ||
                    it.key == JsonSharedDataHandler.preChat_lName_key ||
                    it.key == JsonSharedDataHandler.preChat_fName_key
        }.map {
            if (extraData == null) extraData = mutableMapOf()
            extraData!!.toMutableMap().put(it.key, it.value)
        }

    }

    override fun saveAccount(context: Context?) {

        accountId = accountData.getGroupId()

        context?.let {
            sharedDataHandler.saveAccount(it, accountData, chatType)

            restorable = sharedDataHandler.isRestorable(
                it, chatType
            )
        }

    }

    fun onAccountUpdated(accountData: JsonObject? = null) {
        loginData.value = object : LoginData {
            override var account = accountData
            override var extraData = this@LoginFormViewModel.extraData
            override var restoreState = RestoreState(restoreRequest, restorable)
        }
    }
}