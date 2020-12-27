package com.common.utils

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.integration.bold.boldchat.visitor.api.FormField
import com.integration.bold.boldchat.visitor.api.FormFieldType
import com.integration.core.StateEvent
import com.nanorep.convesationui.bold.ui.*
import com.nanorep.convesationui.bold.ui.ChatForm.Companion.LanguageFieldKey
import com.nanorep.convesationui.bold.ui.boldFormComponents.SelectionView
import com.nanorep.convesationui.structure.setStyleConfig
import com.nanorep.nanoengine.model.configuration.StyleConfig
import com.nanorep.sdkcore.utils.TextTagHandler
import com.nanorep.sdkcore.utils.forEachChild
import com.nanorep.sdkcore.utils.px
import kotlinx.android.synthetic.main.custom_live_forms_layout.*
import nanorep.com.common.R

/**
 * Custom form implementation to be displayed instead of the SDKs provided forms
 */
class CustomForm : Fragment() {

    private var isSubmitted = false

    companion object {
        @JvmStatic
        fun create(): Fragment {
            return CustomForm()
        }
    }

    //-> fetch ViewModel instance to get the form arguments (as provided on [BoldCustomChatForm])
    private val formViewModel by lazy {
        ViewModelProvider(activity!!).get(ChatFormViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.custom_live_forms_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        //-> Construct the form according to supplied fields and data:
        initFormTypeTitle()
        createBrandedFields()
        initSubmitButton()

        //-> Sets an observer to FormData changes
        formViewModel.observeFormData(this, Observer { data ->
            Log.i("ChatForm", "Got form data update")
            data?.let {
                // Save current user filled data into the FormData, before we remove the views.
                saveValues()

                // We're taking the easy way, and just removing all fields views and recreating them
                // according to the updated FormData. That will cover branding changes and fields changes.
                form_fields_container.removeAllViews()
                createBrandedFields()
            }
        })
    }

    private fun saveValues() {
        form_fields_container.forEachChild { child ->
            (child.tag as? Int)?.let {
                formViewModel.data?.fields?.get(it)?.value = (child as? FormComponent)?.getData() ?: let {
                    (child as? EditText)?.text.toString()
                }
            }
        }
    }

    private fun createBrandedFields() {
        appendIntroText()
        appendFormFields()
    }

    //////////////////////////////////////////////////////////
    //<editor-fold desc=">>>>> Form fields creation and handling <<<<<">

    private fun initFormTypeTitle() {
        formViewModel.data?.formType?.let { formType ->
            form_type_title.apply {
                setStyleConfig(StyleConfig(20, ContextCompat.getColor(context, R.color.colorPrimary),
                        Typeface.DEFAULT_BOLD))
                text = formType
            }
        }
    }

    private fun appendIntroText() {
        formViewModel.data?.getIntroMessage()?.takeIf { it.isNotEmpty() }?.let { introMessage ->

            val introTxt = TextView(context).apply {
                setStyleConfig(StyleConfig(16, Color.DKGRAY, Typeface.DEFAULT_BOLD))
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT)
                (layoutParams as? ViewGroup.MarginLayoutParams)?.setMargins(0, 0, 0, 10.px)
                text = TextTagHandler.getSpannedHtml(introMessage)
            }
            form_fields_container.addView(introTxt)
        }
    }

    private fun appendFormFields() {
        formViewModel.data?.let {

            it.fields?.takeIf { context != null }?.forEachIndexed { index, fieldData ->

                // Update with branded text
                it.strings[fieldData.labelBrandingKey]?.run { fieldData.label = this }
                it.strings[fieldData.errorBrandingKey]?.run { fieldData.validationError = this }

                fun createEditViewView(fieldData: FormField): EditText {
                    return EditText(context).apply {
                        hint = fieldData.label
                        id = ViewCompat.generateViewId()
                        tag = index
                        fieldData.value?.let { setText(it) }
                    }
                }

                when (fieldData.type) {
                    FormFieldType.Select -> {
                        when (fieldData.key) {
                            "department" -> handleDeptView(form_fields_container, fieldData,
                                    createEditViewView(fieldData).apply {
                                        tag = index
                                    })
                            LanguageFieldKey -> handleLanguageView(context!!, index, form_fields_container,
                                    fieldData, FormConfiguration(context))
                        }
                    }
                    else -> {
                        form_fields_container.addView(createEditViewView(fieldData))
                    }
                }

            }
        }
    }

