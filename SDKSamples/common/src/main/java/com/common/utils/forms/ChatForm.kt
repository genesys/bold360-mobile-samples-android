package com.common.utils.forms

import android.graphics.Color
import android.os.Bundle
import android.provider.Telephony
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.*
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat.generateViewId
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.common.topicsbase.LoginFormViewModel
import com.common.utils.forms.defs.FieldProps
import com.common.utils.forms.defs.FieldTypes
import com.common.utils.forms.defs.FormType
import com.common.utils.forms.fields.ContextBlock
import com.nanorep.sdkcore.utils.children
import com.sdk.common.R
import kotlinx.android.synthetic.main.account_form.*
import kotlinx.android.synthetic.main.context_view.view.*
import java.util.regex.Pattern

class ChatForm : Fragment() {

    private lateinit var formType: String

    private val loginFormViewModel: LoginFormViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.account_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        createForm()

        view.findViewById<Button>(R.id.start_chat).apply {
            setOnClickListener {
                collaborateData()
            }
        }
    }

    private fun isValid(index: Int, value: String?, required: Boolean, validator: Pattern?): Boolean {

        val presentError: ((message: String) -> Unit) = { message ->
            (formFields?.children()?.get(index) as? TextView)?.apply {
                this.requestFocus()
                error = message
            }
        }

        val validatorCheck = {

            validator?.let { // -> If there is a validator, we check that the value passes (empty is valid)

                (value.isNullOrEmpty() || validator.matcher(value).matches()).also {
                    if(!it) presentError(getString(R.string.validation_error))
                }

            } ?: true

        }

        val requiredCheck = {
            (!(required && value.isNullOrEmpty())).also {
                if (!it) presentError(getString(R.string.required_error))
            }
        }

        return validatorCheck() && requiredCheck()
    }

    private fun collaborateData() = when (formType) {

        FormType.Account -> collaborateAccountData()
        else -> collaborateRestoreData()

    }

    private fun collaborateRestoreData() {

        formFields?.children()?.forEach { view ->

            when (view) {

                is RadioGroup -> loginFormViewModel.chatType.value = formFields?.findViewById<RadioButton>(view.checkedRadioButtonId)?.text?.toString()

                is SwitchCompat -> loginFormViewModel.restoreRequest = view.isChecked

            }
        }
    }

    private fun collaborateAccountData() {

        formFields?.children()?.forEachIndexed { index, view ->

            loginFormViewModel.formData[index+1]?.asJsonObject?.let { fieldData ->

                fieldData.getString( FieldProps.Key )?.run {

                    val value = (

                            when (view) {

                                is EditText -> view.text

                                is RadioGroup -> formFields?.findViewById<RadioButton>(view.checkedRadioButtonId)?.text?.toString()

                                is SwitchCompat -> view.isChecked.toString()

                                is ContextBlock -> view.contextHandler.getContext().toString() -> Add Context Validation + submission

                                else -> return@run

                            }.toString())

                    val isRequired = fieldData.get(FieldProps.Required)?.asBoolean ?: false
                    val validator = fieldData.getString(FieldProps.Validator)?.toPattern()
                    if (!isValid(index, value, isRequired, validator)) return

                    loginFormViewModel.accountData.addProperty(this, value)
                }
            }
        }

        loginFormViewModel.onAccountData()
    }

    private fun createForm() {

        val radioOptions = mutableListOf<RadioButton>()

        loginFormViewModel.formData.forEach {

            it.asJsonObject?.let { currentField ->

                when (currentField.getString(FieldProps.Type)) {

                    FieldTypes.Title -> {
                        formFields?.addView(AppCompatTextView(context).apply {
                            text = currentField.getString(FieldProps.Value) ?: ""
                            textSize = 22f
                            setTextColor(Color.BLUE)
                            setPadding(8, 14, 8, 14)
                            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                                gravity = Gravity.CENTER
                            }
                        })
                    }

                    FieldTypes.RadioOption -> {
                        radioOptions.add(AppCompatRadioButton(context).apply {
                            isChecked = currentField.getString(FieldProps.Value) == "true"
                            currentField.getString(FieldProps.Key)?.let { radioText ->
                                textSize = 16f
                                text = radioText
                                id = generateViewId()
                            }
                        })
                    }

                    FieldTypes.Switch -> {
                        formFields?.addView(SwitchCompat(context).apply {
                            isChecked = currentField.getString(FieldProps.Value) == "true"
                            currentField.getString(FieldProps.Key)?.let { switchText ->
                                textSize = 16f
                                text = switchText
                                id = generateViewId()
                            }
                        })
                    }

                    FieldTypes.TextInput -> {
                        formFields?.addView(AppCompatEditText(context).apply {
                            setText(currentField.getString(FieldProps.Value) ?: "")
                            hint = currentField.getString(FieldProps.Hint) ?: ""
                        })
                    }

                    FieldTypes.ContextView -> context?.let { context ->
                        formFields?.addView(
                            ContextBlock(context).apply { initContextBlock(formFields, view?.findViewById(R.id.scroller)) }
                        )
                    }

                    else -> formType = currentField.get("FormType").asString
                }
            }
        }

        radioOptions.takeIf { radioOptions.isNotEmpty() }?.let { options ->
            RadioGroup(context).apply {
                options.forEach { radio ->
                    addView(radio)
                }
            }.also { group ->
                formFields?.addView(group)
                radioOptions.clear()
            }
        }

    }

    companion object {

        const val TAG = "ChatForm"

        fun newInstance(): ChatForm {
            return ChatForm()
        }
    }
}

