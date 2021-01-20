package com.common.utils.loginForms.dynamicFormPOC

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.common.utils.loginForms.dynamicFormPOC.SharedDataHandler.Companion.preChat_deptCode_key
import com.common.utils.loginForms.dynamicFormPOC.SharedDataHandler.Companion.preChat_fName_key
import com.common.utils.loginForms.dynamicFormPOC.SharedDataHandler.Companion.preChat_lName_key
import com.common.utils.loginForms.dynamicFormPOC.defs.ChatType
import com.google.gson.*

class LoginFormViewModel : ViewModel() {

    val loginData: MutableLiveData<LoginData> = MutableLiveData()

    var formFields = JsonArray()

    var accountData: JsonObject = JsonObject()

    /**
     * true is the user pressed on the restore button
     */
    var restoreRequest: Boolean = false

    var chatType: String = ChatType.None

    fun onAccountUpdated() {
        loginData.value = object : LoginData {

            override var account = this@LoginFormViewModel.accountData

            override var extraData = this@LoginFormViewModel.accountData.let {
                JsonObject().apply {
                    accountData.copyTo(preChat_deptCode_key, this)
                    accountData.copyTo(preChat_lName_key, this)
                    accountData.copyTo(preChat_fName_key, this)
                }
            }

            override var restoreRequest = this@LoginFormViewModel.restoreRequest
        }
    }
}