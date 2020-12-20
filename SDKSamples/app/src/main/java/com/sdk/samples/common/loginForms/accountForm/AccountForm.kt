package com.sdk.samples.common.loginForms.accountForm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.nanorep.nanoengine.Account
import com.sdk.samples.R
import com.sdk.samples.common.accountUtils.ChatType
import com.sdk.samples.common.accountUtils.toAsyncAccount
import com.sdk.samples.common.accountUtils.toBotAccount
import com.sdk.samples.common.accountUtils.toLiveAccount
import com.sdk.samples.common.loginForms.BotSharedDataHandler
import com.sdk.samples.common.loginForms.DataController
import com.sdk.samples.common.loginForms.SharedDataHandler


interface AccountFormDelegate {

    /**
     * Controls the data flow from the form and to it
     */
    val dataController: DataController

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

abstract class AccountForm(override val dataController: DataController) : Fragment(), AccountFormDelegate {

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
                context?.let { dataController.updateAccount(context, account, extraData) }
            }
        }
    }

    override fun presentError(editText: EditText, message: String?) {
        editText.requestFocus()
        editText.error = message ?: ""
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        fillFields()

        view.findViewById<Button>(R.id.start_chat).setOnClickListener {
            validateAndUpdate()?.run {
                dataController.onSubmit(this)
            }
        }
    }

    companion object {

        const val TAG = "AccountForm"

        fun newInstance(
            dataController: DataController,
            extraParams: List<String>?
        ): AccountForm {

            dataController.extraParams = extraParams

            return when (dataController.chatType) {
                ChatType.Live -> LiveAccountForm.newInstance(dataController)
                ChatType.Async -> AsyncAccountForm.newInstance(dataController)
                else -> BotAccountForm.newInstance(dataController)
            }
        }
    }
}