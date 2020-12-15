package com.sdk.samples.common

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
import com.nanorep.convesationui.structure.providers.ChatUIProvider
import com.nanorep.nanoengine.Account
import com.nanorep.sdkcore.utils.SystemUtil
import com.nanorep.sdkcore.utils.runMain
import com.nanorep.sdkcore.utils.toast
import com.nanorep.sdkcore.utils.weakRef
import java.lang.ref.WeakReference
import java.lang.reflect.InvocationTargetException

interface ChatProvider {

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
     * @param chatBuilder optional injection of a custom ChatController.Builder
     * When ready the chat fragment would be passed by 'onChatLoaded' invocation
     */
    fun create(chatBuilder: ChatController.Builder? = null)

    /**
     * Clears the chat and the ChatController
     */
    fun destruct()

    /**
     * Nullable context dependent
     */
    fun getUIProvider(): ChatUIProvider?

    fun getChatController() : ChatController
    fun hasChatController(): Boolean
}

interface AccountProvider {
    var account: Account
    var extraData: Map<String, Any?>?
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
        override lateinit var account: Account

        override var extraData: Map<String, Any?>? = null

        override var restoreState = RestoreState()
        set(value) {
            field = value.also {
                // If there is no chat restore request, the chat would be destructed
                if (!it.restoreRequest) {
                    chatHolder.destruct()
                }
            }
        }
    }

    private inner class ChatHolder(wContext: WeakReference<Context>) : ChatProvider {

        private val context = wContext.get()

        private var controller: ChatController? = null

        override var onChatLoaded: ((Fragment) -> Unit)? = null

        override fun getChatController(): ChatController {
            if (controller == null) {
                create()
            }
            return controller!!
        }

        override fun hasChatController(): Boolean = controller?.wasDestructed == false

        override fun destruct() {
            controller?.let {
                it.terminateChat()
                it.destruct()
            }
            controller = null
        }

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
            controller?.run {
                if(hasOpenChats() && isActive) {
                    restoreChat()
                } else {
                    restoreChat(account = prepareAccount())
                }
                return
            } ?: create()
        }

        override fun getUIProvider() : ChatUIProvider? {
            return context?.let { ChatUIProvider(it) }
        }

        override fun create(chatBuilder: ChatController.Builder?) {
            val builder = chatBuilder ?: context?.let { ChatController.Builder(context) }
            builder?.build(prepareAccount(), chatLoadedListener)?.let {
                controller = it
            } ?: kotlin.run { Log.e("ChatHolder", "Failed to create chat") }
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