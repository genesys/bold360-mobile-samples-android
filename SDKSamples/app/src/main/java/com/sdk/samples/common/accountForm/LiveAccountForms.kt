package com.sdk.samples.common.accountForm

import com.sdk.samples.R
import com.sdk.samples.common.AsyncSharedDataHandler
import com.sdk.samples.common.ChatType
import com.sdk.samples.common.DataController
import com.sdk.samples.common.LiveSharedDataHandler
import com.sdk.samples.common.SharedDataHandler.Companion.ChatType_key
import kotlinx.android.synthetic.main.async_account_form.*
import kotlinx.android.synthetic.main.live_account_form.*
import java.util.*

open class LiveAccountForm(dataController: DataController) : AccountForm(dataController) {

    override val formLayoutRes: Int
        get() = R.layout.live_account_form

    override fun fillFields() {
        api_key_edit_text.setText( dataController.getAccount(context)[LiveSharedDataHandler.Access_key] as? String ?: "" )
    }

    override fun validateFormData(): Map<String, Any?>? {
        return api_key_edit_text.text.toString().takeUnless { it.isEmpty() }?.let {
            mapOf(ChatType_key to ChatType.LiveChat, LiveSharedDataHandler.Access_key to it)
        } ?: run {
            presentError(api_key_edit_text, context?.getString(R.string.error_apiKey))
            null
        }
    }

    companion object {
        fun newInstance(dataController: DataController): LiveAccountForm {
            return LiveAccountForm(dataController)
        }
    }
}

class AsyncAccountForm(dataController: DataController) : LiveAccountForm(dataController) {

    override val formLayoutRes: Int
        get() = R.layout.async_account_form

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

        firstName_edit_text.text?.takeUnless { it.isEmpty() }?.let {
            accountMap[AsyncSharedDataHandler.First_Name_key] = it.toString()
        }

        lastName_edit_text.text?.takeUnless { it.isEmpty() }?.let {
            accountMap[AsyncSharedDataHandler.Last_Name_key] = it.toString()
        }

        phone_edit_text.text?.takeUnless { it.isEmpty() }?.let {
            accountMap[AsyncSharedDataHandler.Phone_Number_key] = it.toString()
        }

        accountMap[ChatType_key] = ChatType.AsyncChat
        accountMap[AsyncSharedDataHandler.user_id_key] = UUID.randomUUID().toString()

        return accountMap
    }

    companion object {
        fun newInstance(dataController: DataController): AsyncAccountForm {
            return AsyncAccountForm(dataController)
        }
    }
}