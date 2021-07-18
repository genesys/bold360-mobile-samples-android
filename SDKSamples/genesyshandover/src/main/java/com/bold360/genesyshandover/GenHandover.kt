package com.bold360.genesyshandover

import android.content.Context
import android.util.Log
import com.genesys.cloud.messenger.io.Configuration
import com.genesys.cloud.messenger.io.MessagingClient
import com.genesys.cloud.messenger.io.shyrka.receive.ErrorCodes
import com.genesys.cloud.messenger.io.shyrka.receive.ErrorCodes.ATTACHMENT_NOT_SUCCESSFULLY_UPLOADED
import com.genesys.cloud.messenger.io.shyrka.receive.MessageType
import com.genesys.cloud.messenger.io.shyrka.receive.SessionResponse
import com.genesys.cloud.messenger.io.shyrka.receive.StructuredMessage
import com.genesys.cloud.messenger.io.shyrka.receive.WebMessagingMessage
import com.integration.core.State
import com.integration.core.StateEvent
import com.integration.core.StateEvent.Companion.Created
import com.integration.core.StateEvent.Companion.Ended
import com.integration.core.StateEvent.Companion.Preparing
import com.integration.core.StateEvent.Companion.Started
import com.nanorep.convesationui.structure.AccountListenerEvent
import com.nanorep.convesationui.structure.HandoverHandler
import com.nanorep.convesationui.views.autocomplete.ChatInputData
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.AccountInfo
import com.nanorep.nanoengine.model.configuration.VoiceSupport
import com.nanorep.nanoengine.model.conversation.SessionInfoConfigKeys
import com.nanorep.nanoengine.model.conversation.statement.IncomingStatement
import com.nanorep.nanoengine.model.conversation.statement.OutgoingStatement
import com.nanorep.sdkcore.model.ChatStatement
import com.nanorep.sdkcore.model.StatementScope
import com.nanorep.sdkcore.model.StatementStatus
import com.nanorep.sdkcore.model.StatusError
import com.nanorep.sdkcore.model.StatusNone
import com.nanorep.sdkcore.model.StatusOk
import com.nanorep.sdkcore.model.StatusPending
import com.nanorep.sdkcore.model.StatusSent
import com.nanorep.sdkcore.utils.Event
import com.nanorep.sdkcore.utils.EventListener
import com.nanorep.sdkcore.utils.NRError
import com.nanorep.sdkcore.utils.SystemUtil
import com.nanorep.sdkcore.utils.lazyM
import com.nanorep.sdkcore.utils.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import java.util.concurrent.ConcurrentLinkedQueue


/*
* 1. sessionId - when will it get a value if ever?
*
* */

class GenAccount(token: String = UUID.randomUUID().toString()) : Account(token) {

    var configuration: Configuration by lazyM {
        DefaultConfig
    }

    var sessionId: String?
        get() = info.getConfiguration("sessionId")
        set(value) {
            info.addConfigurations("sessionId" to value)
        }

    var email: String?
        get() = info.getConfiguration("email")
        set(value) {
            info.addConfigurations("email" to value)
        }

    var firstName: String?
        get() = info.getConfiguration("firstName")
        set(value) {
            info.addConfigurations("firstName" to value)
        }

    var lastName: String?
        get() = info.getConfiguration("lastName")
        set(value) {
            info.addConfigurations("lastName" to value)
        }

    var phoneNumber: String?
        get() = info.getConfiguration("phoneNumber")
        set(value) {
            info.addConfigurations("phoneNumber" to value)
        }


    companion object {

        val DefaultConfig = Configuration(
            deploymentId = "f8aad9d7-f8e7-48e9-ab02-eef92bc4fd2f",
            messagingBaseAddress = "wss://webmessaging.inindca.com",
            apiBaseAddress = "https://api.inindca.com"
        )
    }
}

// Async internal job queue can be used for this instead
class MessagesQueue : ConcurrentLinkedQueue<GenHandover.OutgoingMessage>() {

    var onMessageReady: ((GenHandover.OutgoingMessage) -> Unit)? = null
    var onMessageExpired: ((GenHandover.OutgoingMessage) -> Unit)? = null

    fun update(id: String, status: Int) {
        find { it.id == id }?.let {
            it.status = status

        }
    }

    private fun nextIdle(): GenHandover.OutgoingMessage? {
//        return reversed().find { it.status == StatusNone }
        removeAll(filter { message ->
            message.expired().let {
                if (it) onMessageExpired?.invoke(message)
                it
            }
        })

        return peek()?.takeIf { it.status == StatusNone }
    }


    fun remove(id: String) {
        find { it.id == id }?.let {
            remove(it)
        }
    }

    fun clearDone() {
        removeIf { it.status == StatusError || it.status == StatusOk }
    }

