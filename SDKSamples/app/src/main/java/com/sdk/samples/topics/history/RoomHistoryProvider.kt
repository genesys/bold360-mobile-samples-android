package com.sdk.samples.topics.history

import android.content.Context
import android.util.Log
import com.nanorep.convesationui.structure.history.HistoryCallback
import com.nanorep.convesationui.structure.history.HistoryFetching
import com.nanorep.nanoengine.chatelement.StorableChatElement
import com.sdk.samples.topics.History
import com.sdk.samples.topics.History.Companion.HistoryPageSize
import kotlinx.coroutines.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.coroutines.CoroutineContext
import kotlin.math.max
import kotlin.math.min

/**
 * An example for a History provider class that uses the Room database
 * @param context The application's context
 * @param coroutineScope The application's lifecycle coroutine scope
 */

class RoomHistoryProvider(var context: Context) : HistoryProvider {

    val coroutineScope = CoroutineScope(Dispatchers.IO + Job())

    private val historyDao = HistoryRoomDB.getInstance(context).historyDao()

    /**
     * Gets all the history from the database(on a I/O thread), and invokes a page of it (page size has been determined at the activity).
     * When finished, it sends the callback to the SDK (on the Main thread).
     */
    override fun onFetch(from: Int, @HistoryFetching.FetchDirection direction: Int, callback: HistoryCallback?) {

        coroutineScope.launch {

            getHistory(from, direction) { history ->

                Log.d(
                    "History",
                    "passing history list to callback, from = " + from + ", size = " + history.size
                )

                launch(Dispatchers.Main) {
                    callback?.onReady(from, direction, history)
                }

            }
        }
    }

    /**
     * Adds an element to the history (on a I/O thread)
     */
    override fun onReceive(item: StorableChatElement) {

        coroutineScope.launch {

            Log.d("history", "onReceive: [type:${item.getType()}][text:${item.text}]")
            historyDao.insert(HistoryElement(item))

        }
    }

    /**
     * Removes an element to the history (on a I/O thread)
     */
    override fun onRemove(timestampId: Long) {

        coroutineScope.launch {

            Log.d("history", "onRemove: [id:$timestampId]")
            historyDao.delete(timestampId)

        }
    }

    /**
     * Updates an element at the history by its timestamp (on a I/O thread)
     */
    override fun onUpdate(timestampId: Long, item: StorableChatElement) {

        coroutineScope.launch {

            Log.d("history", "onUpdate: [id:$timestampId] [text:${item.text}] [status:${item.getStatus()}]")
            historyDao.update(HistoryElement(item).apply { setTimestamp(timestampId) })

        }

    }

    /**
     * Clears all the history from the database (on a I/O thread)
     */
    override fun clearAll() {

        coroutineScope.launch {
            HistoryRoomDB.getInstance(context).clearAllTables()
        }
    }

    override fun release() {

        HistoryRoomDB.clearInstance()

        coroutineScope.cancel() // Cancels this scope, including its job and all its children
    }

    @ExperimentalCoroutinesApi
    override suspend fun count(): Int {
        val result = coroutineScope.async<Int> { historyDao.count() }
        return result.await()
    }

    private suspend fun getHistory ( fromIdx: Int, direction: Int, onFetched: (MutableList<HistoryElement>) -> Unit ) {

        var fromIdx = fromIdx

        val fetchOlder = direction == HistoryFetching.Older
        val historySize = historyDao.count()

        when {
            fromIdx == -1 -> {
                fromIdx = if (fetchOlder) historySize - 1 else 0
            }
            fetchOlder -> {
                fromIdx = historySize - fromIdx
            }
        }

        val toIndex = if (fetchOlder)
            max(0, fromIdx - HistoryPageSize)
        else
            min(fromIdx + HistoryPageSize, historySize - 1)

        Log.d("history", "fetching history: total = $historySize, from $toIndex to $fromIdx")

        // In order to prevent Concurrent exception:
        val accountHistory = CopyOnWriteArrayList(historyDao.getCount(toIndex, fromIdx-toIndex))

        try {
            //Log.v("History", accountHistory.map { "item: ${it.inDate}"}.joinToString("\n"))
            onFetched.invoke(accountHistory)

        } catch (ex: Exception) {
            onFetched.invoke(ArrayList())
        }

    }
}
