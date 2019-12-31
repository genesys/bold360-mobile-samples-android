package com.sdk.samples.topics

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.integration.core.StateEvent
import com.nanorep.convesationui.structure.FriendlyDatestampFormatFactory
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.convesationui.structure.controller.ChatEventListener
import com.nanorep.convesationui.structure.controller.ChatLoadResponse
import com.nanorep.convesationui.structure.controller.ChatLoadedListener
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.AccountInfo
import com.nanorep.nanoengine.model.configuration.ConversationSettings
import com.nanorep.sdkcore.utils.NRError
import com.nanorep.sdkcore.utils.toast
import com.sdk.samples.R
import kotlinx.android.synthetic.main.activity_bot_chat.*

abstract class BasicChat : AppCompatActivity(), ChatEventListener {

    protected lateinit var chatController: ChatController

    protected var endMenu: MenuItem? = null
    protected var destructMenu: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bot_chat)

        topic_title.text = intent.getStringExtra("title")

    }

    override fun onStart() {
        super.onStart()

        startChat()
    }

    open fun startChat(){
        createChat()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    abstract fun getAccount(): Account

    protected open fun getBuilder() : ChatController.Builder {
        val settings = createChatSettings()

        return ChatController.Builder(this)
            .chatEventListener(this)
            .conversationSettings(settings)
    }

    protected open fun createChatSettings(): ConversationSettings {
        return ConversationSettings()
            .datestamp(true, FriendlyDatestampFormatFactory(this))
    }

    protected fun createChat() {

        chatController = getBuilder().build(
                getAccount(), object : ChatLoadedListener {
                    override fun onComplete(result: ChatLoadResponse) {
                        result.takeIf { it.error == null && it.fragment != null}?.run {
                            supportFragmentManager.beginTransaction()
                                .add(chat_view.id, fragment!!, topic_title.text.toString())
                                .addToBackStack(null)
                                .commit()
                        }
                    }
                }
            )
    }

    override fun onChatStateChanged(stateEvent: StateEvent) {

        Log.d("Chat event", "chat in state: ${stateEvent.state}")
        when (stateEvent.state) {
            StateEvent.ChatWindowDetached -> finish()
            StateEvent.Unavailable -> toast(this@BasicChat, stateEvent.state, Toast.LENGTH_SHORT, ColorDrawable(Color.GRAY))
        }
    }

    override fun onError(error: NRError) {
        super.onError(error)
        toast(this@BasicChat, error.toString(), Toast.LENGTH_SHORT, ColorDrawable(Color.GRAY))
    }

    override fun onAccountUpdate(accountInfo: AccountInfo) {
    }

    override fun onPhoneNumberSelected(phoneNumber: String) {
    }

    override fun onUrlLinkSelected(url: String) {
    }

    override fun onBackPressed() {
        super.onBackPressed()

        if(supportFragmentManager.backStackEntryCount == 0){
            finish()
        }
    }

    override fun onStop() {
        if(isFinishing) {
            if (this::chatController.isInitialized) chatController.terminateChat()
        }
        super.onStop()
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_main, menu)

        this.endMenu = menu?.findItem(R.id.end_current_chat)
        this.destructMenu = menu?.findItem(R.id.destruct_chat)

        if (this::chatController.isInitialized) {
            enableMenu(endMenu, chatController.hasOpenChats())
            enableMenu(destructMenu, true)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> return false
           
            R.id.end_current_chat -> {
                chatController.endChat(false)
                return true
            }
           
            R.id.destruct_chat -> {
                chatController.destruct()
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
}
