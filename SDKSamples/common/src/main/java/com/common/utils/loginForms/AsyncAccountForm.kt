package com.common.utils.loginForms

import android.os.Bundle
import android.view.View
import com.integration.core.userInfo
import com.nanorep.convesationui.async.AsyncAccount
import com.common.utils.accountUtils.ChatType
import com.common.utils.accountUtils.ExtraParams
import kotlinx.android.synthetic.main.async_account_form.*
import nanorep.com.common.R

class AsyncAccountForm : LiveAccountForm() {

    override val formLayoutRes: Int
        get() = R.layout.async_account_form

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        dataController.extraParams?.let {
            if (it.contains(ExtraParams.RestoreSwitch)) { restore_switch.visibility = View.VISIBLE }
            if (it.contains(ExtraParams.AsyncExtraData)) { async_extra_data.visibility = View.VISIBLE }
        }

        super.onViewCreated(view, savedInstanceState)
    }

    override fun fillFields() {

        val account = dataController.getAccount(context) as AsyncAccount

        async_api_key_edit_text.setText( account.apiKey )
        async_email_edit_text.setText( account.info.userInfo.email )
        async_user_id_edit_text.setText( account.info.userInfo.userId )
        async_firstName_edit_text.setText( account.info.userInfo.firstName )
        async_lastName_edit_text.setText( account.info.userInfo.lastName )
        async_phone_edit_text.setText( account.info.userInfo.phoneNumber )
    }

    override fun validateFormData(): Map<String, Any?>? {

        val accountMap = mutableMapOf<String, Any>()

        async_email_edit_text.text?.takeIf { it.isNotEmpty() }?.let {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches()) {
                presentError(async_email_edit_text, context?.getString(R.string.error_email))
                return null
            } else {
                accountMap[AsyncSharedDataHandler.Email_key] = it.toString()
            }
        }

        async_api_key_edit_text.text?.takeUnless { it.isEmpty() }?.let {
            accountMap[AsyncSharedDataHandler.Access_key] = it.toString()
        }  ?: kotlin.run {
            presentError(async_api_key_edit_text, context?.getString(R.string.error_apiKey))
            return null
        }

        async_user_id_edit_text.text?.takeUnless { it.isEmpty() }?.let {
            accountMap[AsyncSharedDataHandler.user_id_key] = it.toString()
        }

        async_firstName_edit_text.text?.takeUnless { it.isEmpty() }?.let {
            accountMap[AsyncSharedDataHandler.First_Name_key] = it.toString()
        }

        async_lastName_edit_text.text?.takeUnless { it.isEmpty() }?.let {
            accountMap[AsyncSharedDataHandler.Last_Name_key] = it.toString()
        }

        async_phone_edit_text.text?.takeUnless { it.isEmpty() }?.let {
            accountMap[AsyncSharedDataHandler.Phone_Number_key] = it.toString()
        }

        accountMap[SharedDataHandler.ChatType_key] = ChatType.Async

        dataController.restoreRequest = restore_switch.isChecked

        return accountMap
    }

    companion object {
        fun newInstance(): AsyncAccountForm {
            return AsyncAccountForm()
        }
    }
}