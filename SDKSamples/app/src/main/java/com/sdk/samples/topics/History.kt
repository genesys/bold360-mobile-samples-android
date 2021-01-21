//package com.sdk.samples.topics
//
//import android.view.Menu
//import android.view.MenuItem
//import com.common.chatComponents.history.HistoryRepository
//import com.common.chatComponents.history.RoomHistoryProvider
//import com.common.topicsbase.BasicChat
//import com.nanorep.convesationui.structure.controller.ChatController
//import com.nanorep.nanoengine.Account
//import com.nanorep.nanoengine.bot.BotAccount
//import com.sdk.samples.R
//abstract class History : BasicChat() {
//
//    protected lateinit var historyRepository: HistoryRepository
//    protected var historyMenu: MenuItem? = null
//
//    companion object {
//        const val HistoryPageSize = 8
//
//        internal fun Account.getGroupId(): String? {
//            return apiKey.takeUnless { it.isBlank() } ?: (this as? BotAccount)?.let { "${it.account ?: ""}#${it.knowledgeBase}" }
//        }
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        super.onCreateOptionsMenu(menu)
//        historyMenu = menu?.findItem(R.id.clear_history)
//        historyMenu?.isVisible = true
//        if(hasChatController()){
//            enableMenu(historyMenu, true)
//        }
//        return true
//    }
//
//    /**
//     * Adding history save support to the ChatController
//     */
//    override fun getBuilder(): ChatController.Builder {
//
//        historyRepository = HistoryRepository(RoomHistoryProvider(this, getAccount().getGroupId()))
//
//        enableMenu(historyMenu, true)
//
//        return super.getBuilder()
//            .chatElementListener(historyRepository)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        super.onOptionsItemSelected(item)
//
//        when (item.itemId) {
//            R.id.clear_history -> {
//                historyRepository.clear()
//                return true
//            }
//        }
//
//        return false
//    }
//
//    override fun onStop() {
//        takeIf { isFinishing && ::historyRepository.isInitialized }?.historyRepository?.release()
//        super.onStop()
//    }
//}