package com.sdk.samples.topics.base

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.integration.core.StateEvent
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.convesationui.structure.controller.ChatEventListener
import com.nanorep.nanoengine.model.configuration.ConversationSettings
import com.nanorep.sdkcore.utils.NRError
import com.nanorep.sdkcore.utils.hideKeyboard
import com.nanorep.sdkcore.utils.toast
import com.sdk.samples.R
import kotlinx.android.synthetic.main.activity_basic.*
import kotlinx.android.synthetic.main.activity_basic.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class BasicChat : SampleActivity(), ChatEventListener {

    protected var endMenu: MenuItem? = null
    protected var destructMenu: MenuItem? = null

    override var onChatLoaded: (fragment: Fragment) -> Unit = { fragment ->

        if (!isFinishing && !supportFragmentManager.isStateSaved) {

            basic_loading.visibility = View.GONE

            hideKeyboard(window.decorView)

            supportFragmentManager.beginTransaction()
                .add(
                    basic_chat_view.id,
                    fragment,
                    topicTitle
                )
                .addToBackStack(ChatTag)
                .commit()
        } else {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic)

        topic_title.text = topicTitle

        chatProvider.onChatLoaded = onChatLoaded

        startChat()
    }

    open fun startChat() {
        createChat()
    }

    protected open fun createChat() {
        chatProvider.create(getChatBuilder())?.let {
            chatController = it
        } ?: kotlin.run {
            toast(baseContext, "Could not initiate Chat", Toast.LENGTH_SHORT)
            finishIfLast()
        }
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

    protected open fun onChatLoaded() {}

    override fun onChatStateChanged(stateEvent: StateEvent) {

        Log.d(TAG, "chat in state: ${stateEvent.state}")

        when (stateEvent.state) {
            StateEvent.Started -> enableMenu(endMenu, chatController.hasOpenChats())

            StateEvent.ChatWindowDetached -> onChatUIDetached()

            StateEvent.Unavailable -> lifecycleScope.launch {
                toast(this@BasicChat, stateEvent.state, Toast.LENGTH_SHORT)
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
        lifecycleScope.launch { toast(this@BasicChat, error.toString(), Toast.LENGTH_SHORT) }
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
                supportFragmentManager.takeUnless { it.isDestroyed }?.popBackStackImmediate(
                    ChatTag,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
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

    protected open fun destructChat() {
        if (hasChatController()) chatController.destruct()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.end_current_chat -> {
                chatController.endChat(false)
                item.isEnabled = false
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

    protected open fun enableMenu(@Nullable menuItem: MenuItem?, enable: Boolean) {
        if (menuItem != null) {
            menuItem.isEnabled = enable
        }
    }

    protected fun hasChatController(): Boolean = chatProvider.hasChatController()

    override fun onUrlLinkSelected(url: String) {
        toast(this, "got link: $url")
    }

    companion object {
        protected const val TAG = "BasicChat"
        const val ChatTag = "ChatFragment"
    }
}