package com.common.utils.loginForms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import com.common.utils.accountUtils.ChatType
import kotlinx.android.synthetic.main.restore_form.*
import nanorep.com.common.R

class RestoreForm(val onChatRestore: (chatType: String, restoreRequest: Boolean) -> Unit) : Fragment() {

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

        start_chat.setOnClickListener {
            onChatRestore(selectedChatType, restore_switch.isChecked)
        }

        current_radio.setOnCheckedChangeListener { _, isChecked ->
            restore_switch.isChecked = isChecked
        }

    }

    private fun getCheckedRadio() =
        view?.findViewById<RadioButton>(chat_action_group.checkedRadioButtonId)


    companion object {
        const val TAG = "RestoreForm"

        fun newInstance(onChatRestore: (chatType: String, restoreRequest: Boolean) -> Unit): RestoreForm {
            return RestoreForm(onChatRestore)
        }
    }
}