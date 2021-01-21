package com.common.utils.forms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.common.topicsbase.LoginFormViewModel
import com.common.utils.forms.defs.FieldProps
import com.common.utils.forms.defs.FieldTypes
import com.nanorep.sdkcore.utils.children
import com.sdk.common.R
import kotlinx.android.synthetic.main.account_form.*
import java.util.regex.Pattern

class AccountForm : Fragment() {

    private val loginFormViewModel: LoginFormViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.account_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        fillFields()

        view.findViewById<Button>(R.id.start_chat).apply {
            setOnClickListener {
                collaborateData()
            }
        }
    }

    private fun isValid(index: Int, value: String, required: Boolean, validator: Pattern?): Boolean {

        val presentError: ((message: String) -> Unit) = { message ->
            (formFields?.children()?.get(index) as? TextView)?.apply {
                this.requestFocus()
                error = message
            }
        }

        val validatorCheck = {

            validator?.let { // -> If there is a validator, we check that the value passes (empty is valid)

                (value.isEmpty() || validator.matcher(value).matches()).also {
                    if(!it) presentError(getString(R.string.validation_error))
                }

            } ?: true

        }

        val requiredCheck = {
            (!(required && value.isEmpty())).also {
                if (!it) presentError(getString(R.string.required_error))
            }
        }

        return validatorCheck() && requiredCheck()
    }

    private fun collaborateData() {

        formFields?.children()?.forEachIndexed { index, view ->

            loginFormViewModel.formFields[index]?.asJsonObject?.let { fieldData ->

                fieldData.getString(FieldProps.Key)?.let {

                    val (name: String, value: String) = (

                            it to when (view) {
                                is EditText -> view.text.toString()
                                else -> ""
                            })

                    val isRequired = fieldData.get(FieldProps.Required).asBoolean
                    val validator = fieldData.getString(FieldProps.Validator)?.toPattern()
                    if (!isValid(index, value, isRequired, validator) ) return

                    loginFormViewModel.accountData.addProperty(name, value)
                }
            }
        }

        loginFormViewModel.onAccountUpdated()
    }

    private fun fillFields() {

        loginFormViewModel.formFields.forEach {

            it.asJsonObject.let { currentField ->

                formFields?.addView(
                    when (currentField.getString(FieldProps.Type)) {
                        FieldTypes.TextInput -> EditText(context).apply {

                            setText(currentField.getString(FieldProps.Value) ?: "")
                            hint = currentField.getString(FieldProps.Hint) ?: ""
                        }

                        else -> TextView(context)
                    })
            }
        }
    }

    companion object {

        const val TAG = "AccountForm"

        fun newInstance(): AccountForm {
            return AccountForm()
        }
    }
}