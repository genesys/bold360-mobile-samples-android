package com.sdk.samples

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import com.integration.core.securedInfo
import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.convesationui.structure.controller.ChatLoadResponse
import com.nanorep.convesationui.structure.controller.ChatLoadedListener
import com.nanorep.nanoengine.Account
import com.nanorep.sdkcore.utils.SystemUtil
import com.nanorep.sdkcore.utils.runMain
import com.nanorep.sdkcore.utils.toast
import com.nanorep.sdkcore.utils.weakRef
import com.sdk.samples.common.accountUtils.ChatType
import com.sdk.samples.common.history.HistoryRepository
import com.sdk.samples.common.loginForms.RestoreState
import com.sdk.samples.common.loginForms.SharedDataHandler
import java.lang.ref.WeakReference
import java.lang.reflect.InvocationTargetException

interface ChatProvider {

    /**
     * To be called only after chat creation.
     * @return ChatController
     * @throws NullPointerException
     */
    @Throws(NullPointerException::class)
    fun getChatController() : ChatController

    fun updateHistoryRepo(historyRepository: HistoryRepository? = null, targetId: String? = null)

    /**
     * Being invoked when the chat fragment had been fetched and ready to be presented
     */
    var onChatLoaded: ((Fragment) -> Unit)?

    /**
     * Restores the chat (if hasChatController) for the current account
     */
    fun restore()

    /**
     * Creates the chat controller and starts the chat
     * @param chatBuilder optional injection of a custom ChatController.Builder
     * When ready the chat fragment would be passed by 'onChatLoaded' invocation
     */
    fun create(chatBuilder: ChatController.Builder? = null): ChatController?

    /**
     * @return true if the chat controller exists and had not been destructed
     */
    fun hasChatController(): Boolean

    /**
     * Clears the chat and the ChatController
     */
    fun destruct()

    /**
     * Clears the history and frees its resources
     */
    fun clearHistory()

    /**
     * Clears all the chat resources (includes the history)
     */
    fun clear()
}

interface AccountProvider {
    var account: Account?

    /**
     * Extra Account parameters to be submitted for the sample
     */
    var extraData: Map<String, Any?>?

    /**
     * The RestoreState of the account
     */
    var restoreState: RestoreState
}

class SamplesViewModel(application: Application) : AndroidViewModel(application) {

    val accountProvider = AccountHolder()

    val chatProvider: ChatProvider
    get() = chatHolder

    private val chatHolder = ChatHolder(application.applicationContext.weakRef())

    companion object {

        private var myViewModel: SamplesViewModel? = null

        fun getInstance(application: Application) : SamplesViewModel {
            if (myViewModel == null) {
                myViewModel = SamplesViewModel(application)
            }
            return myViewModel!!
        }
    }

    inner class AccountHolder: AccountProvider {
        override var account: Account? = null

        override var extraData: Map<String, Any?>? = null

        override var restoreState = RestoreState()
        set(value) {
            field = value.also {

                // If there is no chat restore request, the chat would be destructed
                if (!it.restoreRequest) chatHolder.destruct()
            }
        }
    }

    private inner class ChatHolder(wContext: WeakReference<Context>) : ChatProvider {

        private var context: Context? = wContext.get()

        private var controller: ChatController? = null

        private var historyProvider: HistoryRepository? = null

        override var onChatLoaded: ((Fragment) -> Unit)? = null

        override fun updateHistoryRepo(historyRepository: HistoryRepository?, targetId: String?) {
            historyRepository?.let { historyProvider = historyRepository }
            historyProvider?.targetId = targetId
        }

        override fun hasChatController(): Boolean = controller?.wasDestructed == false

        @Throws(NullPointerException::class)
        override fun getChatController(): ChatController = controller!!

        private var chatLoadedListener: ChatLoadedListener = object : ChatLoadedListener {

            override fun onComplete(result: ChatLoadResponse) {
                result.error?.takeIf { context != null }?.run {
                    toast( context!!, "Failed to load chat\nerror:${result.error ?: "failed to get chat fragment"}")
                } ?: runMain {
                    result.fragment?.let {
                        onChatLoaded?.invoke(it)
                    }
                }
            }
        }

        private fun prepareAccount(): Account? {
            return accountProvider.account?.apply {
                (this as? BoldAccount)?.let {
                    it.info.securedInfo = getSecuredInfo()
                }
            }
        }

        private fun getSecuredInfo(): String {
            return "some PGP encrypted key string [${SystemUtil.generateTimestamp()}]"
        }

        override fun restore() {

            controller?.takeIf { !it.wasDestructed }?.run {
                val chatType = accountProvider.extraData?.get(SharedDataHandler.ChatType_key) as String
                val continueLast = chatType == ChatType.None || accountProvider.account == null
                when {
                    continueLast && hasOpenChats() && isActive -> restoreChat()

                    accountProvider.restoreState.restorable -> restoreChat( account = prepareAccount() )

                    else -> context?.run{ toast(this, "The Account is not restorable") }
                }

            } ?: kotlin.run { Log.e("ChatHolder", "Failed to restore chat, hasChatController() must be checked first") }
        }

        override fun create(chatBuilder: ChatController.Builder?): ChatController? {

            val builder = (chatBuilder ?: context?.let { ChatController.Builder(it) })?.apply {
                historyProvider?.let { chatElementListener(it) }
            }

            prepareAccount()?.let { account ->
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
            historyProvider = null
        }

        override fun clear() {
            clearHistory()
            destruct()
        }
    }

    fun clear() {
        chatProvider.clear()
    }
}

class SingletonSamplesViewModelFactory(vmInstance: SamplesViewModel) : NewInstanceFactory() {

    private val samplesViewModel: SamplesViewModel = vmInstance
    private val viewModelFactory: MutableMap<Class<out ViewModel>, ViewModel> = mutableMapOf()

    fun clear() {
        viewModelFactory.clear()
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        viewModelFactory[modelClass] = samplesViewModel

        if (SamplesViewModel::class.java.isAssignableFrom(modelClass)) {

            val shareVM: SamplesViewModel

            if (viewModelFactory.containsKey(modelClass)) {
                shareVM = viewModelFactory[modelClass] as SamplesViewModel
            } else {
                shareVM = try {
                    modelClass.getConstructor(Runnable::class.java).newInstance(
                        Runnable { viewModelFactory.remove(modelClass) }) as SamplesViewModel

                } catch (e: Exception) {
                    throw RuntimeException("Cannot create an instance of $modelClass", e)
                } catch (e: IllegalAccessException) {
                    throw RuntimeException("Cannot create an instance of $modelClass", e)
                } catch (e: InstantiationException) {
                    throw RuntimeException("Cannot create an instance of $modelClass", e)
                } catch (e: InvocationTargetException) {
                    throw RuntimeException("Cannot create an instance of $modelClass", e)
                }
                viewModelFactory[modelClass] = shareVM
            }
            return shareVM as T
        }

        return super.create(modelClass)
    }

}