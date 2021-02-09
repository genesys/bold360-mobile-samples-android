package com.common.utils.chat_form

import com.common.utils.chat_form.defs.ChatType
import com.common.utils.chat_form.defs.DataKeys.*
import com.common.utils.chat_form.defs.FieldTypes
import com.common.utils.chat_form.defs.FormType
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser

object FormDataFactory {

    fun createForm(@ChatType chatType: String): JsonArray {

        return when (chatType) {
            ChatType.Live -> createLiveForm()
            ChatType.Async -> createAsyncForm()
            ChatType.Bot -> createBotForm()
            ChatType.None -> createRestorationForm()
            else -> JsonArray()
        }
    }

    private fun createRestorationForm() : JsonArray {
        return JsonArray().apply {
            add(JsonObject().apply { addProperty("FormType", FormType.Restoration) })
            addFormField(FormFieldFactory.TextField("Choose the Account type"))
            addFormField(FormFieldFactory.ChatTypeOption(ChatType.Bot, true))
            addFormField(FormFieldFactory.ChatTypeOption(ChatType.Live))
            addFormField(FormFieldFactory.ChatTypeOption(ChatType.Async))
        }
    }

    private fun createBotForm(): JsonArray {
        return JsonArray().apply {
            add(JsonObject().apply { addProperty("FormType", FormType.Account) })
            addFormField(FormFieldFactory.TextField("Account Details"))
            addFormField(FormFieldFactory.TextInputField(AccountName, "", "Account name", true))
            addFormField(FormFieldFactory.TextInputField(KB, "", "Knowledge Base", true))
            addFormField(FormFieldFactory.TextInputField(Accesskey, "", "Api Key", false))
            addFormField(FormFieldFactory.TextInputField(Server, "", "Server", false))
        }
    }

    private fun createAsyncForm(): JsonArray {
        return JsonArray().apply {
            add(JsonObject().apply { addProperty("FormType", FormType.Account) })
            addFormField(FormFieldFactory.TextField("Account Details"))
            add(FormFieldFactory.TextInputField(Accesskey, "", "Access key", false).toJson())
        }
    }

    private fun createLiveForm(): JsonArray {
        return JsonArray().apply {
            add(JsonObject().apply { addProperty("FormType", FormType.Account) })
            addFormField(FormFieldFactory.TextField("Account Details"))
            add(FormFieldFactory.TextInputField(Accesskey, "", "Access key", false).toJson())
        }
    }

    fun JsonArray.addFormField(formField: FormFieldFactory.FormField) {
        this.add(formField.toJson())
    }
}

object FormFieldFactory {

    open class FormField (@FieldTypes val type: String, val key: String, val value: String ) {

        fun toJson(): JsonObject {
            return JsonParser.parseString(Gson().toJson(this)).asJsonObject
        }

    }

    open class OptionField( text: String, checked: Boolean = false )
        : FormField(FieldTypes.RadioOption, text, checked.toString())

    class ChatTypeOption(@ChatType text: String, checked: Boolean = false )
        : OptionField(text, checked)

    open class SwitchField(key: String, checked: Boolean = false)
        : FormField(FieldTypes.Switch, key, checked.toString())

    class ContextBlock(key: String = Context, value: String = "")
        : FormField(FieldTypes.ContextBlock, key, value)

    class TextField(value: String)
        : FormField(FieldTypes.Title, FieldTypes.Title, value)

    open class TextInputField(key: String, value: String,  val hint: String,  val required: Boolean)
        : FormField(FieldTypes.TextInput, key, value)

    open class PatternInputField(key: String, value: String, hint: String, required: Boolean,  val validator: String)
        : TextInputField(key, value, hint, required)

    class EmailInputField( key: String, value: String, hint: String, required: Boolean )
        : FormFieldFactory.PatternInputField(key, value, hint, required, android.util.Patterns.EMAIL_ADDRESS.toString() )

    class PhoneInputField( key: String, value: String, hint: String, required: Boolean)
        : FormFieldFactory.PatternInputField(key, value, hint, required, android.util.Patterns.PHONE.toString() )


}

