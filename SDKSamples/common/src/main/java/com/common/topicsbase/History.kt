package com.common.topicsbase

import android.view.Menu
import android.view.MenuItem
import com.common.chatComponents.history.HistoryRepository
import com.common.chatComponents.history.RoomHistoryProvider
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.bot.BotAccount
import com.sdk.common.R
import kotlinx.coroutines.ExperimentalCoroutinesApi

abstract class History : BasicChat() {

    private var historyMenu: MenuItem? = null

    companion object {

        fun Account.getGroupId(): String? {
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
    @ExperimentalCoroutinesApi
    override fun getChatBuilder(): ChatController.Builder? {

        updateHistoryRepo( HistoryRepository( RoomHistoryProvider(this, getAccount()?.getGroupId(), 8) ) )

        enableMenu(historyMenu, true)

        return super.getChatBuilder()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)

        when (item.itemId) {
            R.id.clear_history -> {
                clearHistory()
                return true
            }
        }

        return false
    }
}