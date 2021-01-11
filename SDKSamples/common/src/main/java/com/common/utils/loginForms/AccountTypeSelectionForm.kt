package com.common.utils.loginForms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.common.utils.loginForms.accountUtils.ChatType
import com.sdk.common.R
import kotlinx.android.synthetic.main.restore_form.*

class AccountTypeSelectionForm(private val enableRestoreFields: Boolean, val onTypeSelected: (chatType: String) -> Unit) : Fragment() {

    private val formViewModel: FormViewModel by activityViewModels()

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

            formViewModel.chatType = selectedChatType
            formViewModel.restoreRequest = restore_switch.isChecked

            if (selectedChatType == ChatType.None) {

                formViewModel.extraData =
                    mapOf<String, Any>(SharedDataHandler.ChatType_key to ChatType.None)
                formViewModel.onSubmit(null)

            } else onTypeSelected(selectedChatType)

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

        fun newInstance(enableRestoreFields: Boolean = false, onTypeSelected: (chatType: String) -> Unit): AccountTypeSelectionForm {
            return AccountTypeSelectionForm(enableRestoreFields, onTypeSelected)
        }
    }
}