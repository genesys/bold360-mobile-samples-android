package com.common.topicsbase

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.common.utils.chatForm.FormFieldFactory
import com.common.utils.chatForm.defs.ChatType
import com.common.utils.chatForm.defs.DataKeys
import com.common.utils.toast
import com.integration.core.securedInfo
import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.nanoengine.Account
import com.nanorep.sdkcore.utils.getCurrent
import com.sdk.common.R
import kotlinx.coroutines.ExperimentalCoroutinesApi

abstract class RestorationContinuity : History() {

    @ChatType
    override var chatType = ChatType.ChatSelection

    override val extraDataFields: (() -> List<FormFieldFactory.FormField>)?
        get() =
            {
                mutableListOf<FormFieldFactory.FormField>().apply {

                    if (hasChatController()) {
                        add(FormFieldFactory.Option( ChatType.ChatSelection, DataKeys.ChatTypeKey, ChatType.ContinueLast ))
                        add(FormFieldFactory.RestoreSwitch(false))
                    }
                }
            }


    /**
     * Reloads the account details forms according to the ChatType
     */
    private fun reloadForms() {

        supportFragmentManager.fragments.clear()
        Log.i("RestoreSample", "ChatController hadn't been destructed")

        sampleFormViewModel.updateChatType(chatType)

        enableMenu(destructMenu, hasChatController() && !chatController.wasDestructed)

        presentSampleForm()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (chatType == ChatType.ChatSelection) { // -> Observes chat type changes from the ChatSelection form:

            sampleFormViewModel.chatType.observe(this, Observer { chatType ->
                when (chatType) {
                    ChatType.ChatSelection -> {} // Being handled already at the SampleActivity
                    ChatType.ContinueLast -> restore()
                    else -> presentSampleForm()
                }
            })
        }
    }

    @ExperimentalCoroutinesApi
    override fun startSample(isStateSaved: Boolean) {

        updateHistoryRepo(targetId = account?.getGroupId())

        super.startSample(isStateSaved)

    }

    override fun createChat() {
        if ( sampleFormViewModel.restoreRequest && hasChatController() ) restore() else super.createChat()
    }

    override fun onChatUIDetached() {

        // if there are no fragments at the backStack we represent the forms at the Sample context
        if (supportFragmentManager.fragments.isEmpty() && supportFragmentManager.backStackEntryCount == 0) {
            reloadForms()
        }
    }

    override fun prepareAccount(): Account? {
        return ( account as? BoldAccount )?.apply { info.securedInfo = getSecuredInfo() } ?: account
    }

    /**
     * Restores the chat for the current account ( if has ChatController )
     */
    private fun restore() {

        if (hasChatController()) {

            chatController.run {

                when {
                    account == null && hasOpenChats() && isActive -> restoreChat()

                    sampleFormViewModel.checkRestorable() -> restoreChat( account = prepareAccount() )

                    else -> {
                        toast(getString(R.string.chat_restoration_error), Toast.LENGTH_SHORT)
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

        if( supportFragmentManager.backStackEntryCount > 1 ) {

            when (supportFragmentManager.getCurrent()?.tag) {

                CHAT_FORM -> { // -> Chat form is presented (2 in stack)
                    supportFragmentManager
                        .popBackStack(
                            CHAT_FORM,
                            FragmentManager.POP_BACK_STACK_INCLUSIVE
                        )

                    supportFragmentManager.executePendingTransactions()

                    if ( supportFragmentManager.backStackEntryCount == 0 ) { // -> No more form fragments in stack => represent the chat selection
                        reloadForms()
                    }
                }

                else -> { // -> Another fragment in the stuck ( Article fragment, Live form etc. )
                    supportFragmentManager.popBackStackImmediate()
                    finishIfLast()
                }
            }

        } else if ( supportFragmentManager.backStackEntryCount > 0 && supportFragmentManager.getCurrent()?.tag == topicTitle ) {  // -> Chat fragment is presented

            if (chatController.hasOpenChats()) {
                reloadForms()
            }

            removeChatFragment()
            supportFragmentManager.executePendingTransactions()

        }  else { // -> No fragments in stack
            super.onBackPressed()
        }
    }

}