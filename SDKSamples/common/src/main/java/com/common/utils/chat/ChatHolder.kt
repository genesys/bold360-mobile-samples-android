package com.common.utils.chat

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.common.chatComponents.history.HistoryRepository
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.convesationui.structure.controller.ChatLoadResponse
import com.nanorep.convesationui.structure.controller.ChatLoadedListener
import com.nanorep.sdkcore.utils.SystemUtil
import com.nanorep.sdkcore.utils.runMain
import com.nanorep.sdkcore.utils.toast
import java.lang.ref.WeakReference

class ChatHolder(wContext: WeakReference<Context>?, override var onChatLoaded: ((Fragment) -> Unit)?) : ChatProvider {

    override lateinit var loginData: LoginData

    private var context: Context? = wContext?.get()

    private var controller: ChatController? = null

    private var historyProvider: HistoryRepository? = null

    override fun updateHistoryRepo(historyRepository: HistoryRepository?, targetId: String?) {
        historyRepository?.let { historyProvider = historyRepository }
        targetId?.let { historyProvider?.targetId = targetId }
    }

    override fun hasChatController(): Boolean = controller?.wasDestructed == false

    @Throws(NullPointerException::class)
    override fun getChatController(): ChatController = controller!!

    private var chatLoadedListener: ChatLoadedListener = object : ChatLoadedListener {

        override fun onComplete(result: ChatLoadResponse) {
            result.error?.takeIf { context != null }?.run {
                toast(context!!, "Failed to load chat\nerror:${result.error ?: "failed to get chat fragment"}", Toast.LENGTH_SHORT)
            } ?: runMain {
                result.fragment?.let {
                    onChatLoaded?.invoke(it)
                }
            }
        }
    }

    override fun getSecuredInfo(): String {
        return "some PGP encrypted key string [${SystemUtil.generateTimestamp()}]"
    }

    override fun restore() {

        controller?.takeIf { !it.wasDestructed }?.run {

            val continueLast = loginData.account == null

            when {
                continueLast && hasOpenChats() && isActive -> restoreChat()

                loginData.restoreState.restorable -> restoreChat(
                    account = loginData.prepareAccount(
                        getSecuredInfo()
                    )
                )

                else -> {
                    context?.let { toast(it, "The Account is not restorable, a new chat had been created", Toast.LENGTH_SHORT)}
                    startChat(accountInfo = loginData.prepareAccount(getSecuredInfo()))
                }
            }

        } ?: kotlin.run {
            Log.e(
                "ChatHolder",
                "Failed to restore chat, hasChatController() must be checked first"
            )
        }
    }

    override fun create(chatBuilder: ChatController.Builder?): ChatController? {

        val builder = (chatBuilder ?: context?.let { ChatController.Builder(it) })?.apply {
            historyProvider?.let { chatElementListener(it) }
        }

        loginData.prepareAccount(getSecuredInfo())?.let { account ->

            builder?.build(account, chatLoadedListener)?.also {
                controller = it
            }
        }

        return controller
    }

    override fun destruct() {
        controller?.let {
            it.terminateChat()
            it.destruct()
        }
        controller = null
        onChatLoaded = null
        context = null
    }

    override fun clearHistory() {
        historyProvider?.clear()
    }
}