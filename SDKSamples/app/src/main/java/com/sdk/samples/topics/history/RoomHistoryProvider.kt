package com.sdk.samples.topics.history

import android.content.Context
import android.util.Log
import com.nanorep.convesationui.structure.elements.StorableChatElement
import com.nanorep.convesationui.structure.history.HistoryCallback
import com.nanorep.convesationui.structure.history.HistoryFetching
import com.nanorep.convesationui.utils.ElementMigration
import com.nanorep.sdkcore.utils.SystemUtil
import com.sdk.samples.topics.History.Companion.HistoryPageSize
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.collections.ArrayList
import kotlin.math.max
import kotlin.math.min

/**
 * An example for a History provider class that uses the Room database
 * @param context The application's context
 * @param coroutineScope The application's lifecycle coroutine scope
 */

@ExperimentalCoroutinesApi
open class RoomHistoryProvider(var context: Context, override var targetId: String? = null) :
    HistoryProvider {

    protected var pageSize = HistoryPageSize

    protected open val fetchDispatcher: CoroutineDispatcher = Dispatchers.Main

    protected val coroutineScope = CoroutineScope(Dispatchers.IO + Job())

    protected val historyDao = HistoryRoomDB.getInstance(context).historyDao()

    /**
     * Gets all the history from the database(on a I/O thread), and invokes a page of it (page size has been determined at the activity).
     * When finished, it sends the callback to the SDK (on the Main thread).
     */
    override fun onFetch(
        from: Int,
        @HistoryFetching.FetchDirection direction: Int,
        callback: HistoryCallback?
    ) {

        Log.d("history", "got fetch request : from $from direction $direction")

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

    /**
     * Adds an element to the history (on a I/O thread)
     */
    @ExperimentalCoroutinesApi
    override fun onReceive(item: StorableChatElement) {
        // start immediately the insert action without being suspended.
        // this Room version verifies DB actions not on main thread.
        coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {

            targetId?.run {
                historyDao.insert(HistoryElement(this, item).apply {
                    inDate = Date(SystemUtil.syncedCurrentTimeMillis())
                    Log.d(
                        "history",
                        "onReceive: [inDate:${inDate}][timestamp:${getTimestamp()}][text:${textContent}]"
                    )
                })
            } ?: Log.e("history", "onReceive: targetId is null action is canceled")
        }
    }

    /**
     * Removes an element to the history (on a I/O thread)
     */
    @ExperimentalCoroutinesApi
    override fun onRemove(timestampId: Long) {

        coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
            targetId?.run {
                Log.d("history", "onRemove: [id:$timestampId]")
                historyDao.delete(this, timestampId)
            } ?: Log.e("history", "onReceive: targetId is null action is canceled")
        }
    }

    @ExperimentalCoroutinesApi
    override fun onRemove(id: String) {

        coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
            targetId?.run {
                Log.d("history", "onRemove: [id:$id]")
                historyDao.delete(this, id)
            } ?: Log.e("history", "onReceive: targetId is null action is canceled")
        }
    }

    /**
     * Updates an element at the history by its timestamp (on a I/O thread)
     */
    @ExperimentalCoroutinesApi
    override fun onUpdate(timestampId: Long, item: StorableChatElement) {

        coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {

            Log.d(
                "history",
                "onUpdate: [id:$timestampId] [text:${item.text}] [status:${item.getStatus()}]"
            )
            targetId?.run {
                historyDao.update(this, timestampId, item.getStorageKey(), item.getStatus())
            } ?: Log.e("history", "onReceive: targetId is null action is canceled")
        }
    }

    @ExperimentalCoroutinesApi
    override fun onUpdate(id: String, item: StorableChatElement) {

        coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {

            Log.d("history", "onUpdate: [id:$id] [text:${item.text}] [status:${item.getStatus()}]")
            targetId?.run {
                historyDao.update(
                    this,
                    id,
                    item.getTimestamp(),
                    item.getStorageKey(),
                    item.getStatus()
                )
            } ?: Log.e("history", "onReceive: targetId is null action is canceled")
        }
    }

    override fun clear() {

        coroutineScope.launch(Dispatchers.IO) {
            targetId?.run {
                historyDao.delete(this)
            }
                ?: HistoryRoomDB.getInstance(context).clearAllTables()
        }
    }

    /**
     * Clears all the history from the database (on a I/O thread)
     */
    fun clearAll() {

        coroutineScope.launch {
            HistoryRoomDB.getInstance(context).clearAllTables()
        }
    }

    override fun release() {

        HistoryRoomDB.clearInstance()

        coroutineScope.cancel() // Cancels this scope, including its job and all its children
    }

    override suspend fun count(): Int {
        val result = coroutineScope.async<Int> { targetId?.let { historyDao.count(it) } ?: 0 }
        return result.await()
    }

    protected open suspend fun getCount(toIndex: Int, fromIdx: Int): List<HistoryElement> {
        return targetId?.let { historyDao.getCount(it, toIndex, fromIdx - toIndex) }
            ?: emptyList()
    }

    private suspend fun getHistory(
        startFromIdx: Int,
        direction: Int,
        onFetched: (MutableList<HistoryElement>) -> Unit
    ) {

        var fromIdx = startFromIdx

        val fetchOlder = direction == HistoryFetching.Older
        val historySize = count()

        Log.d("history", "got history size = $historySize, fromIdx = $fromIdx")

        when {
            fromIdx == -1 -> {
                fromIdx = if (fetchOlder) historySize - 1 else 0
            }
            fetchOlder -> {
                fromIdx = historySize - fromIdx
            }
        }

        val toIndex = if (fetchOlder)
            max(0, fromIdx - pageSize)
        else
            min(fromIdx + pageSize, historySize - 1)

        Log.d("history", "fetching history: total = $historySize, from $toIndex to $fromIdx")

        // In order to prevent Concurrent exception:
        val accountHistory = CopyOnWriteArrayList(getCount(toIndex, fromIdx))

        try {
            //Log.v("History", accountHistory.map { "item: ${it.inDate}"}.joinToString("\n"))
            coroutineScope.launch(fetchDispatcher) { onFetched.invoke(accountHistory) }

        } catch (ex: Exception) {
            onFetched.invoke(ArrayList())
        }

    }
}

@ExperimentalCoroutinesApi
class HistoryMigrationProvider(context: Context, var onDone:(()->Unit)? = null) : RoomHistoryProvider(context), ElementMigration {

    init {
        pageSize = 20
    }

    private var fetchedChunk: List<HistoryElement> = listOf()

    override val fetchDispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun getCount(toIndex: Int, fromIdx: Int): List<HistoryElement> {
        fetchedChunk = historyDao.getCount(toIndex, fromIdx - toIndex)
        return fetchedChunk
    }

    override fun onReplace(from: Int, migration: Map<String, StorableChatElement>?) {
        Log.d("history", "got replace from = $from of ${migration?.size?:0} items")

        coroutineScope.launch {
            migration?.takeUnless { it.isEmpty() }?.forEach { (prevId, storable) ->
                val (groupId, inDate) = fetchedChunk.find { it.getId() == prevId }
                    ?.let { it.groupId to it.inDate } ?: null to null
                groupId?.run {
                    historyDao.delete(this, prevId)
                    historyDao.insert(HistoryElement(this, storable).apply {
                        this.inDate = inDate!!
                    })
                }
            }?:onDone?.invoke()
        }
    }

    override suspend fun count(): Int {
        val result = coroutineScope.async<Int> { historyDao.count() }
        return result.await()
    }

}