package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.*

@Database(
    entities = [
        AiProviderEntity::class,
        WorkspaceEntity::class,
        ProjectEntity::class,
        ChatEntity::class,
        ChatMessageEntity::class,
        SkillEntity::class,
        PluginEntity::class,
        CustomAgentEntity::class,
        KnowledgeDocEntity::class,
        GithubRepoEntity::class,
        MemoryEntity::class,
        GeneratedImageEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun aiProviderDao(): AiProviderDao
    abstract fun workspaceDao(): WorkspaceDao
    abstract fun projectDao(): ProjectDao
    abstract fun chatDao(): ChatDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun skillDao(): SkillDao
    abstract fun pluginDao(): PluginDao
    abstract fun customAgentDao(): CustomAgentDao
    abstract fun knowledgeDocDao(): KnowledgeDocDao
    abstract fun githubRepoDao(): GithubRepoDao
    abstract fun memoryDao(): MemoryDao
    abstract fun generatedImageDao(): GeneratedImageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "claude_ai_workspace.db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
