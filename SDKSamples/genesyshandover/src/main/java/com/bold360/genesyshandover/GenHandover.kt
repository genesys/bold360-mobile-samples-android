package com.bold360.genesyshandover

import android.content.Context
import android.util.Log
import com.genesys.cloud.messenger.transport.Configuration
import com.genesys.cloud.messenger.transport.MessagingClient
import com.genesys.cloud.messenger.transport.shyrka.receive.ErrorCodes
import com.genesys.cloud.messenger.transport.shyrka.receive.MessageEntityList
import com.genesys.cloud.messenger.transport.shyrka.receive.MessageType
import com.genesys.cloud.messenger.transport.shyrka.receive.SessionResponse
import com.genesys.cloud.messenger.transport.shyrka.receive.StructuredMessage
import com.genesys.cloud.messenger.transport.shyrka.receive.WebMessagingMessage
import com.genesys.cloud.messenger.transport.util.MobileMessenger
import com.integration.core.ErrorEvent
import com.integration.core.State
import com.integration.core.StateEvent
import com.integration.core.StateEvent.Companion.Created
import com.integration.core.StateEvent.Companion.Ended
import com.integration.core.StateEvent.Companion.Ending
import com.integration.core.StateEvent.Companion.Preparing
import com.integration.core.StateEvent.Companion.Started
import com.nanorep.convesationui.structure.AccountListenerEvent
import com.nanorep.convesationui.structure.HandoverAccount
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
import java.util.concurrent.ConcurrentLinkedQueue


/*
* 1. sessionId - when will it get a value if ever?
*
* */

class GenAccount : Account("") {

    var configuration: Configuration
        get() = info.getConfiguration("clientConfiguration") ?: DefaultConfig
        set(value) {
            info.addConfigurations("clientConfiguration" to value)
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
            apiBaseAddress = "https://api.inindca.com",
            tokenStoreKey = "com.genesys.messenger.poc", // will be used to save the random UUID on the device shared preferences
            logging = true
        )

        /**
         * Due to HandoverAccount not open to extension on this SDK version
         */
        fun toHandoverAccount(account: AccountInfo) : HandoverAccount {
            return (account as? HandoverAccount) ?: HandoverAccount("").apply {
                this.info = account.getInfo()
            }
        }

        /**
         * Due to HandoverAccount not open to extension on this SDK version
         */
        fun toGenAccount(account: AccountInfo) : GenAccount {
            return (account as? GenAccount) ?: GenAccount().apply {
                this.info = account.getInfo()
            }
        }
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

class GenHandover(context: Context) :
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

    private var genAccount: GenAccount = GenAccount()
        set(value) {
            field = value

            context?.let {
                client = try {
                    MobileMessenger.createMessagingClient(
                        context = it,
                        configuration = field.configuration

                    )
                } catch (ex: Exception) {
                    Log.e("GenHandover", "failed to create MessagingClient: ")
                    ex.printStackTrace()
                    passEvent(ErrorEvent(NRError(ex)))

                    dummyMessagingClient
                }
            }
        }

    private var client: MessagingClient by lazyM {
        dummyMessagingClient
    }


    override val isActive: Boolean
        get() = chatStarted && client.currentState == MessagingClient.State.CONNECTED//&& genAccount.sessionId != null sessionId is null on new session


    private val activeSession: MessagingClient?
        get() =
            client.takeIf { isActive } //: in case we'll need more assurance tests of availability


    override fun getScope(): StatementScope {
        return StatementScope.HandoverScope
    }

    override fun endChat(forceClose: Boolean) {
        if (isActive) {
            try {
                client.disconnect()
                passStateEvent(StateEvent(Ending, getScope()))

            } catch (t: Throwable) {
                Log.e(TAG, "Failed to disconnect Genesys chat", t)
            }
        } else if (client.currentState == MessagingClient.State.CLOSED) {
            passStateEvent(StateEvent(Ended, getScope()))
        }

        chatStarted = false
        enableChatInput(false, null);
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

        accountInfo?.let {
            genAccount = GenAccount.toGenAccount(it)
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
            injectSystemMessage("Failed to connect")
            endChat(true)
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

                passEvent(ErrorEvent(it))
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
                                updateElement(temp, outMessage.message)
                                updateStatus(outMessage.message, StatusOk)
                            }
                        }

                        else -> {
                            injectElement(IncomingStatement(msgBody.text ?: "", scope = getScope()))
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
            in ErrorCodes.FEATURE_UNAVAILABLE.value..ErrorCodes.ATTACHMENT_NOT_SUCCESSFULLY_UPLOADED.value -> {
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

            MessagingClient.State.CLOSING -> {
                if (chatStarted) {
                    endChat()
                }
            }

            MessagingClient.State.CLOSED -> {
                injectSystemMessage("Chat ended")
                handleEvent(State, StateEvent(Ended, getScope()))

            }
            else -> Log.d(TAG, "onStateChanged: unhandled state $state")
        }
    }

    private fun startConfiguredSession() {
        try {
            client.configureSession(
                genAccount.email, genAccount.phoneNumber,
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

        val dummyMessagingClient = object : MessagingClient {
            override val currentState: MessagingClient.State = MessagingClient.State.CLOSED
            override var messageListener: ((message: WebMessagingMessage<*>) -> Unit)? = null
            override var rawMessageListener: ((text: String) -> Unit)? = null
            override val socketError: Throwable? = null
            override var stateListener: ((MessagingClient.State) -> Unit)? = null
            override val token: String = ""

            override fun configureNewSession(
                email: String?,
                phoneNumber: String?,
                firstName: String?,
                lastName: String?
            ) {
                Log.e("DummyMessagingClient", "Action can't be done. restart sample")
            }

            override fun configureSession(
                email: String?,
                phoneNumber: String?,
                firstName: String?,
                lastName: String?
            ) {
                Log.e("DummyMessagingClient", "Action can't be done. restart sample")
            }

            override fun connect() {
                Log.e("DummyMessagingClient", "Action can't be done. restart sample")
            }

            override fun deleteAttachment(attachmentId: String) {
                Log.e("DummyMessagingClient", "Action can't be done. restart sample")
            }

            override fun disconnect() {
                Log.e("DummyMessagingClient", "Action can't be done. restart sample")
            }

            override fun generateDownloadUrl(attachmentId: String) {
                Log.e("DummyMessagingClient", "Action can't be done. restart sample")
            }

            override fun generateUploadUrl(
                fileName: String,
                fileType: String,
                attachmentId: String?
            ) {
                Log.e("DummyMessagingClient", "Action can't be done. restart sample")
            }

            override suspend fun getMessages(pageNumber: Int, pageSize: Int): MessageEntityList {
                Log.e("DummyMessagingClient", "Action can't be done. restart sample")
                return MessageEntityList(pageSize = 0, pageNumber = 0, total = 0, pageCount = 0)
            }

            override fun sendHealthCheck() {
                Log.e("DummyMessagingClient", "Action can't be done. restart sample")
            }

            override fun sendMessage(text: String) {
                Log.e("DummyMessagingClient", "Action can't be done. restart sample")
            }
        }
    }
}