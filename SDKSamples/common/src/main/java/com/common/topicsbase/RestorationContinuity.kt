package com.common.topicsbase

import android.util.Log
import android.widget.Toast
import com.nanorep.nanoengine.Account
import com.nanorep.sdkcore.utils.getCurrent
import com.nanorep.sdkcore.utils.toast
import com.nanorep.sdkcore.utils.weakRef
import com.common.utils.loginForms.accountUtils.ExtraParams.*
import com.common.utils.loginForms.AccountFormController
import com.common.utils.loginForms.RestoreState
import kotlinx.android.synthetic.main.activity_basic.*

abstract class RestorationContinuity : History() {

    abstract val chatType: String // Needed for reloading the relevant forms

    /**
     * Reloads the login forms according to the ChatType
     */
    private fun reloadForms(onAccountData: (account: Account?, restoreState: RestoreState, extraData: Map<String, Any?>?) -> Unit) {
        supportFragmentManager.fragments.clear()
        Log.i("RestoreSample", "ChatController hadn't been destructed")
        val accountFormController = AccountFormController(basic_chat_view.id, supportFragmentManager.weakRef())
        accountFormController.updateChatType(chatType, listOf(RestoreSwitch, AsyncExtraData, UsingHistory), onAccountData)
    }

    /**
     * The callback from the forms presentation
     */
    abstract val onAccountData: (account: Account?, restoreState: RestoreState, extraData: Map<String, Any?>?) -> Unit

    override fun onChatUIDetached() {

        // if there are no fragments at the backStack we represent the forms at the Sample context
        if (supportFragmentManager.backStackEntryCount == 0) {
            reloadForms(onAccountData)
        }
    }

    fun onRestoreFailed(reason: String) {
        toast( baseContext, reason, Toast.LENGTH_SHORT )
        onBackPressed()
    }

    override fun onBackPressed() {

        when {

            supportFragmentManager.fragments.isNotEmpty()
                    && supportFragmentManager.getCurrent()?.tag == topicTitle -> {
                removeChatFragment()
                supportFragmentManager.executePendingTransactions()
            }

            else -> {
                supportFragmentManager.popBackStackImmediate()
                if (!isFinishing) finishIfLast()
            }

        }
    }

}