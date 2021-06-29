package com.common.topicsbase

import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.common.chatComponents.history.HistoryMigrationProvider
import com.common.chatComponents.history.HistoryRepository
import com.common.chatComponents.history.RoomHistoryProvider
import com.nanorep.convesationui.structure.controller.ChatController
import com.nanorep.convesationui.utils.HistoryMigration
import com.nanorep.nanoengine.Account
import com.nanorep.nanoengine.bot.BotAccount
import com.nanorep.sdkcore.utils.MutableLazy
import com.sdk.common.R
import kotlinx.coroutines.ExperimentalCoroutinesApi


abstract class History : BasicChat() {

    private var historyMenu: MenuItem? = null

    protected var historyProvider: HistoryRepository? by MutableLazy {
        HistoryRepository(RoomHistoryProvider(this, account?.getGroupId()))
    }

    /**
     * Updates the History provider
     */
    fun updateHistoryRepo(targetId: String? = null) {
        targetId?.let { historyProvider?.targetId = targetId }
    }


    //  </editor-fold>


    companion object {

        fun Account.getGroupId(): String? {
            return apiKey.takeUnless { it.isBlank() }
                    ?: (this as? BotAccount)?.let { "${it.account.orEmpty()}#${it.knowledgeBase}" }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        historyMenu = menu?.findItem(R.id.clear_history)
        historyMenu?.isVisible = true
        if (hasChatController()) {
            enableMenu(historyMenu, true)
        }
        return true
    }

    /**
     * Adding history save support to the ChatController
     */
    @ExperimentalCoroutinesApi
    override fun getChatBuilder(): ChatController.Builder? {

        enableMenu(historyMenu, true)

        return super.getChatBuilder()?.apply { historyProvider?.let { chatElementListener(it) } }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)

        when (item.itemId) {
            R.id.clear_history -> {
                historyProvider?.clear()
                return true
            }
        }

        return false
    }

    @ExperimentalCoroutinesApi
    override fun startSample(isStateSaved: Boolean) {

        HistoryMigration.start(HistoryMigrationProvider(this) {
            runOnUiThread {
                Log.d("BotChatHistory", "Migration completed. starting chat...")
                super.startSample(isStateSaved)
            }
        })

    }

}