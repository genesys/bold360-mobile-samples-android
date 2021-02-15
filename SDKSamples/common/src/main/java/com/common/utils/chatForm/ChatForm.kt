package com.common.utils.chatForm

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.common.topicsbase.SampleFormViewModel
import com.common.utils.chatForm.defs.FieldProps
import com.common.utils.chatForm.defs.FieldType
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.nanorep.sdkcore.utils.children
import com.nanorep.sdkcore.utils.dp
import com.sdk.common.R
import kotlinx.android.synthetic.main.account_form.*
import kotlinx.android.synthetic.main.account_form.view.*
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

        sampleFormViewModel.formData.value?.forEach {

            try {

                formFieldsContainer.addFormField(it.asJsonObject)

            } catch ( exception : IllegalStateException) {
                // being thrown by the "asJsonObject" casting
                Log.w(TAG, exception.message ?: "Unable to parse field")
            }

        }

    }

    private fun collaborateData() {

        val accountData = JsonObject()

        formFieldsContainer.getFormFields().forEachIndexed { index, view ->

            sampleFormViewModel.getFormField(index)?.run {

                when (view) {

                    is EditText -> getString(FieldProps.Key) to view.text.toString()

                    is RadioGroup -> getString(FieldProps.Key) to formFieldsContainer.getCheckedRadioText(view)

                    is SwitchCompat -> getString(FieldProps.Key) to view.isChecked.toString()

                    is ContextBlock -> getString(FieldProps.Key) to Gson().toJson(view.contextHandler.getContext()).toString()

                    else -> null

                }?.let {

                    val isRequired = get(FieldProps.Required)?.asBoolean ?: false
                    val validator = getString(FieldProps.Validator)?.toPattern()
                    if (!isValid(index, it.second, isRequired, validator)) return

                    accountData.addProperty(it.first, it.second)
                }
            }
        }

        sampleFormViewModel.onAccountData(accountData)

    }

    private fun isValid(index: Int, value: String?, required: Boolean, validator: Pattern?): Boolean {

        val presentError: ((message: String) -> Unit) = { message ->
            (formFieldsContainer.getFormFields()[index] as? TextView)?.apply {
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

class FormFieldsContainer @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ScrollView(context, attrs, defStyleAttr) {

    private var formFields: LinearLayout

    init {
        setPadding(8.dp, 8.dp, 8.dp, 8.dp)

        formFields = LinearLayout(context)
        addView(
            formFields.apply {
                layoutParams = LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                orientation = LinearLayout.VERTICAL
                id = ViewCompat.generateViewId()
            }
        )
    }

    fun getFormFields(): List<View> = formFields.children()

    fun getCheckedRadioText(radioGroup: RadioGroup): String? {
        return formFields.findViewById<RadioButton>(radioGroup.checkedRadioButtonId).text?.toString()
    }

    fun addFormField(fieldData: JsonObject) {

        when (fieldData.getString(FieldProps.Type)) {

            FieldType.Options -> FieldViewFactory.optionsView(
                fieldData.getAsJsonArray("options"),
                context
            )

            FieldType.ContextBlock -> ContextBlock(context).apply {
                initContextBlock(this@FormFieldsContainer)
            }

            FieldType.Title -> FieldViewFactory.titleView(
                fieldData.getString(FieldProps.Value),
                context
            )

            FieldType.TextInput -> FieldViewFactory.inputView(
                fieldData.getString(FieldProps.Value),
                fieldData.getString(FieldProps.Hint),
                context
            )

            FieldType.Switch -> FieldViewFactory.switchView(
                fieldData.getString(FieldProps.Value),
                fieldData.getString(FieldProps.Key),
                context
            )

            else -> null
        }?.let { view -> this.formFields.addView(
            view.apply { id = ViewCompat.generateViewId() })
        }
    }
}
