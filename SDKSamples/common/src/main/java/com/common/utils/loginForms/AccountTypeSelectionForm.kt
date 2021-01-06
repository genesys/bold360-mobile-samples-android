package com.common.utils.loginForms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import com.common.utils.loginForms.accountUtils.ChatType
import kotlinx.android.synthetic.main.restore_form.*
import nanorep.com.common.R

class AccountTypeSelectionForm(private val enableRestoreFields: Boolean, val onTypeSelected: (chatType: String, restoreRequest: Boolean) -> Unit) : Fragment() {

    @ChatType
    private val selectedChatType: String
        get() = getCheckedRadio()?.tag?.toString() ?: ChatType.Bot

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.restore_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        if (enableRestoreFields) {
            restore_switch.visibility = View.VISIBLE
            current_radio.visibility = View.VISIBLE
        }

        start_chat.setOnClickListener {
            onTypeSelected(selectedChatType, restore_switch.isChecked)
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

        fun newInstance(enableRestoreFields: Boolean = false, onTypeSelected: (chatType: String, restoreRequest: Boolean) -> Unit): AccountTypeSelectionForm {
            return AccountTypeSelectionForm(enableRestoreFields, onTypeSelected)
        }
    }
}