package com.sdk.samples.common.accountForm

import com.sdk.samples.R
import com.sdk.samples.common.ChatType
import com.sdk.samples.common.DataController
import com.sdk.samples.common.LiveSharedDataHandler
import com.sdk.samples.common.SharedDataHandler.Companion.ChatType_key
import kotlinx.android.synthetic.main.live_account_form.*

open class LiveAccountForm(dataController: DataController) : AccountForm(dataController) {

    override val formLayoutRes: Int
        get() = R.layout.live_account_form

    override fun fillFields() {
        live_api_key_edit_text.setText( dataController.getAccount(context).apiKey as? String ?: "" )
    }

    override fun validateFormData(): Map<String, Any?>? {

        return live_api_key_edit_text.text.toString().takeUnless { it.isEmpty() }?.let {
            mapOf(ChatType_key to ChatType.LiveChat, LiveSharedDataHandler.Access_key to it)

        } ?: run {
            presentError(live_api_key_edit_text, context?.getString(R.string.error_apiKey))
            null

        }
    }

    companion object {
        fun newInstance(dataController: DataController): LiveAccountForm {
            return LiveAccountForm(dataController)
        }
    }
}