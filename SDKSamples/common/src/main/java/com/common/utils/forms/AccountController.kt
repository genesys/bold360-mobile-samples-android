package com.common.utils.forms

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import java.lang.ref.WeakReference

interface FormController {
    fun presentForms(onChatTypeChanged: ((chatType: String) ->  Unit)? = null)
}

class AccountController(containerRes: Int, wFragmentManager: WeakReference<FragmentManager>, sharedDataHandler: SharedDataHandler):
    FormController, SharedDataHandler by sharedDataHandler {

    val getFragmentManager: () -> FragmentManager? = { wFragmentManager.get() }

    private val accountFormPresenter = AccountFormPresenter(containerRes)

    override fun presentForms(
        onChatTypeChanged: ((chatType: String) ->  Unit)?
    ) {
        getFragmentManager()?.let { fm ->
            accountFormPresenter.presentForm(fm, onChatTypeChanged)
        }
    }
}

interface FormPresenter {

    val containerRes: Int

    fun presentForm(fragmentManager: FragmentManager, onChatTypeChanged: ((chatType: String) ->  Unit)?)
}

class AccountFormPresenter(override val containerRes: Int): FormPresenter {

    override fun presentForm(fragmentManager: FragmentManager, onChatTypeChanged: ((chatType: String) ->  Unit)?) {
        onChatTypeChanged?.let { presentRestoreForm(fragmentManager, it) } ?: presentAccountForm(fragmentManager)
    }

    private fun presentAccountForm(fragmentManager: FragmentManager) {
        startFormTransaction(fragmentManager, AccountForm.newInstance().also {}, AccountForm.TAG)
    }

    private fun presentRestoreForm(fragmentManager: FragmentManager, onChatTypeChanged: ((chatType: String) ->  Unit)) {
        val fragment = AccountTypeSelectionForm.newInstance { chatType ->
            onChatTypeChanged.invoke(chatType)
            presentAccountForm(fragmentManager)
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