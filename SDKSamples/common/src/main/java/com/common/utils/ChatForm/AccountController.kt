package com.common.utils.ChatForm

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import java.lang.ref.WeakReference

interface FormController {
    fun presentForms(/*isChatSelection: Boolean = false, onChatTypeChanged: ((chatType: String) ->  Unit)? = null*/)
}

class AccountController(containerRes: Int, wFragmentManager: WeakReference<FragmentManager>, sharedDataHandler: SharedDataHandler):
    FormController, SharedDataHandler by sharedDataHandler {

    val getFragmentManager: () -> FragmentManager? = { wFragmentManager.get() }

    private val accountFormPresenter = AccountFormPresenter(containerRes)

    override fun presentForms() {
        getFragmentManager()?.let { fm ->
            accountFormPresenter.presentForm(fm)
        }
    }
}

interface FormPresenter {

    val containerRes: Int

    fun presentForm(fragmentManager: FragmentManager)
}

class AccountFormPresenter(override val containerRes: Int): FormPresenter {

    override fun presentForm(fragmentManager: FragmentManager) {
        startFormTransaction(fragmentManager, ChatForm.newInstance(), ChatForm.TAG)
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