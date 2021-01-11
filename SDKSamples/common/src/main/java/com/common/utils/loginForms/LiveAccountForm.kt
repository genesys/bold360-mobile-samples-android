package com.common.utils.loginForms

import com.common.utils.loginForms.SharedDataHandler.Companion.ChatType_key
import com.common.utils.loginForms.accountUtils.ChatType
import kotlinx.android.synthetic.main.live_account_form.*
import com.sdk.common.R

open class LiveAccountForm : AccountForm() {

    override val formLayoutRes: Int
        get() = R.layout.live_account_form

    override fun fillFields() {
        live_api_key_edit_text.setText( /*dataController.getAccount(context).apiKey as? String ?: ""*/"2300000001700000000:2279548743171682758:/zBPGZf1erxQ1schkxUVq64M2OSUR/Wz:gamma" )
    }

    override fun validateFormData(): Map<String, Any?>? {

        return live_api_key_edit_text.text.toString().takeUnless { it.isEmpty() }?.let {
            mapOf(ChatType_key to ChatType.Live, LiveSharedDataHandler.Access_key to it)

        } ?: run {
            presentError(live_api_key_edit_text, context?.getString(R.string.error_apiKey))
            null

        }
    }

    companion object {
        fun newInstance(): LiveAccountForm {
            return LiveAccountForm()
        }
    }
}