package com.common.topicsbase

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.common.utils.loginForms.AccountFormController
import com.common.utils.loginForms.accountUtils.ChatType
import com.common.utils.loginForms.accountUtils.ExtraParams.AsyncExtraData
import com.common.utils.loginForms.accountUtils.ExtraParams.EnableRestore
import com.nanorep.sdkcore.utils.getCurrent
import com.nanorep.sdkcore.utils.toast
import com.nanorep.sdkcore.utils.weakRef
import com.sdk.common.R

abstract class RestorationContinuity : History() {

    override val extraFormsParams = mutableListOf(AsyncExtraData)

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
            extraFormsParams.add(EnableRestore)
        }

        accountFormController.updateChatType(chatType, extraFormsParams)
    }

    override fun startChat(savedInstanceState: Bundle?) {

        getAccount()?.getGroupId()?.let {
            chatProvider.updateHistoryRepo(targetId = it)
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