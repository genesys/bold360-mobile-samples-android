package com.common.utils.loginForms.dynamicFormPOC

import com.common.utils.loginForms.dynamicFormPOC.AccountFieldFactory.createTextInput
import com.common.utils.loginForms.dynamicFormPOC.defs.ChatType
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
            add(createTextInput("account", "", "Account name", "Account name"))
            add(createTextInput("kb", "", "Knowledge Base", "Knowledge Base"))
            add(createTextInput("apiKey", "", "Api Key", "Api Key"))
            add(createTextInput("domain", "", "Server", "Server"))
        }
    }

    private fun createAsyncForm() : JsonArray {
        return JsonArray()
    }

    private fun createLiveForm() : JsonArray {
        return JsonArray()
    }
/*

    "[{\"type\":2,\"name\":\"account\",\"value\":\"\",\"label\":\"Account name\",\"hint\":\"Account name\"}," +
    "{\"type\":2,\"name\":\"kb\",\"value\":\"\",\"label\":\"Knowledge Base\",\"hint\":\"Account name\"}," +
    "{\"type\":2,\"name\":\"apiKey\",\"value\":\"\",\"label\":\"Api Key\",\"hint\":\"Account name\"}," +
    "{\"type\":2,\"name\":\"domain\",\"value\":\"\",\"label\":\"Server\",\"hint\":\"Account name\"}]"

*/

}

object AccountFieldFactory{

    fun createTextInput(name: String, value: String, label: String, hint: String) : JsonObject {
        return JsonObject().apply {
            addProperty("type", TextInput)
            addProperty("key", name)
            addProperty("value", value)
            addProperty("label", label)
            addProperty("hint", hint)
        }
    }

}

