package com.sdk.samples.topics

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.integration.core.StateEvent
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.convesationui.structure.controller.ChatEventListener
import com.nanorep.convesationui.structure.controller.ChatLoadResponse
import com.nanorep.convesationui.structure.controller.ChatLoadedListener
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.model.configuration.ConversationSettings
import com.nanorep.sdkcore.utils.NRError
import com.nanorep.sdkcore.utils.hideKeyboard
import com.nanorep.sdkcore.utils.toast
import com.sdk.samples.R
import com.sdk.samples.SamplesViewModel
import com.sdk.samples.SingletonSamplesViewModelFactory
import com.sdk.samples.common.ChatType
import kotlinx.android.synthetic.main.activity_bot_chat.*
import kotlinx.android.synthetic.main.restore_layout.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

abstract class BasicChat : SampleActivity(), ChatEventListener {

    protected lateinit var chatController: ChatController
    protected var destructWithUI: Boolean by Delegates.observable(true) { property, oldValue, newValue ->
        current_radio.isEnabled = !newValue
    }

    @ChatType
    protected lateinit var chatType: String

    protected var endMenu: MenuItem? = null
    protected var destructMenu: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bot_chat)

        topic_title.text = intent.getStringExtra("title")

        startChat()
    }

    open fun startChat() {
        createChat()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    protected open fun getAccount(): Account? = viewModel.account

    protected open fun getBuilder(): ChatController.Builder {
        val settings = createChatSettings()

        return ChatController.Builder(this)
            .chatEventListener(this)
            .conversationSettings(settings)
        /*!- uncomment to set AccountInfoProvider:
             .accountProvider(SimpleAccountWithIdProvider(this)) */
    }

    protected open fun createChatSettings(): ConversationSettings {
        return ConversationSettings()
        /*!- uncomment to set custom datestamp format:
             .datestamp(true, SampleDatestampFactory())
         */
    }

    protected open fun createChat() {
        if (!hasChatController()) {
            chatController = getBuilder().build(
                getAccount(), object : ChatLoadedListener {
                    override fun onComplete(result: ChatLoadResponse) {
                        if (isFinishing || supportFragmentManager.isStateSaved) return

                        hideKeyboard(window.decorView)

                        result.takeIf { it.error == null && it.fragment != null }?.run {
                            supportFragmentManager.beginTransaction()
                                .add(chat_view.id, fragment!!, topic_title.text.toString())
                                .addToBackStack(ChatTag)
                                .commit()

                            onChatLoaded()
                        } ?: kotlin.run {
                            toast(
                                this@BasicChat,
                                "Failed to load chat\nerror:${result.error ?: "failed to get chat fragment"}  "
                            )
                            onChatLoaded()
                        }
                    }
                })
        } else {
            chatController.startChat(getAccount())
        }

        enableMenu(destructMenu, true)
    }

    protected open fun onChatLoaded() {

    }

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

        finishIfLast()
    }

    protected fun removeChatFragment() {
        /* !- launch suspended on a different deamon to prevent activation while on a previous
              fragmentManagerTransaction (exp: onBackPressed on postchat form, triggers StateEvent.Ended
              event call, which calls to remove the chat fragment */
        GlobalScope.launch(Dispatchers.Main) {
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

    protected fun finishIfLast() {
        if (supportFragmentManager.backStackEntryCount == 0) {
            finish()
        }
    }

    override fun onStop() {
        onChatClose()
        super.onStop()
    }

    protected open fun onChatClose() {
        takeIf { isFinishing && ::chatController.isInitialized }?.run {
            chatController.terminateChat()
            chatController.destruct()
        }
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

    open fun hasChatController(): Boolean {
        return this::chatController.isInitialized && !chatController.wasDestructed
    }

    override fun onUrlLinkSelected(url: String) {
        toast(this, "got link: $url")
    }

    companion object {
        protected const val TAG = "BasicChat"
        protected const val ChatTag = "ChatFragment"
    }
}
