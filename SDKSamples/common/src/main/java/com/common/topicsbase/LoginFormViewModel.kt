package com.common.topicsbase

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.common.utils.forms.LoginData
import com.common.utils.forms.defs.ChatType
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

        loginData.value = LoginData (

            this@LoginFormViewModel.accountData,

            this@LoginFormViewModel.restoreRequest
        )
    }
}