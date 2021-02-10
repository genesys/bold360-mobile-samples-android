package com.common.utils.chatForm

import com.common.utils.chatForm.defs.ChatType
import com.common.utils.chatForm.defs.DataKeys.*
import com.common.utils.chatForm.defs.FieldTypes
import com.common.utils.chatForm.defs.FormType
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
            add(FormType.Restoration)
            addFormField(FormFieldFactory.TextField(FormType.Restoration, "Choose the Account type"))
            addFormField(FormFieldFactory.ChatTypeOption(ChatType.Bot, true))
            addFormField(FormFieldFactory.ChatTypeOption(ChatType.Live))
            addFormField(FormFieldFactory.ChatTypeOption(ChatType.Async))
        }
    }

    private fun createBotForm(): JsonArray {
        return JsonArray().apply {
            add(FormType.Account)
            addFormField(FormFieldFactory.TextField(FormType.Account, "Account Details"))
            addFormField(FormFieldFactory.TextInputField(FormType.Account, AccountName, "", "Account name", true))
            addFormField(FormFieldFactory.TextInputField(FormType.Account, KB, "", "Knowledge Base", true))
            addFormField(FormFieldFactory.TextInputField(FormType.Account, Accesskey, "", "Api Key", false))
            addFormField(FormFieldFactory.TextInputField(FormType.Account, Server, "", "Server", false))
        }
    }

    private fun createAsyncForm(): JsonArray {
        return JsonArray().apply {
            add(FormType.Account)
            addFormField(FormFieldFactory.TextField(FormType.Account, "Account Details"))
            add(FormFieldFactory.TextInputField(FormType.Account, Accesskey, "", "Access key", false).toJson())
        }
    }

    private fun createLiveForm(): JsonArray {
        return JsonArray().apply {
            add(FormType.Account)
            addFormField(FormFieldFactory.TextField(FormType.Account, "Account Details"))
            add(FormFieldFactory.TextInputField(FormType.Account, Accesskey, "", "Access key", false).toJson())
        }
    }

    fun JsonArray.addFormField(formField: FormFieldFactory.FormField) {
        this.add(formField.toJson())
    }
}

object FormFieldFactory {

    open class FormField ( @FormType val formType: String, @FieldTypes val type: String, val key: String, val value: String ) {

        fun toJson(): JsonObject {
            return JsonParser.parseString(Gson().toJson(this)).asJsonObject
        }

    }

    open class OptionField( @FormType formType: String, text: String, checked: Boolean = false )
        : FormField(formType, FieldTypes.RadioOption, text, checked.toString())

    class ChatTypeOption( @ChatType text: String, checked: Boolean = false )
        : OptionField(FormType.Restoration, text, checked)

    open class SwitchField( @FormType formType: String, key: String, checked: Boolean = false)
        : FormField(FormType.Restoration, FieldTypes.Switch, key, checked.toString())

    class ContextBlock( key: String = Context, value: String = "")
        : FormField(FormType.Account, FieldTypes.ContextBlock, key, value)

    class TextField( @FormType formType: String, value: String)
        : FormField(formType, FieldTypes.Title, FieldTypes.Title, value)

    open class TextInputField( @FormType formType: String, key: String, value: String, val hint: String, val required: Boolean)
        : FormField(formType, FieldTypes.TextInput, key, value)

    open class PatternInputField( @FormType formType: String, key: String, value: String, hint: String, required: Boolean,  val validator: String)
        : TextInputField(formType, key, value, hint, required)

    class EmailInputField( @FormType formType: String, key: String, value: String, hint: String, required: Boolean )
        : FormFieldFactory.PatternInputField(formType, key, value, hint, required, android.util.Patterns.EMAIL_ADDRESS.toString() )

    class PhoneInputField( @FormType formType: String, key: String, value: String, hint: String, required: Boolean)
        : FormFieldFactory.PatternInputField(formType, key, value, hint, required, android.util.Patterns.PHONE.toString() )


}

