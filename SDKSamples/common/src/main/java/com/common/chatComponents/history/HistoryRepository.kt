package com.common.chatComponents.history

import com.nanorep.convesationui.structure.history.ChatElementListener

interface HistoryProvider: ChatElementListener {
    var targetId: String?

    fun clear()

    fun release()

    suspend fun count(): Int
}

/**
 * Being used in order to provide a dynamic HistoryElementListener injection option
 */
class HistoryRepository(private val historyProvider: HistoryProvider) : HistoryProvider by historyProvider