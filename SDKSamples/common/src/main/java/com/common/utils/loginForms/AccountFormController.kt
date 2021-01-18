package com.common.utils.loginForms

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.common.utils.loginForms.dynamicFormPOC.defs.ChatType
import com.nanorep.nanoengine.Account
import java.lang.ref.WeakReference

/**
 * @param restoreRequest is true if the user requested to restore the chat
 * @param restorable is true if the restoration is possible for the account
*/
class RestoreState(val restoreRequest: Boolean = false, var restorable: Boolean = false)

interface FormController {
    fun updateChatType(chatType: String)
}

class AccountFormController(containerRes: Int, wFragmentManager: WeakReference<FragmentManager>):
    FormController {

    val getFragmentManager: () -> FragmentManager? = { wFragmentManager.get() }

    val accountFormPresenter = AccountFormPresenter(containerRes)

    override fun updateChatType(
        chatType: String,
    ) {
        getFragmentManager()?.let { fm ->
            accountFormPresenter.presentForm(fm, chatType)
        }
    }

    inline fun <reified T: Account>login() {
        getFragmentManager()?.let { fm ->
            accountFormPresenter._presentForm(T::class.java, fm)
        }
    }
}

interface FormPresenter {

    val containerRes: Int

    fun presentForm(fragmentManager: FragmentManager, chatType: String)
}

class AccountFormPresenter(override val containerRes: Int): FormPresenter {

    override fun presentForm(fragmentManager: FragmentManager, chatType: String) {

        when (chatType) {
            ChatType.None -> presentRestoreForm(fragmentManager)
            else -> presentAccountForm(fragmentManager, chatType)
        }
    }

    fun <T: Account>_presentForm(c: Class<T>, fragmentManager: FragmentManager) {
        startFormTransaction(fragmentManager, DynamicAccountForm.newInstance(), AccountForm.TAG)
    }

    private fun presentAccountForm(
        fragmentManager: FragmentManager,
        chatType: String
    ) {
       /* startFormTransaction(
            fragmentManager,
            AccountForm.newInstance(chatType),
            AccountForm.TAG
        )*/
    }

    private fun presentRestoreForm(fragmentManager: FragmentManager) {
        val fragment = AccountTypeSelectionForm.newInstance { chatType ->
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