package com.common.utils.loginForms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.common.utils.loginForms.dynamicFormPOC.defs.ChatType
import com.nanorep.nanoengine.Account
import com.sdk.common.R


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

abstract class AccountForm : LoginForm(), AccountFormDelegate {

    abstract val formLayoutRes: Int

    @ChatType
    abstract val chatType: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(formLayoutRes, container, false)
    }

    private fun validateAndUpdate (): Account? {

       /* return validateFormData()?.let { accountData ->
            when (chatType) {
                ChatType.Async -> accountData.toAsyncAccount()
                ChatType.Live -> accountData.toLiveAccount()
                else -> accountData.toBotAccount()
            }?.also { account ->
                val extraData = accountData.filter {
                    it.key == BotSharedDataHandler.preChat_deptCode_key ||
                            it.key == BotSharedDataHandler.preChat_lName_key ||
                            it.key == BotSharedDataHandler.preChat_fName_key
                }
                loginFormViewModel.updateAccount(context, account, extraData)
            }
        }*/
        return null
    }

    override fun presentError(editText: EditText, message: String?) {
        editText.requestFocus()
        editText.error = message.orEmpty()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        view.findViewById<Button>(R.id.start_chat).apply {
            setOnClickListener {
                validateAndUpdate()?.run {
//                    loginFormViewModel.onStartChat(this)
                }
            }
        }

        fillFields()
    }

    companion object {

        const val TAG = "AccountForm"

        /*fun newInstance(@ChatType chatType: String): AccountForm {
            return when (chatType) {
                ChatType.Live -> LiveAccountForm.newInstance()
                ChatType.Async -> AsyncAccountForm.newInstance()
                else -> BotAccountForm.newInstance()
            }
        }*/
    }
}