    /**
     * Observes language selection
     */
    private val selectionListener = object : SelectionListener {
        override fun onSelectedOption(selectionSpec: SelectionSpec) {
            when (selectionSpec.fieldKey) {
                LanguageFieldKey -> handleLanguageSelection(selectionSpec)
            }
        }
    }

    private fun handleLanguageView(context: Context, index: Int, fieldsContainer: LinearLayout,
            formField: FormField, formConfiguration: FormConfiguration) {

        fieldsContainer.addView(SelectionView(context, formField, formConfiguration).apply {
            selectionChangeListener = this@CustomForm.selectionListener
            this.tag = index
        }, index)
    }

    private fun handleLanguageSelection(selectionSpec: SelectionSpec) {
        // handle language selection, if was changed.
        val language =
                selectionSpec.selectedOption.value.takeIf { selectionSpec.prevOption != null && it != selectionSpec.prevOption?.value }
        language?.run {
            // pass selected language and a callback to the languageChange observer. (chat handler)
            formViewModel.onLanguageChanged(language to { approved, error ->
                selectionSpec.onVerified(approved, error) // pass results to the SelectionView,
                // in case of error, the selection will roll back to the previous selected language.
            })
        }
    }

    /**
     * Creates a department code editable field with a description like list of available departments.
     */
    private fun handleDeptView(fieldsContainer: LinearLayout, fieldData: FormField, fieldView: EditText) {

        val departmentTitle = TextView(context).apply {
            text = resources.getText(R.string.department_code)
            setTextColor(Color.BLUE)
        }
        fieldsContainer.addView(departmentTitle)

        //-> sets the value of the department editText, with the previously filled value or `defaultOption` if available.
        var depValue = fieldData.value
                ?: fieldData.defaultOption?.takeIf { it.isDefaultValue && it.isAvailable }?.value ?: ""
        fieldView.text = SpannableStringBuilder(depValue)
        fieldsContainer.addView(fieldView)

        //-> Adds the departments options as description below the edit field.
        fieldData.options?.run {
            val deptOptionsSB = StringBuilder().append(
                    context?.resources?.getString(R.string.custom_form_departments_title) ?: "")

            forEach {

                deptOptionsSB.appendln(context?.getString(R.string.custom_form_departments_details, it.name,
                        it.availableLabel, it.value)).appendln()

                if (depValue.isBlank() && it.isAvailable) {
                    depValue = it.value
                }
            }

            val deptOptions = TextView(context).apply {
                text = deptOptionsSB
                setTextIsSelectable(true)
            }
            fieldsContainer.addView(deptOptions)
        }
    }

    private fun initSubmitButton() {
        custom_form_submit_button.setOnClickListener {

            form_fields_container?.forEachChild {
                (it as? EditText)?.run {
                    val index = tag as Int
                    formViewModel.data?.fields?.get(index)?.value = this.text.toString()
                }
            }

            isSubmitted = true

            // activating form submission - which will trigger the formListener.onComplete
            // with the form fields, filled values
            formViewModel.data?.chatForm?.run {
                formViewModel.onSubmitForm(StateEvent(StateEvent.Accepted, data = this))
            } ?: Log.e("CustomForm", "Something went wron, form submission can't be done.")

            parentFragmentManager.popBackStackImmediate()
        }
    }
//</editor-fold>

    override fun onStop() {

        if ((isRemoving || activity?.isFinishing == true) && !isSubmitted) {
            // in case user doesn't want to fill the form and presses "back" to cancel.
            formViewModel.onSubmitForm(StateEvent(StateEvent.Canceled))
        }

        super.onStop()
    }
}
