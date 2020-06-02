package com.sdk.samples.topics.history

import android.content.Context
import android.util.Log
import androidx.room.*
import com.nanorep.nanoengine.chatelement.ChatElement
import com.nanorep.nanoengine.chatelement.StorableChatElement
import com.nanorep.sdkcore.model.StatementScope
import com.nanorep.sdkcore.model.StatementStatus
import com.nanorep.sdkcore.model.StatusPending
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

    @Query("SELECT * FROM historyElement WHERE groupId=:groupId ORDER BY inDate Limit :count OFFSET :from ")
    suspend fun getCount(groupId: String, from: Int, count: Int): List<HistoryElement>

    @Query("SELECT COUNT(*) FROM historyElement WHERE groupId=:groupId")
    suspend fun count(groupId: String): Int

    @Query("SELECT * FROM historyElement WHERE groupId=:groupId ORDER BY inDate")
    suspend fun getAll(groupId: String): List<HistoryElement>

    @Query("DELETE FROM historyElement WHERE groupId=:groupId and timestamp=:timestamp")
    suspend fun delete(groupId: String, timestamp: Long)

    @Query("DELETE FROM historyElement WHERE groupId=:groupId")
    suspend fun delete(groupId: String)

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(historyElement: HistoryElement)

    @Query("UPDATE historyElement SET `key`=:key, status=:status WHERE groupId=:groupId and timestamp=:timestamp ")
    suspend fun update(groupId: String, timestamp: Long, key: ByteArray?, status: Int)

}

/**
 * [StorableChatElement] implementing class
 * sample class for app usage
 */
@Entity
class HistoryElement() : StorableChatElement {

    var groupId: String = ""

    var key:ByteArray = byteArrayOf()

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

    constructor(groupId: String, storable: StorableChatElement) : this() {
        this.groupId = groupId
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
