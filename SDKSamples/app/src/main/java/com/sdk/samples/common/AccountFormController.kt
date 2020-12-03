package com.sdk.samples.common

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.nanorep.nanoengine.Account
import com.sdk.samples.common.accountForm.AccountForm
import java.lang.ref.WeakReference

interface AccountListener {
    val onAccountData: (account: Account?, isRestore: Boolean) -> Unit
}

interface FormController: AccountListener {
    fun updateChatType(chatType: String?)
}

class AccountFormController(containerRes: Int, wFragmentManager: WeakReference<FragmentManager>
                            , override val onAccountData: (account: Account?, isRestore: Boolean) -> Unit): FormController {

    private val getFragmentManager: () -> FragmentManager? = { wFragmentManager.get() }

    private val accountFormPresenter = AccountFormPresenter(containerRes) { account, isRestore ->
        onAccountData(account, isRestore)
        getFragmentManager()?.popBackStack()
    }

    override fun updateChatType(chatType: String?) {
        getFragmentManager()?.let { fm ->
            chatType?.let { accountFormPresenter.presentAccountForm(fm, chatType) }
                ?: accountFormPresenter.presentRestoreForm(fm)
        }
    }
}

interface FormPresenter: AccountListener{
    val containerRes: Int
    val dataController: DataController?

    fun presentAccountForm(fragmentManager: FragmentManager, chatType: String)
    fun presentRestoreForm(fragmentManager: FragmentManager)
}

class AccountFormPresenter(
    override val containerRes: Int,
    override val onAccountData: (account: Account?, isRestore: Boolean) -> Unit
    ): FormPresenter {

    override val dataController = SharedDataController(onAccountData)

    override fun presentAccountForm(fragmentManager: FragmentManager, chatType: String) {
        presentForm(fragmentManager, AccountForm.newInstance(dataController, chatType), AccountForm.TAG)
    }

    override fun presentRestoreForm(fragmentManager: FragmentManager) {
        presentForm(fragmentManager, RestoreForm.newInstance { chatType, isRestore ->

            dataController.isRestore = isRestore
            presentForm(fragmentManager, AccountForm.newInstance(dataController, chatType), AccountForm.TAG)

        }, RestoreForm.TAG)
    }

    private fun presentForm(fragmentManager: FragmentManager, fragment: Fragment, tag: String) {
        fragmentManager.beginTransaction()
            .replace(containerRes, fragment, tag)
            .commit()
    }
}