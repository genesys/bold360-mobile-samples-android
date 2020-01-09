package com.sdk.samples.topics.history

import android.content.Context
import androidx.room.*
import com.nanorep.nanoengine.chatelement.ChatElement
import com.nanorep.nanoengine.chatelement.StorableChatElement
import com.nanorep.sdkcore.model.StatementScope
import com.nanorep.sdkcore.model.StatementStatus
import com.nanorep.sdkcore.model.StatusPending

/**
 * The Room database implementation for the History
 */

private const val HISTORY = "history_database"

@Database(entities = [HistoryElement::class], version = 1)
abstract class HistoryRoomDB: RoomDatabase() {

    abstract fun historyDao(): HistoryDao

    companion object {

        // The Room uses a Singletone design

        @Volatile
        private var instance: HistoryRoomDB? = null

        fun getInstance(context: Context): HistoryRoomDB {

            return instance ?: synchronized(this) {
                instance ?: buildDB(context.applicationContext).also { instance = it }
            }
        }

        private fun buildDB(context: Context) : HistoryRoomDB {
            return Room.databaseBuilder(
                context,
                HistoryRoomDB::class.java,
                HISTORY).build()
        }
    }
}

@Dao
interface HistoryDao {

    @Query("SELECT * FROM historyElement")
    suspend fun getAll(): List<HistoryElement>

    @Query("DELETE FROM historyElement WHERE timestamp=:timestamp")
    suspend fun delete(timestamp: Long)

    @Insert (onConflict = OnConflictStrategy.REPLACE)
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
