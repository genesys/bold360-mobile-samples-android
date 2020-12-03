package com.sdk.samples.common.accountForm

import com.nanorep.convesationui.async.AsyncAccount
import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.nanoengine.Account
import com.sdk.samples.R
import com.sdk.samples.common.BotSharedDataHandler
import com.sdk.samples.common.DataController
import kotlinx.android.synthetic.main.live_account_form.*

open class LiveAccountForm(dataController: DataController) : AccountForm(dataController) {

    override val formLayoutRes: Int
        get() = R.layout.live_account_form

    override fun fillFields() {
        api_key_edit_text.setText( dataController.getAccount(context)[BotSharedDataHandler.ApiKey_key] as? String ?: "" )
    }

    override fun validateFormData(): Account? {
        return api_key_edit_text.text.toString().takeUnless { it.isEmpty() }?.let {
            BoldAccount(it)
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

    override fun validateFormData(): Account? {
        return api_key_edit_text.text.toString().takeUnless { it.isEmpty() }?.let {
            AsyncAccount(it)
        } ?: let {
            presentError(api_key_edit_text, context?.getString(R.string.error_apiKey))
            null
        }
    }

    companion object {
        fun newInstance(dataController: DataController): AsyncAccountForm {
            return AsyncAccountForm(dataController)
        }
    }
}