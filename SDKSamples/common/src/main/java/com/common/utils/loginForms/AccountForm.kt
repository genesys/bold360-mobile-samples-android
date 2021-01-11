package com.common.utils.loginForms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.common.utils.loginForms.accountUtils.ChatType
import com.common.utils.loginForms.accountUtils.toAsyncAccount
import com.common.utils.loginForms.accountUtils.toBotAccount
import com.common.utils.loginForms.accountUtils.toLiveAccount
import com.nanorep.nanoengine.Account
import com.sdk.common.R


interface AccountFormDelegate {

    /**
     * Controls the data flow from the form and to it
     */
    val formViewModel: DataController

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

abstract class AccountForm : Fragment(), AccountFormDelegate {

    override val formViewModel: FormViewModel by activityViewModels()

    abstract val formLayoutRes: Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(formLayoutRes, container, false)
    }

    private fun validateAndUpdate (): Account? {

        return validateFormData()?.let { accountData ->
            val chatType = accountData[SharedDataHandler.ChatType_key] as String

            formViewModel.chatType = chatType

            when (chatType) {
                ChatType.Async -> accountData.toAsyncAccount()
                ChatType.Live -> accountData.toLiveAccount()
                else -> accountData.toBotAccount()
            }?.also { account ->
                val extraData = accountData.filter {
                    it.key == SharedDataHandler.ChatType_key ||
                            it.key == BotSharedDataHandler.preChat_deptCode_key ||
                            it.key == BotSharedDataHandler.preChat_lName_key ||
                            it.key == BotSharedDataHandler.preChat_fName_key
                }
                context?.let { formViewModel.updateAccount(context, account, extraData) }
            }
        }
    }

    override fun presentError(editText: EditText, message: String?) {
        editText.requestFocus()
        editText.error = message ?: ""
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        view.findViewById<Button>(R.id.start_chat).apply {
            setOnClickListener {
                validateAndUpdate()?.run {
                    formViewModel.onSubmit(this)
                }
            }
        }

        fillFields()
    }

    companion object {

        const val TAG = "AccountForm"

        fun newInstance(@ChatType chatType: String): AccountForm {
            return when (chatType) {
                ChatType.Live -> LiveAccountForm.newInstance()
                ChatType.Async -> AsyncAccountForm.newInstance()
                else -> BotAccountForm.newInstance()
            }
        }
    }
}