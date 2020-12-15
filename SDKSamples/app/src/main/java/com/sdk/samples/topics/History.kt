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

    protected lateinit var historyRepository: HistoryRepository
    protected var historyMenu: MenuItem? = null

    companion object {
        const val HistoryPageSize = 8

        internal fun Account.getGroupId(): String? {
            return apiKey.takeUnless { it.isBlank() } ?: (this as? BotAccount)?.let { "${it.account ?: ""}#${it.knowledgeBase}" }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        historyMenu = menu?.findItem(R.id.clear_history)
        historyMenu?.isVisible = true
        if(hasChatController()){
            enableMenu(historyMenu, true)
        }
        return true
    }

    /**
     * Adding history save support to the ChatController
     */
    override fun getChatBuilder(): ChatController.Builder? {

        historyRepository = HistoryRepository(RoomHistoryProvider(this, getAccount().getGroupId()))

        enableMenu(historyMenu, true)

        return super.getChatBuilder()?.chatElementListener(historyRepository)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)

        when (item.itemId) {
            R.id.clear_history -> {
                historyRepository.clear()
                return true
            }
        }

        return false
    }

    override fun onStop() {
        destructHistory()
        super.onStop()
    }

    fun destructHistory() {
        takeIf { isFinishing && ::historyRepository.isInitialized }?.historyRepository?.release()
    }
}