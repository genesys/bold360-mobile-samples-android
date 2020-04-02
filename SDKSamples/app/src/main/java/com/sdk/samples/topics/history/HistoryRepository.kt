package com.sdk.samples.topics.history

import com.nanorep.convesationui.structure.history.ChatElementListener

interface HistoryProvider: ChatElementListener {
    fun clearAll()
    fun release()
    suspend fun count(): Int
}

/**
 * Being used in order to provide a dynamic HistoryElementListener injection option
 */
class HistoryRepository(private val historyProvider: HistoryProvider) : HistoryProvider by historyProvider