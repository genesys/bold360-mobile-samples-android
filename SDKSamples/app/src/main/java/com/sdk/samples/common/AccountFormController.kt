package com.sdk.samples.common

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.sdk.samples.common.ChatType.NONE
import com.sdk.samples.common.accountForm.AccountForm
import java.lang.ref.WeakReference

interface AccountListener {
    var onAccountData: ((account: Map<String, Any?>?, isRestore: Boolean) -> Unit)?
}

interface FormController {
    fun updateChatType(chatType: String?, extraParams: List<String>?, onAccountData: (account: Map<String, Any?>?, isRestore: Boolean) -> Unit)
}

class AccountFormController(containerRes: Int, wFragmentManager: WeakReference<FragmentManager>): FormController {

    private val getFragmentManager: () -> FragmentManager? = { wFragmentManager.get() }

    private val accountFormPresenter = AccountFormPresenter(containerRes)

    override fun updateChatType(chatType: String?, extraParams: List<String>?, onAccountData: (account: Map<String, Any?>?, isRestore: Boolean) -> Unit) {

        accountFormPresenter.onAccountData = { account, isRestore ->
            onAccountData.invoke(account, isRestore)
        }

        getFragmentManager()?.let { fm ->
            accountFormPresenter.extraParams = extraParams
            chatType?.takeIf { it != ChatType.NONE }?.let { accountFormPresenter.presentAccountForm(fm, chatType) }
                ?: accountFormPresenter.presentRestoreForm(fm)
        }
    }
}

interface FormPresenter: AccountListener{
    val containerRes: Int
    val dataController: DataController?

    var extraParams: List<String>?

    fun presentAccountForm(
        fragmentManager: FragmentManager,
        chatType: String
    )
    fun presentRestoreForm(fragmentManager: FragmentManager)
}

class AccountFormPresenter(override val containerRes: Int): FormPresenter {

    override val dataController = SharedDataController()

    override var extraParams: List<String>? = null

    override var onAccountData: ((account: Map<String, Any?>?, isRestore: Boolean) -> Unit)?
        set(value) {
        dataController.onAccountData = value
    }
    get() = dataController.onAccountData

    override fun presentAccountForm(
        fragmentManager: FragmentManager,
        chatType: String
    ) {
        presentForm(fragmentManager, AccountForm.newInstance(dataController, chatType, extraParams), AccountForm.TAG)
    }

    override fun presentRestoreForm(fragmentManager: FragmentManager) {
        val fragment = RestoreForm.newInstance()

        fragment.onChatRestore = { chatType, isRestore ->
            dataController.isRestore = isRestore

            if (chatType != NONE) {
                presentForm(
                    fragmentManager,
                    AccountForm.newInstance(dataController, chatType, extraParams),
                    AccountForm.TAG
                )

            } else {
                onAccountData?.invoke(null, isRestore)
            }
        }

        presentForm(fragmentManager, fragment, RestoreForm.TAG)
    }

    private fun presentForm(fragmentManager: FragmentManager, fragment: Fragment, tag: String) {
        fragmentManager.beginTransaction()
            .replace(containerRes, fragment, tag)
            .addToBackStack(null)
            .commit()
    }
}