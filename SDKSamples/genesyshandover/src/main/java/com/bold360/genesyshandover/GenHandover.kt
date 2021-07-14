package com.bold360.genesyshandover

import android.content.Context
import android.util.Log
import com.genesys.cloud.messenger.io.Configuration
import com.genesys.cloud.messenger.io.MessagingClient
import com.genesys.cloud.messenger.io.shyrka.receive.ErrorCodes
import com.genesys.cloud.messenger.io.shyrka.receive.ErrorCodes.ATTACHMENT_NOT_SUCCESSFULLY_UPLOADED
import com.genesys.cloud.messenger.io.shyrka.receive.MessageType
import com.genesys.cloud.messenger.io.shyrka.receive.SessionResponse
import com.genesys.cloud.messenger.io.shyrka.receive.WebMessagingMessage
import com.nanorep.convesationui.structure.AccountListenerEvent
import com.nanorep.convesationui.structure.HandoverHandler
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.AccountInfo
import com.nanorep.nanoengine.model.conversation.SessionInfoConfigKeys
import com.nanorep.nanoengine.model.conversation.statement.IncomingStatement
import com.nanorep.sdkcore.model.ChatStatement
import com.nanorep.sdkcore.model.StatementScope
import com.nanorep.sdkcore.model.StatementStatus
import com.nanorep.sdkcore.model.StatusBroken
import com.nanorep.sdkcore.model.StatusNone
import com.nanorep.sdkcore.model.StatusPending
import com.nanorep.sdkcore.utils.Event
import com.nanorep.sdkcore.utils.EventListener
import com.nanorep.sdkcore.utils.NRError
import com.nanorep.sdkcore.utils.lazyM
import java.util.UUID
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.ConcurrentLinkedQueue


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

class GenHandover(context: Context, val genAccount: GenAccount = GenAccount()) :
    HandoverHandler(context) {

    class OutgoingMessage(var message: ChatStatement) {
        @StatementStatus var status:Int = StatusNone



       /* companion object{
            const val IdleStatus = 0
            const val PendingStatus = 1
            const val DoneStatus = 2
        }*/
    }

    val messagesDequeue: ConcurrentLinkedDeque<OutgoingMessage> = ConcurrentLinkedDeque()

    private val client = MessagingClient(
        genAccount.configuration
        /*configuration = Configuration(
            deploymentId = "f8aad9d7-f8e7-48e9-ab02-eef92bc4fd2f",
            messagingBaseAddress = "wss://webmessaging.inindca.com",
            apiBaseAddress = "https://api.inindca.com"
        )*/
    )

    override val isActive: Boolean
        get() = client.currentState == MessagingClient.State.CONNECTED && genAccount.sessionId != null


    private val activeSession:MessagingClient?
    get() =
        client.takeIf { isActive } //: in case we'll need more assurance tests of availability


    override fun getScope(): StatementScope {
        return StatementScope.HandoverScope
    }



    override fun startChat(accountInfo: AccountInfo?) {
        super.startChat(accountInfo)

        //

        client.stateListener = {
            onStateChanged(it)
        }

        client.messageListener = {
            onMessageReceived(it)
        }

        try {
            client.connect()
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to connect: state:${client.currentState} error: $e ")
        }
    }

    private fun onMessageReceived(message: WebMessagingMessage<*>) {
        Log.d(TAG, "onMessageReceived: code: ${message.code}, type: ${message.type}")
        when (message.code) {
            200 -> handelMessage(message)

            else -> getError(message.code)?.let {
                Log.w(TAG, "Got failure response")
            }//injectElement(SystemStatement("got: [${message.code}]"))
        }

    }

    private fun handelMessage(message: WebMessagingMessage<*>) {
        when (message.type) {
            MessageType.Message.value -> {
                injectElement(
                    IncomingStatement(
                        message.body?.toString() ?: "",
                        scope = getScope()
                    )
                )
            }

            MessageType.Response.value -> {
                (message.body as? SessionResponse)?.let {
                    if (it.connected) {
                        genAccount.sessionId = it.sessionId

                        passEvent(AccountListenerEvent { accountListener ->
                            // passing the sessionId update to the application in case it needs to be stored.
                            accountListener?.onConfigUpdate(
                                genAccount,
                                SessionInfoConfigKeys.SenderId,
                                it.sessionId
                            )
                        })

                        // -> the chat is active now.

                    } else {
                        genAccount.sessionId = null
                    }
                }
            }
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
                startConfiguredSession()
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

    override fun endChat(forceClose: Boolean) {
        super.endChat(forceClose)
    }

    override fun handleEvent(name: String, event: Event) {
        super.handleEvent(name, event)
    }

    override fun post(message: ChatStatement) {
        try {
            val outgoingMessage = OutgoingMessage(message)
            messagesQueue.add(outgoingMessage)

            activeSession?.let {
                outgoingMessage.status = StatusPending
                it.sendMessage(message.text)
            }
        } catch (e: Throwable){
            Log.e(TAG, "Post message failed: code: ${message.sId}")
            messagesQueue.poll()
        }
    }

    override fun setListener(listener: EventListener?) {
        super<HandoverHandler>.setListener(listener)
    }

    override fun destruct() {
        super.destruct()
    }

    companion object {
        const val TAG = "GenHandover"
    }
}