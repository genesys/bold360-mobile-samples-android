package com.common.utils.loginForms

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
import com.common.utils.loginForms.dynamicFormPOC.JsonSharedDataHandler.Companion.preChat_deptCode_key
import com.common.utils.loginForms.dynamicFormPOC.JsonSharedDataHandler.Companion.preChat_fName_key
import com.common.utils.loginForms.dynamicFormPOC.JsonSharedDataHandler.Companion.preChat_lName_key
import com.common.utils.loginForms.dynamicFormPOC.LoginFormViewModel
import com.common.utils.loginForms.dynamicFormPOC.defs.FieldTypes
import com.common.utils.loginForms.dynamicFormPOC.toAsyncAccount
import com.common.utils.loginForms.dynamicFormPOC.toBotAccount
import com.common.utils.loginForms.dynamicFormPOC.toLiveAccount
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.nanorep.nanoengine.Account
import com.sdk.common.R
import kotlinx.android.synthetic.main.account_form.*


interface AccountFormDelegate {

    /**
     * Takes the fields data from the shared properties
     */
    fun fillFields()

    /**
     * Validates the form data
     * returns Account map if the data is valid else null
     */
    fun validateFormData(): Map<String, Any?>?

    /**
     * Presents error on a form field
     */
    fun presentError(editText: EditText, message: String?)
}

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
            it.key == preChat_deptCode_key ||
                    it.key == preChat_lName_key ||
                    it.key == preChat_fName_key
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

open class AccountForm : LoginForm(), AccountFormDelegate {

 /*   abstract val formLayoutRes: Int

    @ChatType
    abstract val chatType: String*/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.account_form, container, false)
    }

    fun updateExtraData(accountData: JsonObject): MutableMap<String, Any?>? {

        var extraData: MutableMap<String, Any?>? = null

        accountData.entrySet().filter {
            it.key == preChat_deptCode_key ||
                    it.key == preChat_lName_key ||
                    it.key == preChat_fName_key
        }.map {
            if (extraData == null) extraData = mutableMapOf()
            extraData!!.put(it.key, it.value)
        }

        return extraData
    }

    private fun validateAndUpdate (): Account? {

        return _validateFormData()?.let { accountData ->
            Gson().fromJson(accountData, Account::class.java).also {
                loginFormViewModel.updateGenericAccount(
                    context, accountData, updateExtraData(
                        accountData
                    )
                )
            }
        }
    }

    fun _validateFormData(): JsonObject? {
        return loginFormViewModel.getJsonAccount(context)
    }

    override fun validateFormData(): Map<String, Any?>? {
        return null
    }

    override fun presentError(editText: EditText, message: String?) {
        editText.requestFocus()
        editText.error = message ?: ""
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

    override fun fillFields() {

        loginFormViewModel.getFormFields().forEach {

            val currentField = it.asJsonObject

            formFields?.apply {

                addView(
                    when (currentField.get("type").asInt) {
                        FieldTypes.TextInput -> EditText(context)
                        else -> TextView(context)
                    }.apply {
                        val account = loginFormViewModel.getJsonAccount(context)
                        hint = currentField.get("hint").asString
                        text = (account?.get(currentField.get("name").asString)?.asString ?: "")
                    }
                )
            }
        }
    }

    companion object {

        const val TAG = "AccountForm"

        fun newInstance(): AccountForm {
            return AccountForm()
        }

        /*fun newInstance(@ChatType chatType: String): AccountForm {
            *//*return when (chatType) {
                ChatType.Live -> LiveAccountForm.newInstance()
                ChatType.Async -> AsyncAccountForm.newInstance()
                else -> BotAccountForm.newInstance()
            }*//*
            return AccountForm()
        }*/
    }
}