package com.sdk.samples.topics

import android.view.Menu
import android.view.MenuItem
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.bot.BotAccount
import com.sdk.samples.R
import com.sdk.samples.topics.history.HistoryRepository
import com.sdk.samples.topics.history.RoomHistoryProvider

abstract class History : BasicChat() {

    private lateinit var historyRepository: HistoryRepository

    companion object {
        const val HistoryPageSize = 8

        private fun Account.getGroupId(): String? {
            return apiKey.takeUnless { it.isBlank() } ?: (this as? BotAccount)?.let { "${it.account ?: ""}#${it.knowledgeBase}" }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menu?.findItem(R.id.clear_history)?.isVisible = true
        return true
    }

    /**
     * Adding history save support to the ChatController
     */
    override fun getBuilder(): ChatController.Builder {

        historyRepository = HistoryRepository(RoomHistoryProvider(this, getAccount().getGroupId()))

        return super.getBuilder()
            .chatElementListener(historyRepository)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)

        when (item.itemId) {
            R.id.clear_history -> {
                historyRepository.clear()
                finish()
                return true
            }
        }

        return false
    }

    override fun onStop() {
        takeIf { isFinishing && ::historyRepository.isInitialized }?.historyRepository?.release()
        super.onStop()
    }
}