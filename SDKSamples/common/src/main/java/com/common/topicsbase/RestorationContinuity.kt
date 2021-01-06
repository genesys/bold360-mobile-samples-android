package com.common.topicsbase

import android.util.Log
import android.widget.Toast
import com.common.utils.loginForms.AccountFormController
import com.common.utils.loginForms.RestoreState
import com.common.utils.loginForms.SharedDataHandler
import com.common.utils.loginForms.accountUtils.ChatType
import com.common.utils.loginForms.accountUtils.ExtraParams.*
import com.nanorep.nanoengine.Account
import com.nanorep.sdkcore.utils.getCurrent
import com.nanorep.sdkcore.utils.toast
import com.nanorep.sdkcore.utils.weakRef
import kotlinx.android.synthetic.main.activity_basic.*

abstract class RestorationContinuity : History() {

    protected open val extraFormsParams = mutableListOf(AsyncExtraData, UsingHistory)

    protected open val chatType: String // Needed for reloading the relevant forms
    get() = ChatType.None

    /**
     * Reloads the login forms according to the ChatType
     */
    private fun reloadForms(onAccountData: (account: Account?, restoreState: RestoreState, extraData: Map<String, Any?>?) -> Unit) {
        supportFragmentManager.fragments.clear()
        Log.i("RestoreSample", "ChatController hadn't been destructed")
        val accountFormController = AccountFormController(basic_chat_view.id, supportFragmentManager.weakRef())

        if (hasChatController()) {
            extraFormsParams.add(EnableRestore)
        }

        accountFormController.updateChatType(
            chatType,
            extraFormsParams,
            onAccountData)
    }

    /**
     * The callback from the forms presentation
     */
    private val onAccountData: (account: Account?, restoreState: RestoreState, extraData: Map<String, Any?>?) -> Unit
        get() = { account, restoreState, extraData ->
            chatProvider.account = account
            chatProvider.restoreState = restoreState.apply {

                if ( extraData?.get(SharedDataHandler.ChatType_key) == ChatType.None && hasChatController() )
                     restorable = chatController.hasOpenChats()

            }

            chatProvider.extraData = extraData

            getAccount()?.getGroupId()?.let {
                chatProvider.updateHistoryRepo(targetId = it)
            }

            // Restores the chat when there is a ChatController,
            // else it creates a new ChatController
            if (hasChatController()) chatProvider.restore() else super.startChat()
        }

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