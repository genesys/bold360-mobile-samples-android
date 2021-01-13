package com.common.topicsbase

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.common.utils.loginForms.AccountFormController
import com.common.utils.loginForms.accountUtils.ChatType
import com.common.utils.loginForms.accountUtils.FormsParams.AsyncExtraData
import com.common.utils.loginForms.accountUtils.FormsParams.EnableRestore
import com.nanorep.sdkcore.utils.getCurrent
import com.nanorep.sdkcore.utils.toast
import com.nanorep.sdkcore.utils.weakRef
import com.sdk.common.R

abstract class RestorationContinuity : History() {

    override var formsParams = AsyncExtraData

    override val chatType: String
        get() = ChatType.None

    /**
     * Reloads the login forms according to the ChatType
     */
    private fun reloadForms() {
        supportFragmentManager.fragments.clear()
        Log.i("RestoreSample", "ChatController hadn't been destructed")
        val accountFormController = AccountFormController(R.id.basic_chat_view, supportFragmentManager.weakRef())

        if (hasChatController()) {
            addFormsParam(EnableRestore)
        }

        accountFormController.updateChatType(chatType)
    }

    override fun startChat(savedInstanceState: Bundle?) {

        getAccount()?.getGroupId()?.let {
            updateHistoryRepo(targetId = it)
        }

        super.startChat(savedInstanceState)

    }

    override fun onChatUIDetached() {

        // if there are no fragments at the backStack we represent the forms at the Sample context
        if (supportFragmentManager.backStackEntryCount == 0) {
            reloadForms()
        }
    }

    fun onRestoreFailed(reason: String) {
        toast( baseContext, reason, Toast.LENGTH_SHORT )
        onBackPressed()
    }

    override fun onBackPressed() {

        when {

            supportFragmentManager.fragments.isEmpty() || supportFragmentManager.getCurrent()?.tag == topicTitle -> {
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