    fun addMessage(message: GenHandover.OutgoingMessage) {
        Log.d("GenHandover", "queue add:")
// consider set message self-timeout
        add(message)
        notifyNext()
    }

    private fun notifyNext() {
        Log.d("GenHandover", "notifyNext:")
        nextIdle()?.let {
            Log.d("GenHandover", "notifyNext: got ready message")

            onMessageReady?.invoke(it)
        }
    }

    fun poll(status: Int): GenHandover.OutgoingMessage? {
        Log.d("GenHandover", "queue poll on status: $status")

        return super.peek()?.takeIf { it.status == status }?.let {
            poll().also {
                notifyNext()
            }
        }
    }
    // only peek idles can be sent
}

class GenHandover(context: Context, val genAccount: GenAccount = GenAccount()) :
    HandoverHandler(context) {

    class OutgoingMessage(var message: ChatStatement) {
        fun expired(): Boolean {
            return status == StatusPending && SystemUtil.generateTimestamp() - message.timestamp > ExpirationTime
        }

        val id: String
            get() = message.sId

        @StatementStatus
        var status: Int = StatusNone


        companion object {
            const val ExpirationTime = 5000
            /*const val IdleStatus = 0
            const val PendingStatus = 1
            const val DoneStatus = 2*/
        }
    }

    private val messagesQueue = MessagesQueue().apply {

        onMessageReady = { outgoingMessage ->
            Log.d(
                "GenHandover",
                "onMessageReady: got outgoing message: ${outgoingMessage.message.text.log(50)}"
            )

            try {
                activeSession?.let {
                    Log.d("GenHandover", "onMessageReady: session active, sending outgoing message")

                    it.sendMessage(outgoingMessage.also {
                        it.status = StatusPending
                        updateStatus(it.message, StatusSent)
                    }.message.text)
                }
            } catch (e: Throwable) {
                Log.e(TAG, "Post message failed: code: ${outgoingMessage.id}")
                updateStatus(outgoingMessage.message, StatusError)

                poll()//remove(outgoingMessage.id) TODO: check this
            }
        }

        onMessageExpired = { outgoingMessage ->
            Log.d(
                "GenHandover",
                "onMessageExpired: outgoing message expired: ${outgoingMessage.message.text.log(50)}"
            )

            updateStatus(outgoingMessage.message, StatusError)

        }
    }


    private var client = MessagingClient(
        genAccount.configuration
        /*configuration = Configuration(
            deploymentId = "f8aad9d7-f8e7-48e9-ab02-eef92bc4fd2f",
            messagingBaseAddress = "wss://webmessaging.inindca.com",
            apiBaseAddress = "https://api.inindca.com"
        )*/
    )

    override val isActive: Boolean
        get() = chatStarted //&& genAccount.sessionId != null sessionId is null on new session


    private val activeSession: MessagingClient?
        get() =
            client.takeIf { isActive } //: in case we'll need more assurance tests of availability


    override fun getScope(): StatementScope {
        return StatementScope.HandoverScope
    }


    override fun endChat(forceClose: Boolean) {
        if(isActive){
            try {
                client.disconnect()
            } catch (t: Throwable) {
                Log.e(TAG, "Failed to disconnect Genesys chat", t)
            } finally {
                injectSystemMessage("Chat ended")
            }
        }

        handleEvent(State, StateEvent(Ended, getScope()))

        enableChatInput(false, null);
        chatStarted = false
    }


    override fun enableChatInput(enable: Boolean, cmpData: ChatInputData?) {
        if (enable) {
            cmpData?.apply {
                voiceSettings = configureVoiceSettings(VoiceSupport.SpeechRecognition)
                uploadEnabled = false
                autocompleteEnabled = false
                typingMonitoringEnabled = false
                onSend = {
                    post(
                        OutgoingStatement(
                            text = it.toString(),
                            scope = getScope(),
                            source = inputSource.name
                        )
                    )
                }
                inputEnabled = enable
            }
        }

        super.enableChatInput(enable, cmpData)
    }


    override fun startChat(accountInfo: AccountInfo?) {
        super.startChat(accountInfo)

        (accountInfo as? GenAccount)?.configuration?.let {
            client = MessagingClient(it)
        }

        client.stateListener = {
            onStateChanged(it)
        }

        client.messageListener = {
            onMessageReceived(it)
        }

        try {
            client.connect()

            handleEvent(State, StateEvent(Preparing, getScope()))

        } catch (e: Throwable) {
            Log.e(TAG, "Failed to connect: state:${client.currentState} error: $e ")
        }
    }

    override fun post(message: ChatStatement) {
        injectElement(message.apply {
            scope = getScope()
        })?.also { // in case message was not intercepted by hosting App:
            Log.d(TAG, "post: message added to queue")
            messagesQueue.addMessage(OutgoingMessage(message)) // tODO: condition this that there are no current pending messages,
        }
    }

    private fun onMessageReceived(message: WebMessagingMessage<*>) {
        Log.d(TAG, "onMessageReceived: code: ${message.code}, type: ${message.type}")
        when (message.code) {
            200 -> handleMessage(message)

            else -> getError(message.code)?.let {
                Log.w(TAG, "Got failure response")
            }//injectElement(SystemStatement("got: [${message.code}]"))
        }

    }

    private fun handleMessage(message: WebMessagingMessage<*>) {
        Log.d(TAG, "handleMessage: type: ${message.type}")

        when (message.type) {
            MessageType.Message.value -> {
                Log.d(TAG, "handleMessage: injecting message to chat")
                (message.body as? StructuredMessage)?.let { msgBody ->
                    when (msgBody.direction) {
                        "Inbound" -> { // sent by user
                            messagesQueue.poll(StatusPending)?.let { outMessage ->
                                val temp = outMessage.message.sId
                                 msgBody.id?.let { outMessage.message.sId = it }

                                // ?? how can we verify that the message we've got is the one we expect?
                                //  messages can be sent from the server on malfunction, and we don't want to consider
                                //  them unless it's our message
                                Log.d(TAG, "handleMessage: polling user message from pending queue")
                                updateElement(temp,  outMessage.message)
                                updateStatus(outMessage.message, StatusOk)
                            }
                        }

                        else -> {
                            injectElement(IncomingStatement(msgBody.text?:"", scope = getScope()))
                        }
                    }
                }

            }

            MessageType.Response.value -> {
                handleResponse(message)
            }
        }
    }

    private fun handleResponse(message: WebMessagingMessage<*>) {
        Log.d(
            TAG,
            "handleResponse: body: ${message.body?.javaClass?.simpleName ?: " message has no body"}"
        )

        val body = message.body

        when (body) {
            is StructuredMessage -> { // response for posted message
                // todo: go to pending message in the queue and send StatusOk update

            }

            is SessionResponse -> {
                handleSessionResponse(body)
            }

            is String -> {
                Log.d(TAG, "handleResponse: String response: $body")

            }


        }
    }

    private fun handleSessionResponse(body: SessionResponse) {
        if (body.connected) {
            // TODO: verify when this response is expected:

            // !- lets say this is a configure session response:
            passStateEvent(StateEvent(Started, getScope()))
            chatStarted = true

            genAccount.sessionId = body.sessionId

            body.sessionId?.let {
                passEvent(AccountListenerEvent { accountListener ->
                    // passing the sessionId update to the application in case it needs to be stored.
                    accountListener?.onConfigUpdate(
                        genAccount,
                        SessionInfoConfigKeys.SenderId,
                        it
                    )
                })
            }

            enableChatInput(true, ChatInputData())
            // -> the chat is active now.
            injectSystemMessage("Start Chat")

        } else {
            genAccount.sessionId = null
        }
    }

    private fun getError(code: Int): NRError? {
        return when (code) {
            in ErrorCodes.FEATURE_UNAVAILABLE.value..ATTACHMENT_NOT_SUCCESSFULLY_UPLOADED.value -> {
                NRError(NRError.GeneralError)

            }

            ErrorCodes.MISSING_PARAMETERS.value -> {
                NRError(NRError.MissingRequestParamsError)
            }

            else -> {
                NRError(NRError.GeneralError)

            }
        }
    }

    fun onStateChanged(state: MessagingClient.State) {
        Log.d(TAG, "onStateChanged: state: ${state}")

        when (state) {
            MessagingClient.State.CONNECTED -> {
                passStateEvent(StateEvent(Created, getScope()))
                startConfiguredSession()
            }

            MessagingClient.State.CLOSED -> {
                endChat()
            }
            else -> Log.d(TAG, "onStateChanged: unhandled state $state")
        }
    }

    private fun startConfiguredSession() {
        try {
            client.configureSession(
                genAccount.apiKey, genAccount.email, genAccount.phoneNumber,
                genAccount.firstName, genAccount.lastName
            )
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to configure session: state:${client.currentState}, error: $e")
        }
    }


    override fun handleEvent(name: String, event: Event) {
        super.handleEvent(name, event)


    }


    override fun setListener(listener: EventListener?) {
        super<HandoverHandler>.setListener(listener)
    }

    /*override fun onResume() {
        super.onResume()

        enableChatInput(true, ChatInputData())
    }*/

    override fun destruct() {
        super.destruct()

        client.stateListener = null
        client.messageListener = null

    }

    companion object {
        const val TAG = "GenHandover"
    }
}