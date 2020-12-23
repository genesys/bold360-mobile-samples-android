package com.sdk.utils.chat

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.convesationui.structure.controller.ChatLoadResponse
import com.nanorep.convesationui.structure.controller.ChatLoadedListener
import com.nanorep.sdkcore.utils.SystemUtil
import com.nanorep.sdkcore.utils.runMain
import com.nanorep.sdkcore.utils.toast
import com.sdk.utils.accountUtils.ChatType
import com.sdk.utils.history.HistoryRepository
import com.sdk.utils.loginForms.SharedDataHandler
import java.lang.ref.WeakReference

class ChatHolder(wContext: WeakReference<Context>?, override val accountHolder: AccountHolder)
    : ChatProvider, AccountHolder by accountHolder {

    private var context: Context? = wContext?.get()

    private var controller: ChatController? = null

    private var historyProvider: HistoryRepository? = null

    override var onChatLoaded: ((Fragment) -> Unit)? = null

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
                toast( context!!, "Failed to load chat\nerror:${result.error ?: "failed to get chat fragment"}", Toast.LENGTH_SHORT)
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
            val chatType = accountHolder.extraData?.get(SharedDataHandler.ChatType_key) as String
            val continueLast = chatType == ChatType.None || accountHolder.account == null
            when {
                continueLast && hasOpenChats() && isActive -> restoreChat()

                accountHolder.restoreState.restorable -> restoreChat( account = accountHolder.prepareAccount(getSecuredInfo()) )

                else -> context?.run{ toast(this, "The Account is not restorable") }
            }

        } ?: kotlin.run { Log.e("ChatHolder", "Failed to restore chat, hasChatController() must be checked first") }
    }

    override fun create(chatBuilder: ChatController.Builder?): ChatController? {

        val builder = (chatBuilder ?: context?.let { ChatController.Builder(it) })?.apply {
            historyProvider?.let { chatElementListener(it) }
        }

        accountHolder.prepareAccount(getSecuredInfo())?.let { account ->
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