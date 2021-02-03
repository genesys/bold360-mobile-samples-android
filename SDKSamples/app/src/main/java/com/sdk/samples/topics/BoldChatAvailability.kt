package com.sdk.samples.topics

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.integration.core.Empty
import com.integration.core.StateEvent
import com.integration.core.UnavailableEvent
import com.integration.core.UnavailableReason
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
        //account.skipPrechat() //Uncomment to start chat immediately without displaying prechat form to the user.

        /*//>>> uncomment to enable passing preconfigured encrypted info, that enables chat creation,
                if your account demands it.
                Replace current text with your Secured string.

           account.info.securedInfo = "this is an encrypted content. Don't read"
        */
    }

    override fun startChat() {
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

    override fun getBuilder(): ChatController.Builder {
        return super.getBuilder().apply {
            this.chatUIProvider(ChatUIProvider(this@BoldChatAvailability).apply {
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

