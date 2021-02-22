package com.common.topicsbase

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.common.utils.chatForm.ChatForm
import com.common.utils.chatForm.FormDataFactory
import com.common.utils.chatForm.FormFieldFactory
import com.common.utils.chatForm.SampleData
import com.common.utils.chatForm.SampleRepository
import com.common.utils.chatForm.applyValues
import com.common.utils.chatForm.defs.ChatType
import com.common.utils.chatForm.defs.DataKeys
import com.common.utils.chatForm.getString
import com.common.utils.chatForm.toAsyncAccount
import com.common.utils.chatForm.toBotAccount
import com.common.utils.chatForm.toLiveAccount
import com.common.utils.chatForm.toObject
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.nanorep.nanoengine.Account

@Suppress("UNCHECKED_CAST")
class SampleViewModelFactory (
    private val sampleRepository: SampleRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        (SampleFormViewModel( sampleRepository) as T )
}

class SampleFormViewModel( private val sampleRepository: SampleRepository ) : ViewModel(), SampleRepository by sampleRepository{

    private val accountData: JsonObject?
    get() = _sampleData.value?.account

    private var _sampleData: MutableLiveData<SampleData> = MutableLiveData()
    val sampleData: LiveData<SampleData> = _sampleData

    private var _chatType: MutableLiveData<String> = MutableLiveData()
    val chatType: LiveData<String> = _chatType

    private var _formData: MutableLiveData<JsonArray> = MutableLiveData()
    val formData: LiveData<JsonArray> = _formData

    /**
     * @returns true if the account found by the repository
     */
    fun checkRestorable() : Boolean {
        return sampleRepository.isRestorable(getChatType())
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

    fun getAccountDataByKey(key: String): String? {
        return accountData?.getString(key)
    }

    fun getFormField(index: Int): JsonObject? {
       return _formData.value?.takeIf { it.size() > 0 }?.get(index)?.toObject()
    }

    fun updateChatType(@ChatType chatType: String) {
        _chatType.value = chatType
    }

    private fun getChatType() = chatType.value ?: ChatType.ChatSelection

    fun createFormFields(extraFields: List<FormFieldFactory.FormField>? = null) {
            _formData.value = FormDataFactory.createFormFields(getChatType(), extraFields).applyValues(getSavedAccount(getChatType()) as? JsonObject)
    }

    fun onAccountData(accountData: JsonObject) {

        try {
            accountData.remove(DataKeys.ChatTypeKey)?.asString?.let { _chatType.value = it }
            accountData.remove(DataKeys.Restore)?.asBoolean?.let { restoreRequest = it }
        } catch ( exception : IllegalStateException) {
           // being thrown by the 'JsonElement' casting
           Log.w(ChatForm.TAG, exception.message ?: "Unable to parse field")
        }


        accountData.takeIf { it.size() > 0 }?.let {
            _sampleData.value =  SampleData ( accountData, restoreRequest )
            saveAccount(accountData, getChatType())
        }
    }
}