package com.sdk.samples.common

import android.app.Application
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import com.integration.core.securedInfo
import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.convesationui.structure.controller.ChatLoadResponse
import com.nanorep.convesationui.structure.controller.ChatLoadedListener
import com.nanorep.convesationui.structure.providers.ChatUIProvider
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.model.configuration.ConversationSettings
import com.nanorep.sdkcore.utils.SystemUtil
import com.nanorep.sdkcore.utils.runMain
import com.nanorep.sdkcore.utils.toast
import java.lang.reflect.InvocationTargetException

interface ChatProvider {

    /**
     * @param chatController is the ChatController of the SDK
     */
    var chatController: ChatController?

    /**
     * Being invoked when the chat fragment had been fetched and ready to be presented
     */
    var onChatLoaded: ((Fragment) -> Unit)?

    /**
     * Restores the chat (if available) for the current account
     */
    fun restore()

    /**
     * Creates the chat controller and starts the chat
     * When ready the chat fragment would be passed by 'onChatLoaded' invocation
     */
    fun create()

    /**
     * Clears the chat and the ChatController
     */
    fun destruct()

    fun getBuilder() : ChatController.Builder
    fun hasChatController(): Boolean
    fun createSettings(): ConversationSettings
    fun getUIProvider(): ChatUIProvider
}

interface AccountProvider {
    var account: Account
    var extraData: Map<String, Any?>?
    var restoreState: RestoreState
}

class SamplesViewModel(application: Application) : AndroidViewModel(application) {

    var accountProvider = AccountHolder()

    fun getChat() = chatHolder as ChatProvider
    private var chatHolder = ChatHolder()

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
        override lateinit var account: Account

        override var extraData: Map<String, Any?>? = null

        override var restoreState = RestoreState()
        set(value) {
            field = value.also {
                // If there is no chat restore request, the chat would be destructed
                if (!it.restoreRequest) {
                    getChat().destruct()
                }
            }
        }
    }

    private inner class ChatHolder : ChatProvider {

        val context: Context = getApplication<Application>().applicationContext

        override var chatController: ChatController? = null

        override var onChatLoaded: ((Fragment) -> Unit)? = null

        override fun hasChatController(): Boolean = chatController?.wasDestructed == false

        override fun destruct() {
            chatController?.let {
                it.terminateChat()
                it.destruct()
            }
            chatController = null
        }

        private var chatLoadedListener: ChatLoadedListener = object : ChatLoadedListener {

            override fun onComplete(result: ChatLoadResponse) {
                result.error?.run {
                    toast(
                        context,
                        "Failed to load chat\nerror:${result.error ?: "failed to get chat fragment"}  "
                    )
                } ?: runMain {
                    result.fragment?.let {
                        onChatLoaded?.invoke(it)
                    }
                }
            }
        }

        private fun prepareAccount(): Account {
            return accountProvider.account.apply {
                (this as? BoldAccount)?.let {
                    it.info.securedInfo = getSecuredInfo()
                }
            }
        }

        private fun getSecuredInfo(): String {
            return "some PGP encrypted key string [${SystemUtil.generateTimestamp()}]"
        }

        override fun restore() {
            chatController?.run {
                if(hasOpenChats() && isActive) {
                    restoreChat()
                } else {
                    restoreChat(account = prepareAccount())
                }
                return
            } ?: create()
        }

        override fun getUIProvider() : ChatUIProvider {
            return ChatUIProvider(context)
        }

        override fun createSettings(): ConversationSettings {
            return  ConversationSettings()
        }

        override fun getBuilder(): ChatController.Builder {
            return ChatController.Builder(context)
                .conversationSettings(createSettings())
                .chatUIProvider(getUIProvider())
        }

        override fun create() {
            destruct()
            chatController = getBuilder().build(prepareAccount(), chatLoadedListener)
        }
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