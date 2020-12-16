package com.sdk.samples.topics

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.Nullable
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
import com.sdk.samples.SampleActivity
import kotlinx.android.synthetic.main.activity_basic.*
import kotlinx.android.synthetic.main.activity_basic.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class BasicChat : SampleActivity(), ChatEventListener {

    protected var endMenu: MenuItem? = null
    protected var destructMenu: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic)

        topic_title.text = intent.getStringExtra("title")

        startChat()
    }

    override fun onStart() {
        super.onStart()
        chatProvider.onChatLoaded =  { fragment ->

            if (!isFinishing && !supportFragmentManager.isStateSaved) {

                basic_loading.visibility = View.GONE

                hideKeyboard(window.decorView)

                supportFragmentManager.beginTransaction()
                    .add(
                        basic_chat_view.id,
                        fragment,
                        topic_title.text.toString()
                    )
                    .addToBackStack(ChatTag)
                    .commit()
            } else {
                finish()
            }
        }
    }

    open fun startChat() {
        createChat()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    protected open fun createChat() {
        chatController = chatProvider.create(getChatBuilder())!!
    }

    protected open fun getChatBuilder(): ChatController.Builder? {
        return ChatController.Builder(applicationContext)
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

            StateEvent.ChatWindowLoaded -> enableMenu(endMenu, chatController.hasOpenChats())

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

        finishIfLast()
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

    override fun onStop() {
        onChatClose()
        super.onStop()
    }

    protected open fun onChatClose() {
        if (isFinishing) { chatProvider.destruct() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_main, menu)

        this.endMenu = menu?.findItem(R.id.end_current_chat)
        this.destructMenu = menu?.findItem(R.id.destruct_chat)

        if (hasChatController()) {
            enableMenu(endMenu, chatController.hasOpenChats())
            enableMenu(destructMenu, true)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.end_current_chat -> {
                chatController.endChat(false)
                return true
            }

            R.id.destruct_chat -> {
                item.isEnabled = false
                finish()
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
        protected const val ChatTag = "ChatFragment"
    }
}
