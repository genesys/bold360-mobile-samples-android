package com.common.topicsbase

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import com.common.utils.chat_form.FormFieldFactory
import com.common.utils.chat_form.defs.ChatType
import com.common.utils.chat_form.defs.DataKeys
import com.integration.core.securedInfo
import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.nanoengine.Account
import com.nanorep.sdkcore.utils.getCurrent
import com.nanorep.sdkcore.utils.toast
import kotlinx.coroutines.ExperimentalCoroutinesApi

abstract class RestorationContinuity : History() {

    @ChatType
    override var chatType = ChatType.None

    override fun onCreate(savedInstanceState: Bundle?) {
        loginFormViewModel.chatType.observe(this, Observer<String> { chatType ->

            if (chatType == ChatType.ContinueLast) {
                restore()
            } else {
                this.chatType = chatType
                presentForms()
            }

        })
        super.onCreate(savedInstanceState)
    }

    private fun addRestorationFields() {
        extraDataFields = {
            listOf(
                FormFieldFactory.ChatTypeOption(ChatType.ContinueLast),
                FormFieldFactory.SwitchField(DataKeys.Restore)
            )
        }
    }

    /**
     * Reloads the login forms according to the ChatType
     */
    private fun reloadForms() {

        supportFragmentManager.fragments.clear()
        Log.i("RestoreSample", "ChatController hadn't been destructed")

        chatType = ChatType.None

        if (hasChatController()) {
            addRestorationFields()
        }

        presentForms()
        extraDataFields = { listOf() }

    }


    @ExperimentalCoroutinesApi
    override fun startChat(savedInstanceState: Bundle?) {

        updateHistoryRepo(targetId = account?.getGroupId())

        if (loginFormViewModel.restoreRequest && hasChatController()) restore() else createChat()

    }

    override fun onChatUIDetached() {

        // if there are no fragments at the backStack we represent the forms at the Sample context
        if (supportFragmentManager.backStackEntryCount == 0) {
            reloadForms()
        }
    }

    override fun prepareAccount(): Account? {
        return (account as? BoldAccount)?.apply { info.securedInfo = getSecuredInfo() } ?: account
    }


    /**
     * Restores the chat for the current account ( if has ChatController )
     */
    private fun restore() {

        if (hasChatController()) {

            chatController.run {

                when {
                    account == null && hasOpenChats() && isActive -> restoreChat()

                    accountController.isRestorable(baseContext, chatType) -> restoreChat(
                        account = prepareAccount()
                    )

                    else -> {
                        context?.let { toast(it, "The Account is not restorable, a new chat had been created", Toast.LENGTH_SHORT) }
                        startChat(accountInfo = prepareAccount())
                    }
                }
            }

        } else {
            Log.e(
                "ChatHolder",
                "Failed to restore chat, hasChatController() must be checked first"
            )
        }
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