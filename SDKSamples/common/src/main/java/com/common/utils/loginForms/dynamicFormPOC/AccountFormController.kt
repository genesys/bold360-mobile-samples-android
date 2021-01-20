package com.common.utils.loginForms.dynamicFormPOC

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.common.utils.loginForms.AccountForm
import com.common.utils.loginForms.AccountTypeSelectionForm
import java.lang.ref.WeakReference

interface Validator {
    fun presentError(index: Int, message: String)
}

interface FormController: Validator {
    fun presentForms(onChatTypeChanged: ((chatType: String) ->  Unit)? = null)
}

class AccountFormController(containerRes: Int, wFragmentManager: WeakReference<FragmentManager>, sharedDataHandler: SharedDataHandler):
    FormController, SharedDataHandler by sharedDataHandler {

    val getFragmentManager: () -> FragmentManager? = { wFragmentManager.get() }

    private val accountFormPresenter = AccountFormPresenter(containerRes)

    override fun presentForms(
        onChatTypeChanged: ((chatType: String) ->  Unit)?,
    ) {
        getFragmentManager()?.let { fm ->
            accountFormPresenter.presentForm(fm, onChatTypeChanged)
        }
    }

    override fun presentError(index: Int, message: String) {
        accountFormPresenter.onValidationFailed(index, message)
    }
}

interface FormPresenter {

    val containerRes: Int

    fun onValidationFailed(index: Int, message: String)

    fun presentForm(fragmentManager: FragmentManager, onChatTypeChanged: ((chatType: String) ->  Unit)?)
}

class AccountFormPresenter(override val containerRes: Int): FormPresenter {

    private var validationFailed: ((index: Int, message: String) -> Unit)? = null

    override fun presentForm(fragmentManager: FragmentManager, onChatTypeChanged: ((chatType: String) ->  Unit)?) {
        onChatTypeChanged?.let { presentRestoreForm(fragmentManager, it) } ?: presentAccountForm(fragmentManager)
    }

    private fun presentAccountForm(fragmentManager: FragmentManager) {
        startFormTransaction(fragmentManager, DynamicAccountForm.newInstance().also {
            validationFailed = it.validationFailed
        }, AccountForm.TAG)
    }

    override fun onValidationFailed(index: Int, message: String) {
        validationFailed?.invoke(index, message)
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