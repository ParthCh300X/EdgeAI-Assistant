package parth.appdev.edgeaiassistant.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import parth.appdev.edgeaiassistant.data.local.AppDatabase
import parth.appdev.edgeaiassistant.data.local.dao.AnalyticsDao
import parth.appdev.edgeaiassistant.data.local.dao.ChatDao
import parth.appdev.edgeaiassistant.data.local.dao.NoteDao
import parth.appdev.edgeaiassistant.data.preferences.UserPreferences
import parth.appdev.edgeaiassistant.data.repository.AnalyticsRepository
import parth.appdev.edgeaiassistant.data.repository.ChatRepository
import parth.appdev.edgeaiassistant.data.repository.NoteRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "edge_db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideNoteDao(db: AppDatabase): NoteDao = db.noteDao()

    @Provides
    fun provideAnalyticsDao(db: AppDatabase): AnalyticsDao = db.analyticsDao()

    @Provides
    @Singleton
    fun provideNoteRepository(dao: NoteDao): NoteRepository = NoteRepository(dao)

    @Provides
    @Singleton
    fun provideAnalyticsRepository(dao: AnalyticsDao): AnalyticsRepository =
        AnalyticsRepository(dao)

    // Context for CommandDispatcher (system commands need it)
    @Provides
    fun provideContext(@ApplicationContext context: Context): Context = context

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences =
        UserPreferences(context)

    @Provides
    fun provideChatDao(db: AppDatabase): ChatDao = db.chatDao()

    @Provides
    @Singleton
    fun provideChatRepository(dao: ChatDao): ChatRepository = ChatRepository(dao)
}