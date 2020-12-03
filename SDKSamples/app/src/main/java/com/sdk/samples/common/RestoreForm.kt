package com.sdk.samples.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import com.sdk.samples.R
import kotlinx.android.synthetic.main.restore_layout.*

class RestoreForm(val onChatRestore: (chatType: String, isRestore: Boolean) -> Unit) : Fragment() {

    @ChatType private val selectedChatType: String
        get() = getCheckedRadio()?.tag?.toString() ?: ChatType.BotChat

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.restore_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        create_chat.setOnClickListener {
            enableChatAction(false)
            onChatRestore(selectedChatType, false)
        }

        restore_chat.setOnClickListener {
            enableChatAction(false)
            onChatRestore(selectedChatType, true)
        }

        chat_action_group.check(bot_radio.id)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        enableChatAction(true)
    }

    private fun enableChatAction(enable: Boolean) {
        create_chat.isEnabled = enable
        restore_chat.isEnabled = enable
    }

    private fun getCheckedRadio() =
        view?.findViewById<RadioButton>(chat_action_group.checkedRadioButtonId)


    companion object {
        const val TAG = "RestoreForm"

        fun newInstance(onChatRestore: (chatType: String, isRestore: Boolean) -> Unit): RestoreForm {
            return RestoreForm(onChatRestore)
        }
    }
}