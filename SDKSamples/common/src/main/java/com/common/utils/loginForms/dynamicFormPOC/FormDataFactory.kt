package com.common.utils.loginForms.dynamicFormPOC

import com.common.utils.loginForms.dynamicFormPOC.AccountFieldFactory.createTextInput
import com.common.utils.loginForms.dynamicFormPOC.defs.ChatType
import com.common.utils.loginForms.dynamicFormPOC.defs.DataKeys
import com.common.utils.loginForms.dynamicFormPOC.defs.FieldProps
import com.common.utils.loginForms.dynamicFormPOC.defs.FieldTypes.TextInput
import com.google.gson.JsonArray
import com.google.gson.JsonObject

object FormDataFactory {

    fun createForm(@ChatType chatType: String): JsonArray {

        return when(chatType) {
            ChatType.Live -> createBotForm()
            ChatType.Async -> createBotForm()
            else -> createBotForm()
        }
    }

    private fun createBotForm() : JsonArray {
        return JsonArray().apply {
            add(createTextInput(DataKeys.Name, "", "Account name", "Account name"))
            add(createTextInput(DataKeys.KB, "", "Knowledge Base", "Knowledge Base"))
            add(createTextInput(DataKeys.Accesskey, "", "Api Key", "Api Key"))
            add(createTextInput(DataKeys.Server, "", "Server", "Server"))
        }
    }

    private fun createAsyncForm() : JsonArray {
        return JsonArray()
    }

    private fun createLiveForm() : JsonArray {
        return JsonArray()
    }
}

object AccountFieldFactory{

    fun createTextInput(name: String, value: String, label: String, hint: String) : JsonObject {
        return JsonObject().apply {
            addProperty(FieldProps.Type, TextInput)
            addProperty(FieldProps.Key, name)
            addProperty(FieldProps.Value, value)
            addProperty(FieldProps.Label, label)
            addProperty(FieldProps.Hint, hint)
        }
    }

}

