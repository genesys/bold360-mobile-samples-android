package com.sdk.samples.common

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.nanorep.convesationui.async.AsyncAccount
import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.bot.BotAccount
import com.sdk.samples.R
import com.sdk.samples.common.ChatType.AsyncChat
import com.sdk.samples.common.ChatType.LiveChat
import kotlinx.android.synthetic.main.frgment_account_form.*
import kotlinx.android.synthetic.main.restore_layout.*
import java.lang.ref.WeakReference

interface AccountController {
    val onAccountReady: (account: Account, isRestore: Boolean) -> Unit
}

class AccountFormController(@ChatType chatType: String, containerRes: Int,  fragmentManager: WeakReference<FragmentManager>, override val onAccountReady: (account: Account, isRestore: Boolean) -> Unit): AccountController {
    val accountFormPresenter = fragmentManager.get()?.let { AccountFormPresenter(chatType, containerRes, it, onAccountReady ) }
}

interface FormPresenter: AccountController{
    val containerRes: Int
    val fragmentManager: FragmentManager?
    val accountDataHandler: AccountDataHandler?
    @ChatType var chatType: String?

    fun presentAccountForm(chatType: String)
    fun presentRestoreForm(onChatRestore: (chatType: String, isRestore: Boolean)-> Unit)
}

class AccountFormPresenter(
    override var chatType: String?,
    override val containerRes: Int,
    override val fragmentManager: FragmentManager,
    override val onAccountReady: (account: Account, isRestore: Boolean) -> Unit
): FormPresenter {

    override val accountDataHandler = SharedPrefsDataHandler(chatType, onAccountReady)

    init {
        chatType?.let { presentAccountForm(it) } ?: kotlin.run {

            presentRestoreForm { chatType, isRestore ->
                fragmentManager.popBackStack()
                this.chatType = chatType
                accountDataHandler.isRestore = isRestore
                presentAccountForm(chatType)
            }

        }
    }

    override fun presentAccountForm(chatType: String) {
        presentForm(AccountFragment.newInstance(accountDataHandler))
    }

    override fun presentRestoreForm(onChatRestore: (chatType: String, isRestore: Boolean) -> Unit) {
        presentForm(RestoreFragment.newInstance(onChatRestore))
    }

    private fun presentForm(fragment: Fragment) {
        fragmentManager.beginTransaction()
            .replace(containerRes, fragment)
            .commit()
    }
}

class RestoreFragment(val onChatRestore: (chatType: String, isRestore: Boolean) -> Unit) : Fragment() {

    @ChatType private val selectedChatType: String
        get() = getCheckedRadio()?.tag?.toString() ?: ChatType.BotChat

    companion object {
        fun newInstance(onChatRestore: (chatType: String, isRestore: Boolean) -> Unit): RestoreFragment {
            return RestoreFragment(onChatRestore)
        }
    }

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
}


interface AccountFormDelegate {
    val dataHandler: AccountDataHandler

    /**
     * Takes the fields data from the shared properties
     */
    fun fillFields()

    /**
     * Validates the form data
     * returns Account if the data is valid else null
     */
    fun validateFormData(): Account?
}

abstract class AccountFragment(override val dataHandler: AccountDataHandler) : Fragment(), AccountFormDelegate {

    abstract val formLayoutRes: Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(formLayoutRes, container, false)
    }

    private fun validateAndUpdate (): Account? {
        return validateFormData()?.also {
            dataHandler.updateAccount(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        fillFields()

        start_chat.setOnClickListener {
            validateAndUpdate()?.run { dataHandler.onSubmit(this) }
        }
    }

    companion object {
        fun newInstance(dataHandler: AccountDataHandler, chatType: String): AccountFragment {
            return when (chatType) {
                    ChatType.LiveChat -> LiveAccountFragment.newInstance(dataHandler)
                    ChatType.AsyncChat -> AsyncAccountForm.newInstance(dataHandler)
                    else -> BotAccountForm.newInstance(dataHandler)
            }
        }
    }
}