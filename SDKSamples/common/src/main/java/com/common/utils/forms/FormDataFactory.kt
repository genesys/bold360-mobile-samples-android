package com.common.utils.forms

import com.common.utils.forms.defs.ChatType
import com.common.utils.forms.defs.DataKeys
import com.common.utils.forms.defs.FieldTypes
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser

object FormDataFactory {

    fun createForm(@ChatType chatType: String): JsonArray {

        return when (chatType) {
            ChatType.Live -> createBotForm()
            ChatType.Async -> createBotForm()
            else -> createBotForm()
        }
    }

    private fun createBotForm(): JsonArray {
        return JsonArray().apply {
            add(FormFieldFactory.TextInputField(DataKeys.Name, "", "Account name", true).toJson())
            add(FormFieldFactory.TextInputField(DataKeys.KB, "", "Knowledge Base", true).toJson())
            add(FormFieldFactory.TextInputField(DataKeys.Accesskey, "", "Api Key", false).toJson())
            add(FormFieldFactory.TextInputField(DataKeys.Server, "", "Server", false).toJson())
        }
    }

    private fun createAsyncForm(): JsonArray {
        return JsonArray()
    }

    private fun createLiveForm(): JsonArray {
        return JsonArray()
    }
}

object FormFieldFactory {

    open class FormField (@FieldTypes val type: String, val key: String, val value: String ) {

        fun toJson(): JsonObject {
            return JsonParser.parseString(Gson().toJson(this)).asJsonObject
        }

    }

    class TextField(key: String, value: String)
        : FormField(FieldTypes.Title, key, value)

    open class TextInputField(key: String, value: String,  val hint: String,  val required: Boolean)
        : FormField(FieldTypes.TextInput, key, value)

    open class PatternInputField(key: String, value: String, hint: String, required: Boolean,  val validator: String)
        : TextInputField(key, value, hint, required)

    class EmailInputField( key: String, value: String, hint: String, required: Boolean)
        : FormFieldFactory.PatternInputField(key, value, hint, required, android.util.Patterns.EMAIL_ADDRESS.toString() )

    class PhoneInputField( key: String, value: String, hint: String, required: Boolean)
        : FormFieldFactory.PatternInputField(key, value, hint, required, android.util.Patterns.PHONE.toString() )


}

