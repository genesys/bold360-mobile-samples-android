package com.common.utils.loginForms.dynamicFormPOC

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.common.utils.loginForms.dynamicFormPOC.defs.FieldProps
import com.common.utils.loginForms.dynamicFormPOC.defs.FieldTypes
import com.nanorep.sdkcore.utils.children
import com.sdk.common.R
import kotlinx.android.synthetic.main.account_form.*

class DynamicAccountForm : Fragment() {

    private val loginFormViewModel: LoginFormViewModel by activityViewModels()

    var validationFailed: ((index: Int, message: String) -> Unit) = { index, message ->
        (formFields?.children()?.get(index) as? EditText)?.apply {
            this.requestFocus()
            error = message
        }
    }

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
                loginFormViewModel.onAccountUpdated()
            }
        }
    }

    private fun collaborateData() {

        formFields?.children()?.forEachIndexed { index, view ->

            loginFormViewModel.formFields[index].asJsonObject.getString(FieldProps.Key)?.let {

                val (name: String, value: String) = (

                        it to when (view) {
                            is EditText -> view.text.toString()
                            else -> ""
                        })

                loginFormViewModel.accountData.addProperty(name, value)
            }
        }
    }

    private fun fillFields() {

        val jsonAccount = loginFormViewModel.accountData

        loginFormViewModel.formFields.forEach {

            it.asJsonObject.let { currentField ->

                formFields?.apply {

                    addView(

                        when (currentField.getString(FieldProps.Type)) {
                            FieldTypes.TextInput -> EditText(context)
                            else -> TextView(context)

                        }.apply {

                            hint = currentField.getString(FieldProps.Hint)
                            (jsonAccount.getString(currentField.getString(FieldProps.Key)))?.let { value ->
                                currentField.addProperty(FieldProps.Value, value)
                                text = value
                            }

                        }
                    )
                }
            }
        }
    }

    companion object {

        fun newInstance(): DynamicAccountForm {
            return DynamicAccountForm()
        }
    }
}