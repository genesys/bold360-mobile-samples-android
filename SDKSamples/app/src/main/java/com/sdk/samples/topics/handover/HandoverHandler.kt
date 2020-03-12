package com.sdk.samples.topics.handover

import android.content.Context
import android.os.Handler
import android.util.Log
import com.integration.core.*
import com.nanorep.convesationui.structure.HandoverHandler
import com.nanorep.convesationui.structure.components.ComponentType
import com.nanorep.convesationui.views.autocomplete.ChatInputData
import com.nanorep.nanoengine.AccountInfo
import com.nanorep.nanoengine.model.configuration.ChatFeatures
import com.nanorep.nanoengine.model.conversation.providerConfig
import com.nanorep.nanoengine.model.conversation.statement.IncomingStatement
import com.nanorep.nanoengine.model.conversation.statement.OutgoingStatement
import com.nanorep.sdkcore.model.ChatStatement
import com.nanorep.sdkcore.model.StatusOk
import com.nanorep.sdkcore.model.StatusPending
import com.nanorep.sdkcore.utils.Event
import com.nanorep.sdkcore.utils.NRError
import com.nanorep.sdkcore.utils.getAs
import java.util.*

class MyHandoverHandler(context: Context) : HandoverHandler(context) {

    private var handlerConfiguration: String? = null
    private val handler = Handler()

    override fun handleEvent(name: String, event: Event) {
        when (name) {

            UserAction -> if (event is UserEvent) {
                val userEvent = event
                if (userEvent.action == UserEvent.ActionLink) {
                    passEvent(userEvent)
                }
            }

            Message -> {
                val statement = event.data.getAs<ChatStatement>()
                statement?.let { injectElement(it) }
            }

            State -> handleState(event.getAs<StateEvent>())
            Error -> handleError(event)
            else -> passEvent(event)
        }

        // If there is any post event function, invoke it after the event handling
        val postEvent: Function0<*>? = event.postEvent
        postEvent?.invoke()
    }

    private fun handleError(event: Event) {

        (event as? ErrorEvent)?.takeIf { it.code == NRError.StatementError }?.run {

            val request = data.getAs<ChatStatement>() ?: kotlin.run {  data.getAs<NRError>()?.run { data.getAs<ChatStatement>() } }

            request?.let { updateStatus(it, StatusPending) }
        }

        passEvent(event)
    }

    override fun enableChatInput( enable: Boolean, cmpData: ChatInputData? ) {

        super.enableChatInput(enable,  ChatInputData().apply {

            onSend = if (enable) { charSequence: CharSequence ->
                post(OutgoingStatement(charSequence.toString()))
            } else null

            /* not release yet on 3.6.0 rcs
            onSendInput = if (enable) { userInput: UserInput ->
                post(OutgoingStatement(userInput.text))
            } else null*/

            voiceEnabled = enable && isEnabled(ChatFeatures.SpeechRecognition)
            inputEnabled = enable

        })
    }

    override fun startChat(accountInfo: AccountInfo?) {

        enableChatInput(true, null)
        accountInfo?.run { handlerConfiguration = getInfo().providerConfig }
        handleEvent(State, StateEvent(StateEvent.Started, getScope()))
        chatStarted = true
    }

    override fun endChat(forceClose: Boolean) {
        handleEvent(State, StateEvent(StateEvent.Ended, getScope()))
        enableChatInput(false, null)
        chatStarted = false
    }

    override fun post(message: ChatStatement) {

        injectElement(message)
        updateStatus(message, StatusOk)
        simulateAgentResponse(message.text)
    }

    override fun handleState(event: StateEvent?) {

        event?.run {

            when (state) {

                StateEvent.Started -> {
                    Log.e("MainFragment", "started handover")
                    injectSystemMessage("Started Chat with Handover provider, the handover data is: $handlerConfiguration")
                    injectElement(IncomingStatement("Hi from handover", getScope()))
                    passStateEvent(event)
                }

                StateEvent.Ended -> {
                    Log.e("MainFragment", "handover ended")
                    injectElement(IncomingStatement("bye from handover", getScope()))
                    injectSystemMessage("Ended Chat with the Handover provider")
                    passStateEvent(event)
                }

                StateEvent.Resumed -> onResume()

                else -> super.handleState(event)
            }
        }

    }

    override fun onResume() {

        super.onResume()
        enableChatInput(true, null)
    }

    /***
     * A function used to simulate agent typing indication
     * @param outgoingMessage
     */
    private fun simulateAgentResponse(outgoingMessage: String) {

        presentTypingIndication(true)
        handler.postDelayed({

            var agentAnswer = "handover response number " + responseNumber++

            if (outgoingMessage.toLowerCase(Locale.getDefault()) == "url test") {
                agentAnswer = "<a href=\"https://www.google.com\">Google link</a>"
            }

            presentTypingIndication(false)

            // Event to be sent after the agent response:
            handleEvent(Message, MessageEvent(IncomingStatement(agentAnswer, getScope())))

        }, 2000)
    }

    private fun presentTypingIndication(isTyping: Boolean) { /* -> In order to use the apps custom typing indication use:
            listener.handleEvent(Operator, new OperatorEvent(OperatorEvent.OperatorTyping, getScope(), isTyping));
         */
        Log.d("handover", "event: operatorTyping: $isTyping")

        chatDelegate?.run {
            if (isTyping) {
                updateCmp(ComponentType.LiveTypingCmp, null)
            } else {
                removeCmp(ComponentType.LiveTypingCmp, true)
            }
        }

    }

    companion object {
        private var responseNumber = 1
    }
}