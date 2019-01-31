package com.conversation.demo.forms

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.conversation.demo.R
import com.integration.bold.boldchat.core.FormData
import com.integration.bold.boldchat.visitor.api.FormField
import com.integration.bold.boldchat.visitor.api.FormFieldType
import com.nanorep.convesationui.bold.ui.FormListener
import com.nanorep.convesationui.structure.setStyleConfig
import com.nanorep.nanoengine.model.configuration.StyleConfig
import com.nanorep.sdkcore.utils.TextTagHandler
import com.nanorep.sdkcore.utils.px
import com.nanorep.sdkcore.utils.weakRef
import org.jetbrains.anko.childrenSequence
import java.lang.ref.WeakReference


class DemoForm : Fragment() {

    private var data: FormData? = null
    private var isSubmitted = false

    private var listener: WeakReference<FormListener>? = null

    companion object {
        @JvmStatic fun create(data: FormData, listener: FormListener) : Fragment {
            return DemoForm().apply {
                this.data = data
                this.listener = listener.weakRef()
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.dummy_live_forms_layout, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

/*
        view.findViewById<TextView>(R.id.form_text).text = data?.logFormBrandings()
*/

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val fieldsContainer =  view.findViewById<LinearLayout>(R.id.form_fields_container)

        val introTxt = TextView(context)
        fieldsContainer.addView(introTxt.apply {
            setStyleConfig(StyleConfig(20, Color.DKGRAY, Typeface.DEFAULT_BOLD))
            (layoutParams as? ViewGroup.MarginLayoutParams)?.setMargins(0, 0,0, 60.px)
            data?.getIntroMessage()?.run { text = TextTagHandler.getSpannedHtml(this) }?: kotlin.run { visibility = View.GONE }
        })

        view.findViewById<Button>(R.id.submitButton).setOnClickListener {

            fieldsContainer?.childrenSequence()?.forEach {
                (it as? EditText)?.run {

                    val index = tag as Int
                    data?.fields?.get(index)?.value = this.text.toString()

                }
            }

            isSubmitted = true
            fragmentManager?.popBackStackImmediate()

            listener?.get()?.onComplete(data?.chatForm)
        }

        data?.fields?.forEachIndexed { index, formField ->
            val editText = EditText(context)
            editText.hint = formField.label
            editText.id = View.generateViewId()
            editText.tag = index

            fieldsContainer.addView(editText)

            if (formField.type == FormFieldType.Select && formField.label.contains("Department")) {
                handleDeptView(index, fieldsContainer, formField, editText)
            }
        }
    }

    private fun handleDeptView(index: Int, fieldsContainer: LinearLayout, formField: FormField, editText: EditText) {

        val departmentTitle = TextView(context)
        departmentTitle.text = resources.getText(R.string.department_code)

        data?.fields?.takeIf{ it.size - 1  > index}?.run {
            fieldsContainer.addView(departmentTitle, index + 1)
        }

        departmentTitle.setTextColor(Color.BLUE)

        formField.options?.run {
            var dep:String = ""
            val deptOptionsSB = StringBuilder().append(" DepartmentStrings: \n\n")
            forEach {

                deptOptionsSB
                        .append(" Name: ${it.name} ,Status: ${it.availableLabel}, \nCode to Input: ${it.value}")
                        .append("\n\n")
                if(dep == "" && it.isAvailable){
                    dep = it.value
                }
            }

            editText.text = SpannableStringBuilder(dep)
            val deptOptions = TextView(context)
            deptOptions.text = deptOptionsSB
            deptOptions.setTextIsSelectable(true)

            fieldsContainer.addView(deptOptions)
        }

    }

    override fun onStop() {

        if (!isSubmitted) {
            listener?.get()?.onCancel(data?.formType)
        }

        super.onStop()
    }
}
