package com.sdk.samples.common

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.nanorep.nanoengine.Account
import com.sdk.samples.common.accountForm.AccountForm
import java.lang.ref.WeakReference

interface AccountListener {
    var onAccountData: ((account: Account?, isRestore: Boolean) -> Unit)?
}

interface FormController {
    fun updateChatType(chatType: String?, onAccountData: (account: Account?, isRestore: Boolean) -> Unit)
}

class AccountFormController(containerRes: Int, wFragmentManager: WeakReference<FragmentManager>): FormController {

    private val getFragmentManager: () -> FragmentManager? = { wFragmentManager.get() }

    private val accountFormPresenter = AccountFormPresenter(containerRes)

    override fun updateChatType(chatType: String?, onAccountData: (account: Account?, isRestore: Boolean) -> Unit) {

        accountFormPresenter.onAccountData = { account, isRestore ->
            onAccountData.invoke(account, isRestore)
            getFragmentManager()?.popBackStack()
        }

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

class AccountFormPresenter(override val containerRes: Int): FormPresenter {

    override val dataController = SharedDataController()

    override var onAccountData: ((account: Account?, isRestore: Boolean) -> Unit)?
    set(value) {
        dataController.onAccountData = value
    }
    get() = dataController.onAccountData

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
            .addToBackStack(null)
            .commit()
    }
}