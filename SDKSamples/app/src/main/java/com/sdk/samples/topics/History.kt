package com.sdk.samples.topics

import android.view.Menu
import android.view.MenuItem
import com.nanorep.convesationui.structure.controller.ChatController
import com.sdk.samples.R
import com.sdk.samples.topics.history.HistoryRepository
import com.sdk.samples.topics.history.RoomHistoryProvider

open class History : BoldChatAsync(){//} BotChat() {

    private lateinit var historyRepository: HistoryRepository

    companion object {
        const val HistoryPageSize = 8
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menu?.findItem(R.id.clear_history)?.isVisible = true
        return true
    }

    override fun getBuilder(): ChatController.Builder {

        historyRepository = HistoryRepository( RoomHistoryProvider(this) )

        return super.getBuilder()
            .chatElementListener( historyRepository )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)

        when (item.itemId) {
            R.id.clear_history -> {
                historyRepository.clearAll()
                chatController.destruct()

                return true
            }
        }

        return false
    }

    override fun finish() {
        historyRepository.release()
        super.finish()
    }

}