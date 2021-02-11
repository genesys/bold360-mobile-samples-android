package com.common.topicsbase

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.common.utils.chatForm.*
import com.common.utils.chatForm.defs.ChatType
import com.common.utils.chatForm.defs.FormType
import com.google.gson.*
import com.nanorep.nanoengine.Account

class SampleFormViewModel(private val app: Application) : AndroidViewModel(app) {

    private val sampleRepository: SampleRepository = JsonSampleRepository()
    private var accountData: JsonObject = JsonObject()

    private var _sampleData: MutableLiveData<LoginData> = MutableLiveData()
    val sampleData: LiveData<LoginData> = _sampleData

    private var _chatType: MutableLiveData<String> = MutableLiveData()
    val chatType: LiveData<String> = _chatType

    private var _formData: MutableLiveData<JsonArray> = MutableLiveData()
    val formData: LiveData<JsonArray> = _formData

    private fun getSavedAccount(): JsonObject {
        return sampleRepository.getSavedAccount(app.applicationContext, chatType.value ?: ChatType.None) as JsonObject
    }

    private fun saveAccount(accountData: JsonObject?) {
        sampleRepository.saveAccount(app.applicationContext, accountData, chatType.value ?: ChatType.None)
    }

    fun isRestorable() : Boolean {
        return sampleRepository.isRestorable(app.applicationContext, chatType.value ?: ChatType.None)
    }

    val account: Account?
        get() = when (chatType.value) {
            ChatType.Live -> accountData.toLiveAccount()
            ChatType.Async -> accountData.toAsyncAccount()
            ChatType.Bot -> accountData.toBotAccount()
            else -> null
        }

    /**
     * true is the user pressed on the restore button
     */
    var restoreRequest: Boolean = false

    fun getFormField(index: Int): JsonObject? {
        return _formData.value?.get(index)?.asJsonObject
    }

    @FormType
    fun getFormType(): String {
        return _formData.value?.remove(0)?.asString ?: FormType.Account
    }

    fun updateChatType(@ChatType chatType: String) {
        _chatType.value = chatType
    }

    fun updateFormData(extraFields: List<FormFieldFactory.FormField>? = null) {
        _formData.value = FormDataFactory.createForm(_chatType.value ?: ChatType.None, extraFields).applyValues( getSavedAccount() )
    }

    fun reset() {
        updateChatType(ChatType.None)
        accountData = JsonObject()
    }

    fun getAccountDataByKey(key: String): String? {
        return accountData.getString(key)
    }

    fun addAccountProperty(key: String, value: String) {
        accountData.addProperty(key, value)
    }

    fun onAccountData() {

        _sampleData.value =  LoginData (

            this@SampleFormViewModel.accountData,

            this@SampleFormViewModel.restoreRequest
        )

        saveAccount( this@SampleFormViewModel.accountData)

    }
}