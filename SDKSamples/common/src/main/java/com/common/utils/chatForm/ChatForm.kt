package com.common.utils.chatForm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.common.topicsbase.SampleFormViewModel
import com.common.utils.chatForm.defs.FieldProps
import com.common.utils.chatForm.defs.FormType
import com.google.gson.Gson
import com.nanorep.sdkcore.utils.children
import com.sdk.common.R
import kotlinx.android.synthetic.main.account_form.*
import kotlinx.android.synthetic.main.context_view.view.*
import java.util.regex.Pattern

class ChatForm : Fragment() {

    private lateinit var formType: String

    private val sampleFormViewModel: SampleFormViewModel by activityViewModels()

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

    private fun createForm() {

        formType = sampleFormViewModel.getFormType()

        sampleFormViewModel.formData.value?.forEach {

            it.asJsonObject?.let { currentField ->

                if (currentField.getString(FieldProps.FormType) != formType) return@let

                FieldViewFactory.createFieldView(currentField, requireContext())?.apply {
                    (this as? ContextBlock)?.apply { initContextBlock(view?.findViewById(R.id.scroller)) }

                }?.let { fieldView ->
                    formFields?.addView(fieldView)
                }
            }
        }

        FieldViewFactory.clear()
    }

    private fun collaborateData() = when (formType) {

        FormType.Account -> collaborateAccountData()
        else -> collaborateRestoreData()

    }

    private fun collaborateRestoreData() {

        formFields?.children()?.forEach { view ->

            when (view) {

                is RadioGroup -> {
                    formFields?.findViewById<RadioButton>(view.checkedRadioButtonId)?.text?.toString()?.let { chatType ->
                        sampleFormViewModel.updateChatType(chatType)
                    }
                }

                is SwitchCompat -> sampleFormViewModel.restoreRequest = view.isChecked

            }
        }
    }

    private fun collaborateAccountData() {

        formFields?.children()?.forEachIndexed { index, view ->

            sampleFormViewModel.getFormField(index)?.run {

                getString( FieldProps.Key )?.let { key ->

                    val value = (

                            when (view) {

                                is EditText -> view.text

                                is RadioGroup -> formFields?.findViewById<RadioButton>(view.checkedRadioButtonId)?.text

                                is Switch -> view.isChecked.toString()

                                is ContextBlock -> Gson().toJson(view.contextHandler.getContext())

                                else -> return@let

                            }.toString())

                    val isRequired = get(FieldProps.Required)?.asBoolean ?: false
                    val validator = getString(FieldProps.Validator)?.toPattern()
                    if (!isValid(index, value, isRequired, validator)) return

                    sampleFormViewModel.addAccountProperty(key, value)
                }
            }
        }

        sampleFormViewModel.onAccountData()
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

    companion object {

        const val TAG = "ChatForm"

        fun newInstance(): ChatForm {
            return ChatForm()
        }
    }
}

