package com.common.utils.loginForms

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.common.utils.loginForms.accountUtils.ChatType
import com.common.utils.loginForms.accountUtils.ExtraParams.EnableRestore
import java.lang.ref.WeakReference

/**
 * @param restoreRequest is true if the user requested to restore the chat
 * @param restorable is true if the restoration is possible for the account
*/
class RestoreState(val restoreRequest: Boolean = false, var restorable: Boolean = false)

interface FormController {
    fun updateChatType(
        chatType: String,
        extraParams: List<String>,
    )
}

class AccountFormController(containerRes: Int, wFragmentManager: WeakReference<FragmentManager>):
    FormController {

    private val getFragmentManager: () -> FragmentManager? = { wFragmentManager.get() }

    private val accountFormPresenter = AccountFormPresenter(containerRes)

    override fun updateChatType(
        chatType: String,
        extraParams: List<String>,
    ) {
        getFragmentManager()?.let { fm ->
            accountFormPresenter.extraParams = extraParams
            accountFormPresenter.presentForm(fm, chatType)
        }
    }
}

interface FormPresenter {
    val containerRes: Int

    var extraParams: List<String>

    fun presentForm(fragmentManager: FragmentManager, chatType: String)
}

class AccountFormPresenter(override val containerRes: Int): FormPresenter {

    override lateinit var extraParams: List<String>

    override fun presentForm(fragmentManager: FragmentManager, chatType: String) {

        when (chatType) {
            ChatType.None -> presentRestoreForm(fragmentManager)
            else -> presentAccountForm(fragmentManager, chatType)
        }
    }

    private fun presentAccountForm(
        fragmentManager: FragmentManager,
        chatType: String
    ) {
        startFormTransaction(
            fragmentManager,
            AccountForm.newInstance(chatType),
            AccountForm.TAG
        )
    }

    private fun presentRestoreForm(fragmentManager: FragmentManager) {
        val fragment = AccountTypeSelectionForm.newInstance(extraParams.contains(EnableRestore)) { chatType ->
            presentAccountForm(fragmentManager, chatType)
        }

        startFormTransaction(
            fragmentManager,
            fragment,
            AccountTypeSelectionForm.TAG
        )
    }

    private fun startFormTransaction(fragmentManager: FragmentManager, fragment: Fragment, tag: String) {
        if (!fragmentManager.isStateSaved) {

            fragmentManager.beginTransaction()
                .add(containerRes, fragment, tag)
                .addToBackStack(LOGIN_FORM)
                .commit()
        }
    }

    companion object {
        const val LOGIN_FORM = "loginForm"
    }
}