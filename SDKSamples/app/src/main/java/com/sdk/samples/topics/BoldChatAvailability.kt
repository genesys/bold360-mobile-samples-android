package com.sdk.samples.topics

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.integration.core.Empty
import com.integration.core.StateEvent
import com.integration.core.UnavailableEvent
import com.integration.core.UnavailableReason
import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.convesationui.structure.providers.ChatUIProvider
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.model.conversation.SessionInfoKeys
import com.nanorep.sdkcore.utils.Event
import com.sdk.samples.R

open class BoldChatAvailability : BoldChat() {

    private val availabilityViewModel: CheckAvailabilityViewModel by viewModels()

    private fun loadAvailabilityCheck() {

        availabilityViewModel.apply {

            observeResults(this@BoldChatAvailability,
                Observer { results ->
                    results?.run {
                        if (isAvailable) {
                            departmentId.takeIf { it > 0 }?.let {
                                account.addExtraData(SessionInfoKeys.Department to results.departmentId)
                            }

                            createChat()
                        }
                    }
                })
        }

        availabilityViewModel.account = account as BoldAccount

        supportFragmentManager.beginTransaction()
            .add(R.id.basic_chat_view, BoldAvailability.newInstance(), AvailabilityTag)
            .addToBackStack(AvailabilityTag)
            .commit()
    }

    override fun prepareAccount(): Account {
        return (super.prepareAccount() as BoldAccount).apply {
            //skipPrechat() //Uncomment to start chat immediately without displaying prechat form to the user.

            /*//>>> uncomment to enable passing preconfigured encrypted info, that enables chat creation,
                    if your account demands it.
                    Replace current text with your Secured string.

               info.securedInfo = "this is an encrypted content. Don't read"
            */
        }
    }

    override fun startChat(savedInstanceState: Bundle?) {
        loadAvailabilityCheck()
    }

    override fun onChatStateChanged(stateEvent: StateEvent) {
        super.onChatStateChanged(stateEvent)

        when (stateEvent.state) {
            StateEvent.Idle -> removeChatFragment()

            StateEvent.Unavailable -> {
                val unavailableEvent = stateEvent as? UnavailableEvent
                takeIf { unavailableEvent?.isFollowedByForm != true }?.removeChatFragment()

                Log.d("boldChat", stateEvent.state +", reason: ${unavailableEvent?.unavailableReason?:UnavailableReason.Unknown}")
                //-> trigger the observer that was assigned to this viewModel to trigger
                //  refresh of chat availability status.
                availabilityViewModel.refresh(Event(Empty))
            }
        }
    }

    override fun getChatBuilder(): ChatController.Builder? {
        return super.getChatBuilder()?.apply {
            chatUIProvider(ChatUIProvider(this@BoldChatAvailability).apply {
                chatInputUIProvider.uiConfig.showUpload = false

                // uncomment the following to hide the "cancel" option on the queuebar:
                // queueCmpUIProvider.queueUIConfig.enableCancel = false
            })
        }
    }

    companion object {
        const val AvailabilityTag = "AvailabilityTag"
    }
}

