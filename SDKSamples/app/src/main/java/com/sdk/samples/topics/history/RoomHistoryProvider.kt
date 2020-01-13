package com.sdk.samples.topics.history

import android.content.Context
import android.util.Log
import com.nanorep.convesationui.structure.history.HistoryCallback
import com.nanorep.convesationui.structure.history.HistoryFetching
import com.nanorep.nanoengine.chatelement.StorableChatElement
import com.sdk.samples.topics.History
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.max
import kotlin.math.min

/**
 * An example for a History provider class that uses the Room database
 * @param context The application's context
 * @param coroutineScope The application's lifecycle coroutine scope
 */

class RoomHistoryProvider(var context: Context, var coroutineScope: CoroutineScope) : HistoryProvider {

    private val historyDao = HistoryRoomDB.getInstance(context).historyDao()

    override fun onFetch(from: Int, @HistoryFetching.FetchDirection direction: Int, callback: HistoryCallback?) {

        coroutineScope.launch {

            getHistory(from, direction) { history ->

                Log.d(
                    "History",
                    "passing history list to callback, from = " + from + ", size = " + history.size
                )

                callback?.onReady(from, direction, history)

            }
        }
    }

    override fun onReceive(item: StorableChatElement) {

        coroutineScope.launch {

            Log.d("history", "onReceive: [type:${item.getType()}][text:${item.text}]")
            historyDao.insert(HistoryElement(item))

        }
    }

    override fun onRemove(timestampId: Long) {

        coroutineScope.launch {

            Log.d("history", "onRemove: [id:$timestampId]")
            historyDao.delete(timestampId)

        }
    }

    override fun onUpdate(timestampId: Long, item: StorableChatElement) {

        coroutineScope.launch {

            Log.d("history", "onUpdate: [id:$timestampId] [text:${item.text}] [status:${item.getStatus()}]")
            historyDao.update(HistoryElement(item).apply { setTimestamp(timestampId) })

        }

    }

    override fun onClear() {

        coroutineScope.launch(Dispatchers.IO)  {

            HistoryRoomDB.getInstance(context).clearAllTables()

        }
    }

    private suspend fun getHistory ( fromIdx: Int, direction: Int, onFetched: (MutableList<HistoryElement>) -> Unit ) {

        var fromIdx = fromIdx

        val fetchOlder = direction == HistoryFetching.Older

        // In order to prevent Concurrent exception:
        val accountHistory = CopyOnWriteArrayList(historyDao.getAll())

        val historySize = accountHistory.size

        when {
            fromIdx == -1 -> { fromIdx = if (fetchOlder) historySize - 1 else 0 }
            fetchOlder -> { fromIdx = historySize - fromIdx }
        }

        val toIndex = if (fetchOlder)
            max(0, fromIdx - History.HistoryPageSize)
        else
            min(fromIdx + History.HistoryPageSize, historySize - 1)

        try {
            Log.d("History", "fetching history items ($historySize) from $toIndex to $fromIdx")
            onFetched.invoke(accountHistory.subList(toIndex, fromIdx))
        } catch (ex: Exception) {
            onFetched.invoke(ArrayList())
        }

    }
}
