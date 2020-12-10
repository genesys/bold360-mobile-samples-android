package com.sdk.samples.common.accountForm

import android.os.Bundle
import android.view.View
import com.integration.core.userInfo
import com.nanorep.convesationui.async.AsyncAccount
import com.sdk.samples.R
import com.sdk.samples.common.*
import kotlinx.android.synthetic.main.async_account_form.*
import kotlinx.android.synthetic.main.live_account_form.*


class AsyncAccountForm(dataController: DataController) : LiveAccountForm(dataController) {

    override val formLayoutRes: Int
        get() = R.layout.async_account_form

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        dataController.extraParams?.let {
            if (it.contains(ExtraParams.RestoreSwitch)) { restore_switch.visibility = View.VISIBLE }
        }

        super.onViewCreated(view, savedInstanceState)
    }

    override fun fillFields() {

        val account = dataController.getAccount(context) as AsyncAccount

        api_key_edit_text.setText( account.apiKey )
        email_edit_text.setText( account.info.userInfo.email )
        user_id_edit_text.setText( account.info.userInfo.userId )
        firstName_edit_text.setText( account.info.userInfo.firstName )
        lastName_edit_text.setText( account.info.userInfo.lastName )
        phone_edit_text.setText( account.info.userInfo.phoneNumber )
    }

    override fun validateFormData(): Map<String, Any?>? {

        val accountMap = mutableMapOf<String, Any>()

        email_edit_text.text?.takeIf { it.isNotEmpty() }?.let {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches()) {
                presentError(email_edit_text, context?.getString(R.string.error_email))
                return null
            } else {
                accountMap[AsyncSharedDataHandler.Email_key] = it.toString()
            }
        }

        api_key_edit_text.text?.takeUnless { it.isEmpty() }?.let {
            accountMap[AsyncSharedDataHandler.Access_key] = it.toString()
        }  ?: kotlin.run {
            presentError(api_key_edit_text, context?.getString(R.string.error_apiKey))
            return null
        }

        user_id_edit_text.text?.takeUnless { it.isEmpty() }?.let {
            accountMap[AsyncSharedDataHandler.user_id_key] = it.toString()
        }

        firstName_edit_text.text?.takeUnless { it.isEmpty() }?.let {
            accountMap[AsyncSharedDataHandler.First_Name_key] = it.toString()
        }

        lastName_edit_text.text?.takeUnless { it.isEmpty() }?.let {
            accountMap[AsyncSharedDataHandler.Last_Name_key] = it.toString()
        }

        phone_edit_text.text?.takeUnless { it.isEmpty() }?.let {
            accountMap[AsyncSharedDataHandler.Phone_Number_key] = it.toString()
        }

        accountMap[SharedDataHandler.ChatType_key] = ChatType.AsyncChat

        dataController.updateRestoreRequest(restore_switch.isChecked)

        return accountMap
    }

    companion object {
        fun newInstance(dataController: DataController): AsyncAccountForm {
            return AsyncAccountForm(dataController)
        }
    }
}