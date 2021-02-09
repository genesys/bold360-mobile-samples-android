package com.common.topicsbase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.common.utils.ChatForm.LoginData
import com.google.gson.*

class LoginFormViewModel : ViewModel() {

    private var _loginData: MutableLiveData<LoginData> = MutableLiveData()
    val loginData: LiveData<LoginData> = _loginData

    var chatType: MutableLiveData<String> = MutableLiveData()

    var formData = JsonArray()

    var accountData: JsonObject = JsonObject()

    /**
     * true is the user pressed on the restore button
     */
    var restoreRequest: Boolean = false

    fun onAccountData() {

        _loginData.postValue( LoginData (

            this@LoginFormViewModel.accountData,

            this@LoginFormViewModel.restoreRequest
        ))
    }
}