package com.sdk.samples.topics.base

import androidx.lifecycle.ViewModel
import com.nanorep.nanoengine.Account
import com.sdk.utils.loginForms.RestoreState

class SamplesViewModel : ViewModel() {

    val accountProvider = AccountHolder()

    companion object {

        private var myViewModel: SamplesViewModel? = null

        @Synchronized
        fun getInstance() : SamplesViewModel {
            if (myViewModel == null) {
                myViewModel = SamplesViewModel()
            }
            return myViewModel!!
        }
    }

    inner class AccountHolder: com.sdk.utils.chat.AccountHolder {
        override var account: Account? = null

        override var extraData: Map<String, Any?>? = null

        override var restoreState = RestoreState()
    }
}
