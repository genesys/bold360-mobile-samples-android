package com.common.topicsbase

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.integration.core.StateEvent
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.convesationui.structure.controller.ChatEventListener
import com.nanorep.convesationui.structure.controller.ChatLoadResponse
import com.nanorep.convesationui.structure.controller.ChatLoadedListener
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.model.configuration.ConversationSettings
import com.nanorep.sdkcore.utils.NRError
import com.nanorep.sdkcore.utils.SystemUtil
import com.nanorep.sdkcore.utils.hideKeyboard
import com.nanorep.sdkcore.utils.runMain
import com.nanorep.sdkcore.utils.toast
import com.sdk.common.R
import com.sdk.common.databinding.ActivityBasicBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class BasicChat : SampleActivity(), ChatEventListener {

    protected var endMenu: MenuItem? = null
    protected var destructMenu: MenuItem? = null

    lateinit var binding: ActivityBasicBinding

    override val containerId: Int
        get() = R.id.basic_chat_view

    /**
     * Creates the chat chatController and starts the chat
     * @param chatBuilder (optional) injection of a custom ChatController.Builder
     * @return true if the chatController had been created properly, false otherwise
     * When ready the chat fragment would be passed by 'onChatLoaded' invocation
     */
    private fun create(chatBuilder: ChatController.Builder? = null) {

        val chatLoadedListener: ChatLoadedListener = object : ChatLoadedListener {

            override fun onComplete(result: ChatLoadResponse) {
                result.error?.run {

                    toast(baseContext, "Failed to load chat\nerror:${result.error ?: "failed to get chat fragment"}", Toast.LENGTH_SHORT)

                } ?: runMain {

                    result.fragment?.let { chatFragment ->
                        if (!isFinishing && !supportFragmentManager.isStateSaved) {

                            binding.basicLoading.visibility = View.GONE

                            hideKeyboard(window.decorView)

                            supportFragmentManager.beginTransaction()
                                .add(
                                    R.id.basic_chat_view,
                                    chatFragment,
                                    topicTitle
                                )
                                .addToBackStack(ChatTag)
                                .commit()
                        } else {
                            finish()
                        }
                    }

                }
            }
        }

        prepareAccount()?.let { account ->

            (chatBuilder ?: ChatController.Builder(baseContext))
                .build(account, chatLoadedListener).also {
                    chatController = it
                }
        }
    }

    /**
     * @return true if the chat chatController exists and had not been destructed
     */
    protected fun hasChatController(): Boolean =
        ::chatController.isInitialized && chatController.hasOpenChats() //-> 'hasOpenChats()' Would be replaced with 'wasDestructed()' on the next SDK version

    /**
     * Enables the sample to modify the account before creating the chat
     */
    protected open fun prepareAccount(): Account? = account

    /**
     * Being invoked when the chat fragment had been fetched and ready to be presented
     */
    protected lateinit var chatController: ChatController

    /**
     * Returns encrypted info to be added to the Live account (if there is any)
     */
    protected fun getSecuredInfo(): String {
        return "some PGP encrypted key string [${SystemUtil.generateTimestamp()}]"
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(
            this, R.layout.activity_basic)

        setSupportActionBar(findViewById(R.id.sample_toolbar))

        binding.topicTitle.text = topicTitle
    }

    override fun startSample(savedInstanceState: Bundle?) {
        createChat()
    }

    protected open fun createChat() {
        create(getChatBuilder())
    }

    protected open fun getChatBuilder(): ChatController.Builder? {
        return ChatController.Builder(baseContext)
            .conversationSettings(createChatSettings())
            .chatEventListener(this)
    }

    protected open fun createChatSettings(): ConversationSettings {
        return ConversationSettings().apply {
            /*!- uncomment to set custom datestamp format:
             .datestamp(true, SampleDatestampFactory())
         */
        }
    }

    override fun onChatStateChanged(stateEvent: StateEvent) {

        Log.d(TAG, "chat in state: ${stateEvent.state}")

        when (stateEvent.state) {
            StateEvent.Started -> enableMenu(endMenu, chatController.hasOpenChats())

            StateEvent.ChatWindowDetached -> onChatUIDetached()

            StateEvent.Unavailable -> lifecycleScope.launch {
                toast(baseContext, stateEvent.state, Toast.LENGTH_SHORT)
            }

            StateEvent.Ended -> {
                if (!chatController.hasOpenChats()) {
                    removeChatFragment()
                }
            }

            StateEvent.Idle -> enableMenu(endMenu, false)
        }
    }

    override fun onError(error: NRError) {
        super.onError(error)
        lifecycleScope.launch { toast(baseContext, error.toString(), Toast.LENGTH_SHORT) }
    }

    override fun onBackPressed() {
        enableMenu(endMenu, hasChatController() && chatController.hasOpenChats())
        super.onBackPressed()
    }

    protected fun removeChatFragment() {
        /* !- launch suspended on a different deamon to prevent activation while on a previous
              fragmentManagerTransaction (exp: onBackPressed on postchat form, triggers StateEvent.Ended
              event call, which calls to remove the chat fragment */
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val fragmentFound = supportFragmentManager.takeUnless { it.isDestroyed }?.popBackStackImmediate(
                    ChatTag,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
                if (fragmentFound == false) onChatUIDetached()
            } catch (ex: IllegalStateException) {
                ex.printStackTrace()
            }
        }
    }

    protected open fun onChatUIDetached() {
        finishIfLast()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_main, menu)

        this.endMenu = menu?.findItem(R.id.end_current_chat)
        this.destructMenu = menu?.findItem(R.id.destruct_chat)

        if (hasChatController()) {
            enableMenu(endMenu, chatController.hasOpenChats())
            enableMenu(destructMenu, hasChatController() && !chatController.wasDestructed)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.end_current_chat -> {
                chatController.endChat(false)
                item.isEnabled = chatController.hasOpenChats()
                return true
            }

            R.id.destruct_chat -> {
                destructChat()
                enableMenu(endMenu, false)
                item.isEnabled = false
                return true
            }

            else -> {
            }
        }
        return false
    }

    override fun onStop() {
        if (isFinishing) { destructChat() }
        super.onStop()
    }

    private fun destructChat() {
        if (hasChatController()) {
            chatController.terminateChat()
            chatController.destruct()
        }
    }

    protected fun enableMenu(@Nullable menuItem: MenuItem?, enable: Boolean) {
        if (menuItem != null) {
            menuItem.isEnabled = enable
            if (enable && !menuItem.isVisible) menuItem.isVisible = true
        }
    }

    override fun onUrlLinkSelected(url: String) {
        toast(baseContext, "got link: $url")
    }

    companion object {
        protected const val TAG = "BasicChat"
        const val ChatTag = "ChatFragment"
    }
}
