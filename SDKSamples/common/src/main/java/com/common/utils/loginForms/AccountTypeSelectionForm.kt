package com.common.utils.loginForms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import com.common.utils.loginForms.dynamicFormPOC.defs.ChatType
import com.common.utils.loginForms.accountUtils.FormsParams.EnableRestore
import com.sdk.common.R
import kotlinx.android.synthetic.main.restore_form.*

class AccountTypeSelectionForm(val onTypeSelected: (chatType: String) -> Unit) : LoginForm() {

    @ChatType
    private val selectedChatType: String
        get() = getCheckedRadio()?.tag?.toString() ?: ChatType.None

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.restore_form, container, false)
    }

    override fun onResume() {
        loginFormViewModel.chatType = ChatType.None
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        if (hasFormParam(EnableRestore)) {
            restore_switch.visibility = View.VISIBLE
            current_radio.visibility = View.VISIBLE
        }

        start_chat.setOnClickListener {

            loginFormViewModel.restoreRequest = restore_switch.isChecked

            if (selectedChatType == ChatType.None) {
                loginFormViewModel.onStartChat(null)
            } else {
                onTypeSelected(selectedChatType)
            }

        }

        current_radio.setOnCheckedChangeListener { _, isChecked ->
            if (!restore_switch.isChecked) restore_switch.isChecked = true
            restore_switch.isEnabled = !isChecked
        }

    }

    private fun getCheckedRadio() =
        view?.findViewById<RadioButton>(chat_action_group.checkedRadioButtonId)


    companion object {
        const val TAG = "RestoreForm"

        fun newInstance(onTypeSelected: (chatType: String) -> Unit): AccountTypeSelectionForm {
            return AccountTypeSelectionForm(onTypeSelected)
        }
    }
}