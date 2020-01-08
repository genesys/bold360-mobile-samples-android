package com.sdk.samples.topics.history

import androidx.room.*
import com.nanorep.nanoengine.chatelement.ChatElement
import com.nanorep.nanoengine.chatelement.StorableChatElement
import com.nanorep.sdkcore.model.StatementScope
import com.nanorep.sdkcore.model.StatementStatus
import com.nanorep.sdkcore.model.StatusPending

@Dao
interface HistoryDao {

    @Query("SELECT * FROM historyElement ORDER BY timestamp ASC")
    suspend fun getAll(): List<HistoryElement>

    @Query("SELECT * from historyElement ORDER BY timestamp DESC")
    suspend fun getAllReversed(): List<HistoryElement>

    @Query("SELECT * FROM historyElement WHERE timestamp > :from AND timestamp < :to")
    suspend fun loadElements(from: Long, to: Long): List<HistoryElement>

    @Query("DELETE FROM historyElement WHERE timestamp=:timestamp")
    suspend fun delete(timestamp: Long)

    @Insert
    suspend fun insert(historyElement: HistoryElement)

    @Update
    suspend fun update(historyElement: HistoryElement)

}

/**
 * [StorableChatElement] implementing class
 * sample class for app usage
 */

@Entity
open class HistoryElement(var key:ByteArray = byteArrayOf()) : StorableChatElement {

    @PrimaryKey
    @ColumnInfo(name = "timestamp")
    private var timestamp: Long = 0

    override val scope: StatementScope
            get() = StatementScope.NanoBotScope

    @ChatElement.Companion.ChatElementType
    private var type: Int = 0

    @StatementStatus
    private var status = StatusPending

    override var isStorageReady = true

    constructor(type: Int, timestamp: Long) : this() {
        this.type = type
        this.timestamp = timestamp
    }

    constructor(storable: StorableChatElement) :this(storable.getStorageKey()) {
        type = storable.getType()
        timestamp = storable.getTimestamp()
        status = storable.getStatus()
    }

    override fun getStorageKey(): ByteArray {
        return key
    }

    override fun getStorableContent(): String {
        return String(key)
    }

    override fun getType(): Int {
        return type
    }

    override fun getTimestamp(): Long {
        return timestamp
    }

    override fun getStatus(): Int {
        return status
    }

    fun setTimestamp(timestamp: Long) {
        this.timestamp = timestamp
    }

    fun setType(type: Int) {
        this.type = type
    }

    fun setStatus(@StatementStatus status: Int) {
        this.status = status
    }

}
