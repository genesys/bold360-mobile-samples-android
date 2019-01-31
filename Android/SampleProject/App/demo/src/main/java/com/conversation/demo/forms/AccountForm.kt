package com.conversation.demo.forms

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import com.conversation.demo.ChatType
import com.conversation.demo.R
import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.BotAccount
import kotlinx.android.synthetic.main.account_form.*


interface AccountListener {
    fun onReady(account: Account)
}


class AccountForm : Fragment() {

    companion object {
        @JvmStatic fun create(@ChatType chatType: String): AccountForm {
            return AccountForm().apply { this.chatType = chatType }
        }
    }

    private lateinit var chatType: String

    private var accountListener: AccountListener? = null
    private var account: Account? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.account_form, container, false);
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bot_fields.visibility = if (chatType == ChatType.LiveChat) View.GONE else View.VISIBLE

        start_chat.setOnClickListener {
            it.isEnabled = false
            it.startAnimation(AlphaAnimation(1f, 0.8f).also { it.duration = 150 })

            account = createAccount()
            account?.run { accountListener?.onReady(this) }
        }
    }

    private fun createAccount(): Account? {
        return when (chatType) {
            ChatType.LiveChat -> {
                api_key_edit_text.text.toString().takeUnless { it.isEmpty() }?.let { BoldAccount(it) }
            }

            ChatType.BotChat -> {
                getBotAccount()
            }

            else -> null
        }
    }

    private fun getBotAccount(): BotAccount {
        val accountName = account_name_edit_text.text.toString()
        val kb = knowledgebase_edit_text.text.toString()
        val apiKey = api_key_edit_text.text.toString()
        val server = server_edit_text.text.toString()

        return BotAccount(apiKey, accountName, kb, server)
            /* uncomment if contexts and entities should be applied to the bot account
            .apply {
            this.contexts = mutableMapOf()
            this.entities = arrayOf();
        }*/
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        this.accountListener = context as? AccountListener
    }

    override fun onResume() {
        super.onResume()

        start_chat.isEnabled = true
    }

}