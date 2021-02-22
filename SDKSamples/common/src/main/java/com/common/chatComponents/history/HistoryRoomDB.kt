package com.common.chatComponents.history

import android.content.Context
import android.util.Log
import androidx.annotation.NonNull
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nanorep.convesationui.structure.elements.ChatElement
import com.nanorep.convesationui.structure.elements.StorableChatElement
import com.nanorep.sdkcore.model.StatementScope
import com.nanorep.sdkcore.model.StatementStatus
import com.nanorep.sdkcore.model.StatusPending
import java.util.Date


/**
 * The Room database implementation for the History
 */

private const val HISTORY = "history_database"

@Database(entities = [HistoryElement::class], version = 4)
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
            val MIGRATION_3_4 = object : Migration(3, 4) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL(
                        """Create TABLE new_HistoryElement (
                        id TEXT PRIMARY KEY NOT NULL DEFAULT '',
                        groupId TEXT NOT NULL,
                        scope INTEGER NOT NULL,
                        inDate INTEGER NOT NULL,
                        textContent TEXT NOT NULL,
                        type INTEGER NOT NULL,
                        isStorageReady INTEGER NOT NULL,
                        key BLOB NOT NULL,
                        timestamp INTEGER NOT NULL,
                        status INTEGER NOT NULL DEFAULT -1);"""
                    )
                    database.execSQL(
                        """INSERT INTO new_HistoryElement (id, groupId,
                        scope,inDate,textContent,type,isStorageReady,key,timestamp, status) 
                        SELECT timestamp, groupId,
                        scope,inDate,textContent,type,isStorageReady, key,timestamp,status FROM HistoryElement;"""
                    )
                    database.execSQL("DROP TABLE HistoryElement;")
                    database.execSQL("ALTER TABLE new_HistoryElement RENAME TO HistoryElement;")
                }
            }

            return Room.databaseBuilder(
                context,
                HistoryRoomDB::class.java,
                HISTORY
            ).addMigrations(MIGRATION_3_4).build()
        }
    }
}

@Dao
interface HistoryDao {

    @Query("SELECT * FROM historyElement Limit :count OFFSET :from ")
    suspend fun getCount(from: Int, count: Int): List<HistoryElement>

    @Query("SELECT * FROM historyElement WHERE groupId=:groupId ORDER BY inDate Limit :count OFFSET :from ")
    suspend fun getCount(groupId: String, from: Int, count: Int): List<HistoryElement>

    @Query("SELECT COUNT(*) FROM historyElement WHERE groupId=:groupId")
    suspend fun count(groupId: String): Int

    @Query("SELECT COUNT(*) FROM historyElement")
    suspend fun count(): Int

    @Query("SELECT * FROM historyElement WHERE groupId=:groupId ORDER BY inDate")
    suspend fun getAll(groupId: String): List<HistoryElement>

    @Query("DELETE FROM historyElement WHERE groupId=:groupId and id=:id")
    suspend fun delete(groupId: String, id: String)

    @Query("DELETE FROM historyElement WHERE groupId=:groupId and timestamp=:timestamp")
    suspend fun delete(groupId: String, timestamp: Long)

    @Query("DELETE FROM historyElement WHERE groupId=:groupId")
    suspend fun delete(groupId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(historyElement: HistoryElement?)

    @Query("UPDATE historyElement SET `key`=:key, `status`=:status, `timestamp`=:timestamp WHERE groupId=:groupId and id=:id")
    suspend fun update(groupId: String, id: String, timestamp: Long, key: ByteArray, status: Int)

    @Query("UPDATE historyElement SET `key`=:key, status=:status WHERE groupId=:groupId and timestamp=:timestamp ")
    suspend fun update(groupId: String, timestamp: Long, key: ByteArray, status: Int)

}

/**
 * [StorableChatElement] implementing class
 * sample class for app usage
 */
@Entity
class HistoryElement() : StorableChatElement {

    var groupId: String = ""

    @PrimaryKey
    @NonNull
    private var id: String = ""

    var key:ByteArray = byteArrayOf()

    /**
     * for internal use, to get the records in insertion order
     */
    lateinit var inDate: Date

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
        id = storable.getId()
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

    override fun getId(): String {
        return id
    }

    fun setId(id: String){
        this.id = id
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
    fun toScope(scope: Int): StatementScope? {

        return try {
            StatementScope.values()[scope]

        } catch (e: IllegalArgumentException){
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
