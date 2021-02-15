package com.common.utils.chatForm

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.children
import com.common.utils.chatForm.defs.ChatType
import com.common.utils.chatForm.defs.DataKeys
import com.common.utils.chatForm.defs.FieldProps
import com.common.utils.chatForm.defs.FieldType
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser

object FormDataFactory {

    fun createFormFields(
        @ChatType chatType: String,
        extraFormFields: List<FormFieldFactory.FormField>? = null
    ): JsonArray {

        return (chatType to when(chatType) {
            ChatType.Live -> liveForm
            ChatType.Async -> asyncForm
            ChatType.Bot -> botForm
            else -> restorationForm
        }).let { form ->
            extraFormFields?.filter { it.formType == form.first }?.forEach { form.second.addFormField(it) }
            form.second
        }
    }

    private val restorationForm: JsonArray
    get() = JsonArray().apply {
        addFormField(FormFieldFactory.TextField(ChatType.ChatSelection, "Choose the Account type"))
        addFormField(FormFieldFactory.OptionsField(
            ChatType.ChatSelection, DataKeys.ChatTypeKey, listOf(
                FormFieldFactory.Option(ChatType.ChatSelection, DataKeys.ChatTypeKey, ChatType.Bot),
                FormFieldFactory.Option(ChatType.ChatSelection, DataKeys.ChatTypeKey, ChatType.Live),
                FormFieldFactory.Option(ChatType.ChatSelection, DataKeys.ChatTypeKey, ChatType.Async)
            )
        ))
    }

    private val botForm: JsonArray
    get() = JsonArray().apply {
        addFormField(FormFieldFactory.TextField(ChatType.Bot, "Account Details"))
        addFormField(FormFieldFactory.TextInputField(ChatType.Bot, DataKeys.AccountName, "", "Account name", true))
        addFormField(FormFieldFactory.TextInputField(ChatType.Bot, DataKeys.KB, "", "Knowledge Base", true))
        addFormField(FormFieldFactory.TextInputField(ChatType.Bot, DataKeys.Accesskey, "", "Api Key", false))
        addFormField(FormFieldFactory.TextInputField(ChatType.Bot, DataKeys.Server, "", "Server", false))
    }

    private val liveForm: JsonArray
    get() =  JsonArray().apply {
        addFormField(FormFieldFactory.TextField(ChatType.Live, "Account Details"))
        addFormField(FormFieldFactory.TextInputField(ChatType.Live, DataKeys.Accesskey, "", "Access key", false))
    }

    private val asyncForm: JsonArray
    get() =  JsonArray().apply {
        addFormField(FormFieldFactory.TextField(ChatType.Async, "Account Details"))
        addFormField(FormFieldFactory.TextInputField(ChatType.Async, DataKeys.Accesskey, "", "Access key", false))
        addFormField(FormFieldFactory.TextInputField(ChatType.Async, DataKeys.AppId, "", "Application ID", true))
    }

    private fun JsonArray.addFormField(formField: FormFieldFactory.FormField) {

        try {

            formField.toJson().let { jFormField ->

                if (formField.type == FieldType.Option) {

                    find { // -> Searching for a Options field with the same key in order to add

                        it.asJsonObject.getString(FieldProps.Key) == formField.key
                                && it.asJsonObject.getString(FieldProps.Type) == FieldType.Options

                    }?.asJsonObject?.getAsJsonArray("options")?.add(jFormField)

                        ?: kotlin.run { // -> If not found, it creates a new Options field with the same key and form type

                            addFormField(
                                FormFieldFactory.OptionsField( formField.formType, formField.key,
                                    listOf(formField as FormFieldFactory.Option)
                                )

                            )
                        }

                } else {
                    add(jFormField)
                }
            }

        } catch ( exception : IllegalStateException) {
            // being thrown by the "asJsonObject" casting
            Log.w(ChatForm.TAG, exception.message ?: "Unable to parse field")
        }
    }
}

object FormFieldFactory {

    open class FormField (@ChatType val formType: String, @FieldType val type: String, val key: String, val value: String ) {

        open fun toJson(): JsonObject {
            return JsonParser.parseString(Gson().toJson(this)).asJsonObject
        }

    }

    open class Option( @ChatType formType: String, key: String, text: String)
        : FormField( formType, FieldType.Option, key, text)

    class OptionsField(@ChatType formType: String, key: String, private val options: List<Option>)
        : FormField( formType, FieldType.Options, key, options.toString()) {

        override fun toJson(): JsonObject {
            return JsonParser.parseString(Gson().toJson(this)).asJsonObject
        }
    }

    open class SwitchField( @ChatType formType: String, text: String, checked: Boolean = false)
        : FormField( formType, FieldType.Switch, text, checked.toString())

    open class RestoreSwitch( checked: Boolean = false )
        : SwitchField( ChatType.ChatSelection, DataKeys.Restore, checked)

    class ContextBlock(key: String = DataKeys.Context, value: String = "")
        : FormField( ChatType.Bot, FieldType.ContextBlock, key, value)

    class TextField( @ChatType formType: String, value: String)
        : FormField(formType, FieldType.Title, FieldType.Title, value)

    open class TextInputField( @ChatType formType: String, key: String, value: String, val hint: String, val required: Boolean)
        : FormField(formType, FieldType.TextInput, key, value)

    open class PatternInputField( @ChatType formType: String, key: String, value: String, hint: String, required: Boolean,  val validator: String)
        : TextInputField(formType, key, value, hint, required)

    class EmailInputField( @ChatType formType: String, key: String, value: String, hint: String, required: Boolean )
        : FormFieldFactory.PatternInputField(formType, key, value, hint, required, android.util.Patterns.EMAIL_ADDRESS.toString() )

    class PhoneInputField( @ChatType formType: String, key: String, value: String, hint: String, required: Boolean)
        : FormFieldFactory.PatternInputField(formType, key, value, hint, required, android.util.Patterns.PHONE.toString() )

}

object FieldViewFactory {

    fun titleView(value: String?, context: Context): TextView =
        AppCompatTextView(context).apply {
            text = value ?: ""
            textSize = 22f
            setTextColor(Color.BLUE)
            setPadding(8, 14, 8, 14)
            layoutParams =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.CENTER
                }
        }

    fun inputView(value: String?, hint: String?, context: Context): EditText =

        AppCompatEditText(context).apply {
            setText(value ?: "")
            this.hint = hint ?: ""
        }

    fun switchView(value: String?, key: String?, context: Context): SwitchCompat =

        SwitchCompat(context).apply {
            isChecked = value == "true"
            textSize = 16f
            text = key ?: ""
        }

    fun optionsView(options: JsonArray, context: Context): RadioGroup {

        return RadioGroup(context).apply {
            options.forEach {
                addView(
                    AppCompatRadioButton(context).apply {
                        text  = it.asJsonObject.getString(FieldProps.Value)
                        textSize = 16f
                        id = ViewCompat.generateViewId()
                    }
                )
            }
            (children.first() as RadioButton).isChecked = true
        }
    }
}

