package com.sdk.samples.topics

import android.os.Bundle
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.bot.BotAccount
import com.sdk.samples.topics.history.DemoHistoryProvider

open class History : BotChat() {

    var historyProvider: DemoHistoryProvider? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    companion object {
        const val HistoryPageSize = 8
    }

    override fun getAccount(): Account {
        return BotAccount("8bad6dea-8da4-4679-a23f-b10e62c84de8", "jio",
            "Staging_Updated", "qa07", null)
    }
    
    override fun getBuilder(): ChatController.Builder {

        historyProvider = DemoHistoryProvider(this)

        val settings = createChatSettings()

        return ChatController.Builder(this).apply {

            conversationSettings(settings)
            chatEventListener(this@History)
            chatElementListener(historyProvider!!)
        }
    }

    override fun finish() {
        super.finish()
        //historyProvider?.clearRoom()
    }

}