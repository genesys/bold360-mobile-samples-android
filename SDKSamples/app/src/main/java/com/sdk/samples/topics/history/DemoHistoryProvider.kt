package com.sdk.samples.topics.history

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.nanorep.convesationui.structure.history.ChatElementListener
import com.nanorep.convesationui.structure.history.HistoryCallback
import com.nanorep.convesationui.structure.history.HistoryFetching
import com.nanorep.nanoengine.chatelement.StorableChatElement
import com.sdk.samples.topics.History
import kotlinx.coroutines.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.max
import kotlin.math.min

class DemoHistoryProvider(val context: Context) : ChatElementListener {

    private var handler: Handler = Handler(Looper.getMainLooper())
    private val historyDao = HistoryRoomDB.getInstance(context, MainScope()).historyDao()

    override fun onFetch(from: Int, @HistoryFetching.FetchDirection direction: Int, callback: HistoryCallback?) {

        GlobalScope.launch(Dispatchers.IO) {

            getHistoryForAccount(from, direction) { history ->

                if (history.isNotEmpty()) {
                    try {
                        Thread.sleep(800) // simulate async history fetching
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }

                if (handler.looper != null) {
                    handler.post {
                        Log.d(
                            "History",
                            "passing history list to callback, from = " + from + ", size = " + history.size
                        )

                        callback?.onReady(from, direction, history)
                    }
                }
            }
        }
    }

    override fun onReceive(item: StorableChatElement) {
        //if(item == null || item.getStatus() != StatusOk) return;
         Log.d("history", "onReceive: [type:${item.getType()}][text:${item.text}]")

        GlobalScope.launch(Dispatchers.IO) {
            historyDao.insert(HistoryElement(item))

        }
    }

    override fun onRemove(timestampId: Long) {
        GlobalScope.launch(Dispatchers.IO) {
            Log.d("history", "onRemove: [id:$timestampId]")
            historyDao.delete(timestampId)
        }
    }

    override fun onUpdate(timestampId: Long, item: StorableChatElement) {
        GlobalScope.launch(Dispatchers.IO) {
            Log.d("history", "onUpdate: [id:$timestampId] [text:${item.text}] [status:${item.getStatus()}]")
            historyDao.update(HistoryElement(item).apply { setTimestamp(timestampId) })
        }
    }

    private suspend fun getHistoryForAccount(fromIdx: Int, direction: Int, onFetched: (MutableList<HistoryElement>) -> Unit) {

        var fromIdx = fromIdx

        val fetchOlder = direction == HistoryFetching.Older

        GlobalScope.launch(Dispatchers.IO) {

            // to prevent Concurrent exception
            val accountHistory = CopyOnWriteArrayList(historyDao.getAll())

            val historySize = accountHistory.size

            if (fromIdx == -1) {
                fromIdx = if (fetchOlder) historySize - 1 else 0
            } else if (fetchOlder) {
                fromIdx = historySize - fromIdx
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

    fun clearRoom() {
        CoroutineScope(Dispatchers.IO).launch {
            HistoryRoomDB.getInstance(context, MainScope()).clearAllTables()
        }
    }
}
