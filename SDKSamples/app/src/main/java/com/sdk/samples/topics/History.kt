package com.sdk.samples.topics

import android.view.MenuItem
import androidx.lifecycle.lifecycleScope
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.bot.BotAccount
import com.sdk.samples.R
import com.sdk.samples.topics.history.HistoryRepository
import com.sdk.samples.topics.history.RoomHistoryProvider

open class History : BotChat() {

    lateinit var historyRepository: HistoryRepository

    companion object {
        const val HistoryPageSize = 8
    }

    override fun getAccount(): Account {
        return BotAccount("8bad6dea-8da4-4679-a23f-b10e62c84de8", "jio",
            "Staging_Updated", "qa07", null)
    }

    override fun getBuilder(): ChatController.Builder {

        historyRepository = HistoryRepository(RoomHistoryProvider(this, lifecycleScope))

        val settings = createChatSettings()

        return ChatController.Builder(this).apply {

            conversationSettings(settings)
            chatEventListener(this@History)
            chatElementListener(historyRepository)
        }
    }

    override fun onChatLoaded() {
        super.onChatLoaded()
        menu?.findItem(R.id.clear_history)?.isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)

        when (item.itemId) {
            R.id.clear_history -> {
                historyRepository.clearHistory()

                chatController.destruct()
                return true
            }
        }

        return false
    }


}