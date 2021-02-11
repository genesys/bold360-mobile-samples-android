package com.common.topicsbase

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import com.common.utils.chatForm.FormFieldFactory
import com.common.utils.chatForm.defs.ChatType
import com.common.utils.chatForm.defs.DataKeys
import com.common.utils.chatForm.defs.FormType
import com.integration.core.StateEvent
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

        sampleFormViewModel.chatType.observe(this, Observer<String> { chatType ->

            when (chatType) {
                ChatType.None -> {}
                ChatType.ContinueLast -> restore()
                else -> presentSampleForm()
            }
        })

        super.onCreate(savedInstanceState)
    }

    override val extraDataFields: (() -> List<FormFieldFactory.FormField>)?
    get() = takeIf { hasChatController() }?.let { // -> Means that there are chats to be restored/continued
        { listOf(
            FormFieldFactory.ChatTypeOption(ChatType.ContinueLast),
            FormFieldFactory.SwitchField(FormType.Restoration, DataKeys.Restore))
        }
    }



    /**
     * Reloads the login forms according to the ChatType
     */
    private fun reloadForms() {

        supportFragmentManager.fragments.clear()
        Log.i("RestoreSample", "ChatController hadn't been destructed")

        enableMenu(destructMenu, hasChatController() && !chatController.wasDestructed)

        sampleFormViewModel.reset()

        presentSampleForm()

    }


    @ExperimentalCoroutinesApi
    override fun startSample(savedInstanceState: Bundle?) {

        updateHistoryRepo(targetId = account?.getGroupId())

        if (sampleFormViewModel.restoreRequest && hasChatController()) restore() else createChat()

    }

    override fun onChatUIDetached() {

        // if there are no fragments at the backStack we represent the forms at the Sample context
        if (supportFragmentManager.backStackEntryCount == 0) {
            reloadForms()
        }
    }

    override fun prepareAccount(): Account? {
        return ( account as? BoldAccount )?.apply { info.securedInfo = getSecuredInfo() } ?: account
    }

    override fun onChatStateChanged(stateEvent: StateEvent) {
        super.onChatStateChanged(stateEvent)
        if (stateEvent.state == StateEvent.Ended) removeChatFragment()
    }


        /**
     * Restores the chat for the current account ( if has ChatController )
     */
    private fun restore() {

        if (hasChatController()) {

            chatController.run {

                when {
                    account == null && hasOpenChats() && isActive -> restoreChat()

                    sampleFormViewModel.isRestorable() -> restoreChat( account = prepareAccount() )

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