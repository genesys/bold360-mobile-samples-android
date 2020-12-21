package com.sdk.samples.topics.base

import android.util.Log
import com.nanorep.nanoengine.Account
import com.nanorep.sdkcore.utils.getCurrent
import com.nanorep.sdkcore.utils.weakRef
import com.sdk.samples.common.accountUtils.ExtraParams.*
import com.sdk.samples.common.loginForms.AccountFormController
import com.sdk.samples.common.loginForms.RestoreForm
import com.sdk.samples.common.loginForms.RestoreState
import com.sdk.samples.common.loginForms.accountForm.AccountForm
import kotlinx.android.synthetic.main.activity_basic.*

abstract class RestorationContinuity : History() {

    // Needed for reloading the relevant forms
    abstract val chatType: String

    /**
     * Reloads the login forms according to the ChatType
     */
    private fun reloadForms(onAccountData: (account: Account?, restoreState: RestoreState, extraData: Map<String, Any?>?) -> Unit) {
        supportFragmentManager.fragments.clear()
        Log.i("RestoreSample", "ChatController hadn't been destructed")
        val accountFormController = AccountFormController(basic_chat_view.id, supportFragmentManager.weakRef())
        accountFormController.updateChatType(chatType, listOf(RestoreSwitch, AsyncExtraData, UsingHistory), onAccountData)
    }

    abstract val onAccountData: (account: Account?, restoreState: RestoreState, extraData: Map<String, Any?>?) -> Unit

    override fun onChatUIDetached() {
        reloadForms(onAccountData)
    }

    override fun onBackPressed() {

        when {
            supportFragmentManager.fragments.isEmpty() -> reloadForms(onAccountData)

            supportFragmentManager.getCurrent()?.tag == RestoreForm.TAG
                    || supportFragmentManager.getCurrent()?.tag == AccountForm.TAG -> finish()

            else -> supportFragmentManager.popBackStack()
        }
    }

}