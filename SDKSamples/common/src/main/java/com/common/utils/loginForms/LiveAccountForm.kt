package com.common.utils.loginForms
//
//import com.common.utils.loginForms.accountUtils.ChatType
//import com.sdk.common.R
//import kotlinx.android.synthetic.main.live_account_form.*
//
//open class LiveAccountForm : AccountForm(formData) {
//
//    override val formLayoutRes: Int
//        get() = R.layout.live_account_form
//
//    override val chatType: String
//        get() = ChatType.Live
//
//    override fun fillFields() {
//        live_api_key_edit_text.setText( /*dataController.getAccount(context).apiKey as? String.orEmpty()*/"2300000001700000000:2279548743171682758:/zBPGZf1erxQ1schkxUVq64M2OSUR/Wz:gamma" )
//    }
//
//    override fun validateFormData(): Map<String, Any?>? {
//
//        return live_api_key_edit_text.text.toString().takeUnless { it.isEmpty() }?.let {
//            mapOf(LiveSharedDataHandler.Access_key to it)
//
//        } ?: run {
//            presentError(live_api_key_edit_text, context?.getString(R.string.error_apiKey))
//            null
//
//        }
//    }
//
//    companion object {
//        fun newInstance(): LiveAccountForm {
//            return LiveAccountForm()
//        }
//    }
//}