package com.common.topicsbase

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.common.utils.forms.LoginData
import com.common.utils.forms.defs.ChatType
import com.nanorep.nanoengine.Account
import com.nanorep.sdkcore.utils.getCurrent
import com.nanorep.sdkcore.utils.toast


abstract class RestorationContinuity : History() {

    var restoreRequest = false

    override val account: Account?
        get() = null

    override fun updateLoginData(loginData: LoginData) {
        super.updateLoginData(loginData)
        this.restoreRequest = loginData.restoreRequest
    }

    override val chatType = ChatType.None

    fun addRestorationFields() {
        formFieldsData.apply {
//            add() ....
        }
    }

    /**
     * Reloads the login forms according to the ChatType
     */
    private fun reloadForms() {
        supportFragmentManager.fragments.clear()
        Log.i("RestoreSample", "ChatController hadn't been destructed")

        if (hasChatController()) {
            addRestorationFields()
        }

        accountController.presentForms()
    }

    override fun startChat(savedInstanceState: Bundle?) {

        updateHistoryRepo(targetId = account?.getGroupId())

        if (restoreRequest && hasChatController()) restore() else createChat()

    }

    override fun onChatUIDetached() {

        // if there are no fragments at the backStack we represent the forms at the Sample context
        if (supportFragmentManager.backStackEntryCount == 0) {
            reloadForms()
        }
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
                        account = prepareAccount(
                            getSecuredInfo()
                        )
                    )

                    else -> {
                        context?.let { toast(it, "The Account is not restorable, a new chat had been created", Toast.LENGTH_SHORT) }
                        startChat(accountInfo = prepareAccount(getSecuredInfo()))
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