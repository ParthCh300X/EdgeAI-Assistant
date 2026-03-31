package parth.appdev.edgeaiassistant.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import parth.appdev.edgeaiassistant.data.local.entity.AnalyticsEntity
import parth.appdev.edgeaiassistant.data.local.dao.AnalyticsDao
import parth.appdev.edgeaiassistant.data.local.dao.NoteDao
import parth.appdev.edgeaiassistant.data.local.entity.NoteEntity

@Database(
    entities = [NoteEntity::class, AnalyticsEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao
    abstract fun analyticsDao(): AnalyticsDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "edge_db"
                )
                    .fallbackToDestructiveMigration() // 🔥 CRITICAL FIX
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}