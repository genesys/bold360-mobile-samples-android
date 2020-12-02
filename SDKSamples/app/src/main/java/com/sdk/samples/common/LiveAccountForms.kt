package com.sdk.samples.common

import com.nanorep.nanoengine.Account
import com.sdk.samples.R
import com.sdk.samples.topics.Accounts

open class LiveAccountFragment(dataHandler: AccountDataHandler) : AccountFragment(dataHandler) {

    override val formLayoutRes: Int
        get() = R.layout.frgment_live_form

    override fun fillFields() {}

    override fun validateFormData(): Account? {
        return Accounts.defaultBoldAccount
    }

    override fun updateAccountCatch(account: Account) {}

    companion object {
        fun newInstance(dataHandler: AccountDataHandler): LiveAccountFragment {
            return LiveAccountFragment(dataHandler)
        }
    }
}

class AsyncAccountForm(dataHandler: AccountDataHandler) : LiveAccountFragment(dataHandler) {

    override fun validateFormData(): Account? {
        return Accounts.defaultAsyncAccount
    }

    override fun updateAccountCatch(account: Account) {}

    companion object {
        fun newInstance(dataHandler: AccountDataHandler): AsyncAccountForm {
            return AsyncAccountForm(dataHandler)
        }
    }
}