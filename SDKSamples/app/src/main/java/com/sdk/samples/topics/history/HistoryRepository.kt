package com.sdk.samples.topics.history

import com.nanorep.convesationui.structure.history.ChatElementListener

interface HistoryProvider: ChatElementListener {
    fun onClearHistory()
    fun onClearResources()
}

/**
 * Being used in order to provide a dynamic HistoryElementListener injection option
 */
class HistoryRepository(private val historyProvider: HistoryProvider) : ChatElementListener by historyProvider {

    fun clearHistory() {
        historyProvider.onClearHistory()
    }

    fun clearResources() {
        historyProvider.onClearResources()
    }
}