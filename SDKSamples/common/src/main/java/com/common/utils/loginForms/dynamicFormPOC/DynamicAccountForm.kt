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
import com.common.utils.loginForms.dynamicFormPOC.defs.ChatType
import com.common.utils.loginForms.dynamicFormPOC.defs.FieldTypes
import com.google.gson.JsonObject
import com.nanorep.nanoengine.Account
import com.sdk.common.R
import kotlinx.android.synthetic.main.account_form.*

class DynamicAccountForm/*<T : Account>(private val c: Class<T>)*/ : Fragment() {

    private val loginFormViewModel: LoginFormViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.account_form, container, false)
    }

    private fun updateExtraData(accountData: JsonObject): MutableMap<String, Any?>? {

        var extraData: MutableMap<String, Any?>? = null

        accountData.entrySet().filter {
            it.key == JsonSharedDataHandler.preChat_deptCode_key ||
                    it.key == JsonSharedDataHandler.preChat_lName_key ||
                    it.key == JsonSharedDataHandler.preChat_fName_key
        }.map {
            if (extraData == null) extraData = mutableMapOf()
            extraData!!.put(it.key, it.value)
        }

        return extraData
    }

    private fun validateAndUpdate (): Account? {

        return validateFormData()?.let { accountData ->
            when (loginFormViewModel.chatType) {
                ChatType.Bot -> accountData.toBotAccount()
                ChatType.Async -> accountData.toAsyncAccount()
                else -> accountData.toLiveAccount()
            }?.also {
                loginFormViewModel.updateGenericAccount(
                    context, accountData, updateExtraData(accountData)
                )
            }
        }
    }

    private fun validateFormData(): JsonObject? {
        return loginFormViewModel.getJsonAccount(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        fillFields()

        view.findViewById<Button>(R.id.start_chat).apply {
            setOnClickListener {
                validateAndUpdate()?.run {
                    loginFormViewModel.onStartChat(this)
                }
            }
        }
    }

    private fun fillFields() {

        loginFormViewModel.getFormFields().forEach {

            val currentField = it.asJsonObject

            formFields?.apply {

                addView(
                    when (currentField.get("type").asInt) {
                        FieldTypes.TextInput -> EditText(context)
                        else -> TextView(context)
                    }.apply {
                        hint = currentField.get("hint").asString
                        text = (loginFormViewModel.getJsonAccount(context)?.get(currentField.get("name").asString)?.asString ?: "")
                    }
                )
            }
        }
    }

    companion object {

        fun /*<T : Account>*/ newInstance(/*c: Class<T>*/): DynamicAccountForm/*<T>*/ {
            return DynamicAccountForm(/*c*/)
        }
    }
}