package com.sdk.samples.topics

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.integration.core.Empty
import com.integration.core.StateEvent
import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.convesationui.structure.providers.ChatUIProvider
import com.nanorep.nanoengine.model.conversation.SessionInfoKeys
import com.nanorep.sdkcore.utils.Event
import com.sdk.samples.R

open class BoldChatAvailability : BoldChat() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadAvailabilityCheck()
    }

    private val availabilityViewModel: CheckAvailabilityViewModel by lazy {
        ViewModelProvider(this).get(CheckAvailabilityViewModel::class.java)
    }

    private fun loadAvailabilityCheck() {

        availabilityViewModel.apply {
            account = getAccount() as BoldAccount

            observeResults(this@BoldChatAvailability,
                Observer { results ->
                    results?.run {
                        if (isAvailable) {
                            departmentId.takeIf { it > 0 }?.let {
                                account.addExtraData(SessionInfoKeys.Department to results.departmentId)
                            }

                            prepareAccount(account)

                            createChat()
                        }
                    }
                })
        }

        supportFragmentManager.beginTransaction()
            .add(R.id.chat_view, BoldAvailability(), AvailabilityTag)
            .addToBackStack(AvailabilityTag)
            .commit()
    }

    protected open fun prepareAccount(account: BoldAccount) {
        account.skipPrechat()
    }

    override fun startChat() {
    }

    override fun onChatStateChanged(stateEvent: StateEvent) {
        super.onChatStateChanged(stateEvent)

        when (stateEvent.state) {
            StateEvent.Idle, StateEvent.Unavailable -> {
                removeChatFragment()

                //-> trigger the observer that was assigned to this viewModel to trogger
                //  refresh of chat availability status.
                availabilityViewModel.refresh(Event(Empty))
            }
        }
    }

    override fun getBuilder(): ChatController.Builder {
        return super.getBuilder().apply {
            this.chatUIProvider(ChatUIProvider(this@BoldChatAvailability).apply {
                chatInputUIProvider.uiConfig.showUpload = false
            })
        }
    }

    companion object {
        const val AvailabilityTag = "AvailabilityTag"
    }
}

