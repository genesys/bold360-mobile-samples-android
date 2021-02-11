package com.common.utils.chatForm

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import com.common.utils.chatForm.defs.ChatType
import com.common.utils.chatForm.defs.DataKeys.*
import com.common.utils.chatForm.defs.FieldProps
import com.common.utils.chatForm.defs.FieldType
import com.common.utils.chatForm.defs.FormType
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser

object FormDataFactory {

    fun createForm(
        @ChatType chatType: String,
        extraFormFields: List<FormFieldFactory.FormField>? = null
    ): JsonArray {

        return when (chatType) {
            ChatType.Live -> liveForm
            ChatType.Async -> asyncForm
            ChatType.Bot -> botForm
            else -> restorationForm

        }.apply {
            extraFormFields?.forEach { addFormField(it) }
        }
    }

    private val restorationForm: JsonArray
    get() = JsonArray().apply {
        add(FormType.Restoration)
        addFormField(FormFieldFactory.TextField(FormType.Restoration, "Choose the Account type"))
        addFormField(FormFieldFactory.ChatTypeOption(ChatType.Bot, true))
        addFormField(FormFieldFactory.ChatTypeOption(ChatType.Live))
        addFormField(FormFieldFactory.ChatTypeOption(ChatType.Async))
    }

    private val accountForm: JsonArray
    get() =  JsonArray().apply {
        add(FormType.Account)
        addFormField(FormFieldFactory.TextField(FormType.Account, "Account Details"))
    }

    private val botForm: JsonArray
    get() = accountForm.apply {
        addFormField(FormFieldFactory.TextInputField(FormType.Account, AccountName, "", "Account name", true))
        addFormField(FormFieldFactory.TextInputField(FormType.Account, KB, "", "Knowledge Base", true))
        addFormField(FormFieldFactory.TextInputField(FormType.Account, Accesskey, "", "Api Key", false))
        addFormField(FormFieldFactory.TextInputField(FormType.Account, Server, "", "Server", false))
    }

    private val liveForm: JsonArray
    get() = accountForm.apply {
        add(FormFieldFactory.TextInputField(FormType.Account, Accesskey, "", "Access key", false).toJson())
    }

    private val asyncForm: JsonArray
    get() = liveForm.apply {
        addFormField(FormFieldFactory.TextInputField(FormType.Account, AppId, "", "Application ID", true))
    }

    private fun JsonArray.addFormField(formField: FormFieldFactory.FormField) {
        this.add(formField.toJson())
    }
}

object FormFieldFactory {

    open class FormField ( @FormType val formType: String, @FieldType val type: String, val key: String, val value: String ) {

        fun toJson(): JsonObject {
            return JsonParser.parseString(Gson().toJson(this)).asJsonObject
        }

    }

    open class OptionField( @FormType formType: String, text: String, checked: Boolean = false )
        : FormField(formType, FieldType.RadioOption, text, checked.toString())

    class ChatTypeOption( @ChatType text: String, checked: Boolean = false )
        : OptionField(FormType.Restoration, text, checked)

    open class SwitchField( @FormType formType: String, key: String, checked: Boolean = false)
        : FormField(FormType.Restoration, FieldType.Switch, key, checked.toString())

    class ContextBlock( key: String = Context, value: String = "")
        : FormField(FormType.Account, FieldType.ContextBlock, key, value)

    class TextField( @FormType formType: String, value: String)
        : FormField(formType, FieldType.Title, FieldType.Title, value)

    open class TextInputField( @FormType formType: String, key: String, value: String, val hint: String, val required: Boolean)
        : FormField(formType, FieldType.TextInput, key, value)

    open class PatternInputField( @FormType formType: String, key: String, value: String, hint: String, required: Boolean,  val validator: String)
        : TextInputField(formType, key, value, hint, required)

    class EmailInputField( @FormType formType: String, key: String, value: String, hint: String, required: Boolean )
        : FormFieldFactory.PatternInputField(formType, key, value, hint, required, android.util.Patterns.EMAIL_ADDRESS.toString() )

    class PhoneInputField( @FormType formType: String, key: String, value: String, hint: String, required: Boolean)
        : FormFieldFactory.PatternInputField(formType, key, value, hint, required, android.util.Patterns.PHONE.toString() )

}

internal object FieldViewFactory {

    private var radioGroup: RadioGroup? = null

    fun clear() {
        radioGroup = null
    }

    fun createFieldView(fieldData: JsonObject, context: Context): View? {

        return when (fieldData.getString(FieldProps.Type)) {
            FieldType.ContextBlock -> ContextBlock(context)

            FieldType.Title -> titleView( fieldData.getString(FieldProps.Value), context )

            FieldType.TextInput -> inputView( fieldData.getString(FieldProps.Value), fieldData.getString(FieldProps.Hint), context )

            FieldType.Switch -> switchView( fieldData.getString(FieldProps.Value), fieldData.getString(FieldProps.Key), context )

            FieldType.RadioOption -> {

                val group = radioGroup ?: RadioGroup(context)

                group.addView(

                    AppCompatRadioButton(context).apply {
                        isChecked = fieldData.getString(FieldProps.Value) == "true"
                        textSize = 16f
                        text = fieldData.getString(FieldProps.Key) ?: ""
                        id = ViewCompat.generateViewId()
                    }

                )

                if (radioGroup == null) {
                    radioGroup = group
                    radioGroup
                } else {
                    null
                }
            }

            else -> null

        }?.apply {
            id = ViewCompat.generateViewId()
        }
    }

    private fun titleView(value: String?, context: Context): TextView =
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

    private fun inputView(value: String?, hint: String?, context: Context): EditText =

        AppCompatEditText(context).apply {
            setText(value ?: "")
            this.hint = hint ?: ""
        }

    private fun switchView(value: String?, key: String?, context: Context): SwitchCompat =

        SwitchCompat(context).apply {
            isChecked = value == "true"
            textSize = 16f
            text = key ?: ""
        }
}

