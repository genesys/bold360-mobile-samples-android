package com.sdk.samples.topics.history

import com.nanorep.convesationui.structure.history.ChatElementListener
import com.nanorep.convesationui.structure.history.HistoryCallback
import com.nanorep.nanoengine.chatelement.StorableChatElement

interface HistoryProvider: ChatElementListener {
    fun onClear()
}

/**
 * Being used in order to provide a dynamic HistoryElementListener injection option
 */
class HistoryRepository(private val historyProvider: HistoryProvider) : ChatElementListener {

    override fun onFetch(from: Int, direction: Int, callback: HistoryCallback?) {
        historyProvider.onFetch(from, direction, callback)
    }

    override fun onReceive(item: StorableChatElement) {
        historyProvider.onReceive(item)
    }

    override fun onRemove(timestampId: Long) {
        historyProvider.onRemove(timestampId)
    }

    override fun onUpdate(timestampId: Long, item: StorableChatElement) {
        historyProvider.onUpdate(timestampId, item)
    }

    fun clearHistory() {
        historyProvider.onClear()
    }

}