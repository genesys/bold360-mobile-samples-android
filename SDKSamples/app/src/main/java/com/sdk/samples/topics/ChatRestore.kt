package com.sdk.samples.topics

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.integration.async.core.UserInfo
import com.integration.core.userInfo
import com.nanorep.convesationui.async.AsyncAccount
import com.nanorep.convesationui.bold.model.BoldAccount
import com.nanorep.convesationui.structure.FriendlyDatestampFormatFactory
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.convesationui.structure.controller.ChatEventListener
import com.nanorep.convesationui.structure.controller.ChatLoadResponse
import com.nanorep.convesationui.structure.controller.ChatLoadedListener
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.AccountInfo
import com.nanorep.nanoengine.bot.BotAccount
import com.nanorep.nanoengine.model.configuration.ConversationSettings
import com.nanorep.sdkcore.utils.NRError
import com.nanorep.sdkcore.utils.toast
import com.sdk.samples.R
import kotlinx.android.synthetic.main.restore_activity.*

open class ChatRestore : AppCompatActivity(), ChatEventListener {

    private lateinit var chatController: ChatController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.restore_activity)

        topic_title.text = intent.getStringExtra("title")
    }

    override fun onStart() {
        super.onStart()

        startChat()
    }

    private fun startChat() {

        bot_chat.setOnClickListener { onAccountClick(it.id) }
        bold_chat.setOnClickListener { onAccountClick(it.id) }
        async_chat.setOnClickListener { onAccountClick(it.id) }

        restore_chat.setOnClickListener {

            if (this::chatController.isInitialized) {

                val resId = resources.getIdentifier(
                    findViewById<RadioButton>(restore_chat_with_account_group.checkedRadioButtonId).tag as String,
                    "id",
                    packageName
                )

                val account = getAccount(resId)

                try {
                    (account as? BotAccount)?.run { chatController.startChat(this) }

                        ?: kotlin.run {

                            chatController.restoreChat(
                                null,
                                account,
                                !preserver_handler.isChecked
                            )

                        }

                } catch (ex: IllegalStateException) {
                    toast(
                        this,
                        NRError(ex).toString(),
                        Toast.LENGTH_SHORT, ColorDrawable(Color.GRAY)
                    )
                }

            } else {

                toast(
                    this,
                    "ChatController was not initialized", Toast.LENGTH_SHORT,
                    ColorDrawable(Color.GRAY)
                )

            }
        }
    }

    override fun finish() {
        super.finish()
        if (this::chatController.isInitialized) chatController.terminateChat()
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    private fun onAccountClick(id: Int) {
        setLoading(true)
        chatController = getBuilder().build(
            getAccount(id), object : ChatLoadedListener {
                override fun onComplete(result: ChatLoadResponse) {
                    result.takeIf { it.error == null && it.fragment != null }?.run {
                        supportFragmentManager.beginTransaction()
                            .replace(chat_view.id, fragment!!, topic_title.text.toString())
                            .addToBackStack(null)
                            .commit()
                        setLoading(false)
                    }
                }
            },
            !preserver_handler.isChecked
        )
    }

    private fun getBuilder(): ChatController.Builder {
        val settings = createChatSettings()

        return ChatController.Builder(this)
            .chatEventListener(this)
            .conversationSettings(settings)
    }

    private fun createChatSettings(): ConversationSettings {
        return ConversationSettings()
            .datestamp(true, FriendlyDatestampFormatFactory(this))
    }

    private fun getAccount(viewId: Int): Account =

        when (viewId) {
            R.id.bot_chat -> BotAccount(
                "8bad6dea-8da4-4679-a23f-b10e62c84de8", "jio",
                "Staging_Updated", "qa07", null
            )

            R.id.bold_chat -> BoldAccount("2300000001700000000:2278936004449775473:sHkdAhpSpMO/cnqzemsYUuf2iFOyPUYV")

            else -> {
                
                val account = AsyncAccount(
                    "2307475884:2403340045369405:KCxHNTjbS7qDY3CVmg0Z5jqHIIceg85X:alphawd2",
                    "mobile12345"
                )

                val userInfo = UserInfo("1234567654321234567")
                userInfo.firstName = "fame"
                userInfo.lastName = "s"
                userInfo.email = "android@is.s"
                userInfo.phoneNumber = "09666"

                account.getInfo().userInfo = userInfo

                account
            }
        }

    private fun setLoading(loading: Boolean) {
        bot_chat.isEnabled = !loading
        bold_chat.isEnabled = !loading
        async_chat.isEnabled = !loading
        restore_chat.isEnabled = !loading
        progressBar.visibility = if (loading) View.VISIBLE else View.INVISIBLE
    }

    override fun onError(error: NRError) {
        super.onError(error)
        toast(
            this,
            error.toString(), Toast.LENGTH_SHORT,
            ColorDrawable(Color.GRAY)
        )
    }

    override fun onAccountUpdate(accountInfo: AccountInfo) {}

    override fun onPhoneNumberSelected(phoneNumber: String) {}

    override fun onUrlLinkSelected(url: String) {}
}