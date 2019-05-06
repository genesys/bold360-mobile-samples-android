package com.sdk.samples.topics

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.integration.core.StateEvent
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.convesationui.structure.controller.ChatEventListener
import com.nanorep.convesationui.structure.controller.ChatLoadResponse
import com.nanorep.convesationui.structure.controller.ChatLoadedListener
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.AccountInfo
import com.sdk.samples.R
import kotlinx.android.synthetic.main.activity_bot_chat.*

abstract class BasicChat : AppCompatActivity(), ChatEventListener {

    private lateinit var chatController: ChatController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bot_chat)

        topic_title.text = intent.getStringExtra("title")

        createChat()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    abstract fun getAccount(): Account

    protected fun createChat() {
        chatController = ChatController.Builder(this)
            .chatEventListener(this)
            .build(
                getAccount(), object : ChatLoadedListener {
                    override fun onComplete(result: ChatLoadResponse) {
                        result.takeIf { it.error == null }?.run {
                            supportFragmentManager.beginTransaction()
                                .add(chat_view.id, fragment, topic_title.text.toString())
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
        }
    }

    override fun onAccountUpdate(accountInfo: AccountInfo) {
    }

    override fun onPhoneNumberSelected(phoneNumber: String) {
    }

    override fun onUrlLinkSelected(url: String) {
    }
}
