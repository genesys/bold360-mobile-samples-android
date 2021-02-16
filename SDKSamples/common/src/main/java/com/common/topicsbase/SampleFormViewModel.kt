package com.common.topicsbase

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.common.utils.chatForm.*
import com.common.utils.chatForm.defs.ChatType
import com.common.utils.chatForm.defs.DataKeys
import com.common.utils.toObject
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.nanorep.nanoengine.Account

class SampleFormViewModel(app: Application) : AndroidViewModel(app) {

    private var sampleRepository: SampleRepository? = null

    private val accountData: JsonObject?
    get() = _sampleData.value?.account

    private var _sampleData: MutableLiveData<SampleData> = MutableLiveData()
    val sampleData: LiveData<SampleData> = _sampleData

    private var _chatType: MutableLiveData<String> = MutableLiveData()
    val chatType: LiveData<String> = _chatType

    private var _formData: MutableLiveData<JsonArray> = MutableLiveData()
    val formData: LiveData<JsonArray> = _formData

    private fun getSavedAccount(): Any? {
        return sampleRepository?.getSavedAccount( chatType.value ?: ChatType.ChatSelection)
    }

    private fun saveAccount(accountData: Any) {
        sampleRepository?.saveAccount( accountData, chatType.value ?: ChatType.ChatSelection)
    }

    fun isRestorable() : Boolean {
        return sampleRepository?.isRestorable( chatType.value ?: ChatType.ChatSelection) ?: false
    }

    val account: Account?
        get() = when (chatType.value) {
            ChatType.Live -> accountData?.toLiveAccount()
            ChatType.Async -> accountData?.toAsyncAccount()
            ChatType.Bot -> accountData?.toBotAccount()
            else -> null
        }

    /**
     * true is the user pressed on the restore button
     */
    var restoreRequest: Boolean = false

    fun setRepository(sampleRepository: SampleRepository) {
        this.sampleRepository = sampleRepository
    }

    fun getFormField(index: Int): JsonObject? {
       return _formData.value?.takeIf { it.size() > 0 }?.get(index)?.toObject()
    }

    fun updateChatType(@ChatType chatType: String) {
        _chatType.value = chatType
    }

    fun createFormFields(extraFields: List<FormFieldFactory.FormField>? = null) {
        _formData.value = FormDataFactory.createFormFields(_chatType.value ?: ChatType.ChatSelection, extraFields)
            .applyValues(getSavedAccount() as? JsonObject)
    }

    fun getAccountDataByKey(key: String): String? {
        return accountData?.getString(key)
    }

    fun onAccountData(accountData: JsonObject) {

        accountData.remove(DataKeys.ChatTypeKey)?.asString?.let { _chatType.value = it }
        accountData.remove(DataKeys.Restore)?.asBoolean?.let { restoreRequest = it }

        accountData.takeIf { it.size() > 0 }?.let {
            _sampleData.value =  SampleData ( accountData, restoreRequest )
            saveAccount(accountData)
        }

    }
}