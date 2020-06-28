package com.sdk.samples.topics

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
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
import kotlinx.android.synthetic.main.activity_bot_chat.*
import kotlinx.android.synthetic.main.restore_layout.*
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

abstract class BasicChat : AppCompatActivity(), ChatEventListener {

    protected lateinit var chatController: ChatController
    protected var destructWithUI: Boolean by Delegates.observable(true) { property, oldValue, newValue ->
        current_radio.isEnabled = !newValue
    }

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

    abstract fun getAccount(): Account

    protected open fun getBuilder(): ChatController.Builder {
        val settings = createChatSettings()

        return ChatController.Builder(this)
            .chatEventListener(this)
            .conversationSettings(settings)
        // for tests: .accountProvider(SimpleAccountProvider())
    }

    protected open fun createChatSettings(): ConversationSettings {
        return ConversationSettings()
        //uncomment to set custom datestamp format: .datestamp(true, SampleDatestampFactory())
    }

    protected open fun createChat() {

        if (!hasChatController()) {
            chatController = getBuilder().build(
                getAccount(), object : ChatLoadedListener {
                    override fun onComplete(result: ChatLoadResponse) {
                        hideKeyboard(window.decorView)
                        result.takeIf { it.error == null && it.fragment != null }?.run {
                            supportFragmentManager.beginTransaction()
                                .add(chat_view.id, fragment!!, topic_title.text.toString())
                                .addToBackStack(null)
                                .commit()

                            onChatLoaded()
                        } ?: kotlin.run {
                            onChatLoaded()
                        }
                    }
                })
        } else {
            chatController.startChat(getAccount())
        }
    }

    protected open fun onChatLoaded() {

    }

    override fun onChatStateChanged(stateEvent: StateEvent) {

        Log.d("Chat event", "chat in state: ${stateEvent.state}")
        when (stateEvent.state) {
            StateEvent.ChatWindowDetached -> onChatUIDetached()
            StateEvent.Unavailable -> lifecycleScope.launch {
                toast(this@BasicChat, stateEvent.state, Toast.LENGTH_SHORT)
            }
            StateEvent.Ended -> if(!chatController.hasOpenChats()) {
                finish()
            }
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

    open protected fun onChatUIDetached() {
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

    protected open fun onChatClose(){
        takeIf { isFinishing && ::chatController.isInitialized }?.run{
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
                //chatController.destruct()
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

    fun hasChatController(): Boolean {
        return this::chatController.isInitialized && !chatController.wasDestructed
    }

}
