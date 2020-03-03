package com.sdk.samples.topics

import android.view.Menu
import android.view.MenuItem
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.bot.BotAccount
import com.sdk.samples.R
import com.sdk.samples.topics.history.HistoryRepository
import com.sdk.samples.topics.history.RoomHistoryProvider

open class History : BotChat() {

    private lateinit var historyRepository: HistoryRepository

    companion object {
        const val HistoryPageSize = 8
    }

    override fun getAccount(): Account {
        return BotAccount("8bad6dea-8da4-4679-a23f-b10e62c84de8", "jio",
            "Staging_Updated", "qa07", null)
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