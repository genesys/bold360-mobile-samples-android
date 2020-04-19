package com.sdk.samples.topics.history

import android.content.Context
import android.util.Log
import androidx.room.*
import com.nanorep.nanoengine.chatelement.ChatElement
import com.nanorep.nanoengine.chatelement.StorableChatElement
import com.nanorep.sdkcore.model.StatementScope
import com.nanorep.sdkcore.model.StatementStatus
import com.nanorep.sdkcore.model.StatusPending
import com.nanorep.sdkcore.utils.SystemUtil
import java.util.*


/**
 * The Room database implementation for the History
 */

private const val HISTORY = "history_database"

@Database(entities = [HistoryElement::class], version = 1)
@TypeConverters(Converters::class)
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

        fun clearInstance() {
            instance = null
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

    @Query("SELECT * FROM historyElement ORDER BY inDate Limit :count OFFSET :from ")
    suspend fun getCount(from: Int, count: Int): List<HistoryElement>

    @Query("SELECT COUNT(*) FROM historyElement")
    suspend fun count(): Int

    @Query("SELECT * FROM historyElement")
    suspend fun getAll(): List<HistoryElement>

    @Query("DELETE FROM historyElement WHERE timestamp=:timestamp")
    suspend fun delete(timestamp: Long)

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(historyElement: HistoryElement)

    @Query("UPDATE historyElement SET `key`=:key, status=:status WHERE timestamp=:timestamp ")
    fun update(timestamp: Long, key: ByteArray?, status: Int)

}

/**
 * [StorableChatElement] implementing class
 * sample class for app usage
 */
@Entity
class HistoryElement(var key:ByteArray = byteArrayOf()) : StorableChatElement {

    /**
     * for internal use, to get the records in insertion order
     */
    lateinit var inDate: Date

    @PrimaryKey
    private var timestamp: Long = 0

    override var scope = StatementScope.UnknownScope

    @ChatElement.Companion.ChatElementType
    private var type: Int = 0

    @StatementStatus
    private var status = StatusPending

    override var isStorageReady = true

    var textContent: String = ""

    constructor(storable: StorableChatElement) :this(storable.getStorageKey()) {
        type = storable.getType()
        timestamp = storable.getTimestamp()
        status = storable.getStatus()
        scope = storable.scope
        key = storable.getStorageKey()
        isStorageReady = storable.isStorageReady
        textContent = storable.text
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

class Converters {

    @TypeConverter
    fun toScope (scope: Int): StatementScope? {

        return try {
            StatementScope.values()[scope]

        } catch(e: IllegalArgumentException){
            Log.e("ScopeTypeConverter", "Illegal scope value")
            null
        }
    }

    @TypeConverter
    fun toInt(status: StatementScope): Int {
        return status.ordinal
    }

    @TypeConverter
    fun toDate(dateLong: Long?): Date? {
        return dateLong?.let { Date(it) }
    }

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }
}
