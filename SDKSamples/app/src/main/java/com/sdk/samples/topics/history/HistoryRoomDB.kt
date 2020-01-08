package com.sdk.samples.topics.history

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope

@Database(entities = [HistoryElement::class], version = 2, exportSchema = false)
abstract class HistoryRoomDB: RoomDatabase() {

    abstract fun historyDao(): HistoryDao

    companion object {

        @Volatile
        private var instance: HistoryRoomDB? = null

        fun getInstance(context: Context, scope: CoroutineScope): HistoryRoomDB {

            instance?.run {
                return this

            } ?: kotlin.run {

                synchronized(this) {

                    instance = Room.databaseBuilder(
                        context,
                        HistoryRoomDB::class.java,
                        "word_database"
                    ).build()

                    return instance!!
                }
            }
        }
    }
}