package com.common.topicsbase

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.common.utils.chatForm.FormFieldFactory
import com.common.utils.chatForm.defs.ChatType
import com.common.utils.chatForm.defs.DataKeys
import com.integration.core.securedInfo
import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.nanoengine.Account
import com.nanorep.sdkcore.utils.getCurrent
import com.nanorep.sdkcore.utils.toast
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
     * Reloads the login forms according to the ChatType
     */
    private fun reloadForms() {

        supportFragmentManager.fragments.clear()
        Log.i("RestoreSample", "ChatController hadn't been destructed")

        enableMenu(destructMenu, hasChatController() && !chatController.wasDestructed)

        sampleFormViewModel.updateChatType(ChatType.ChatSelection)

        presentSampleForm()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sampleFormViewModel.chatType.observe(this, Observer<String> { chatType ->

            when (chatType) {
                ChatType.ChatSelection -> {}
                ChatType.ContinueLast -> restore()
                else -> presentSampleForm()
            }
        })
    }

    @ExperimentalCoroutinesApi
    override fun startSample(savedInstanceState: Bundle?) {

        updateHistoryRepo(targetId = account?.getGroupId())

        super.startSample(savedInstanceState)

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

            supportFragmentManager.backStackEntryCount > 1 && supportFragmentManager.getCurrent()?.tag == CHAT_FORM -> {
                supportFragmentManager
                    .popBackStack(
                        CHAT_FORM,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                supportFragmentManager.executePendingTransactions()

                if (supportFragmentManager.backStackEntryCount == 0) {
                    reloadForms()
                }
            }

            supportFragmentManager.getCurrent()?.tag == topicTitle -> {
                removeChatFragment()
                supportFragmentManager.executePendingTransactions()
            }

            else -> {
                supportFragmentManager.popBackStack()
                finish()
            }

        }
    }

}