package parth.appdev.edgeaiassistant.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import parth.appdev.edgeaiassistant.data.local.dao.AnalyticsDao
import parth.appdev.edgeaiassistant.data.local.dao.ChatDao
import parth.appdev.edgeaiassistant.data.local.dao.NoteDao
import parth.appdev.edgeaiassistant.data.local.entity.AnalyticsEntity
import parth.appdev.edgeaiassistant.data.local.entity.ChatEntity
import parth.appdev.edgeaiassistant.data.local.entity.NoteEntity

@Database(
    entities = [NoteEntity::class, AnalyticsEntity::class, ChatEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun analyticsDao(): AnalyticsDao
    abstract fun chatDao(): ChatDao
}