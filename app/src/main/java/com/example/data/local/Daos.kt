package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AiProviderDao {
    @Query("SELECT * FROM ai_providers")
    fun getAllProviders(): Flow<List<AiProviderEntity>>

    @Query("SELECT * FROM ai_providers WHERE id = :id")
    suspend fun getProviderById(id: String): AiProviderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(provider: AiProviderEntity)

    @Delete
    suspend fun delete(provider: AiProviderEntity)
}

@Dao
interface WorkspaceDao {
    @Query("SELECT * FROM workspaces ORDER BY createdAt DESC")
    fun getAllWorkspaces(): Flow<List<WorkspaceEntity>>

    @Query("SELECT * FROM workspaces WHERE id = :id")
    suspend fun getWorkspaceById(id: String): WorkspaceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(workspace: WorkspaceEntity)

    @Query("DELETE FROM workspaces WHERE id = :id")
    suspend fun deleteWorkspaceById(id: String)
}

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects WHERE workspaceId = :workspaceId ORDER BY createdAt DESC")
    fun getProjectsForWorkspace(workspaceId: String): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects ORDER BY createdAt DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(project: ProjectEntity)

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteProjectById(id: String)
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chats WHERE workspaceId = :workspaceId AND isArchived = 0 ORDER BY isPinned DESC, updatedAt DESC")
    fun getChatsForWorkspace(workspaceId: String): Flow<List<ChatEntity>>

    @Query("SELECT * FROM chats WHERE isArchived = 0 ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllChats(): Flow<List<ChatEntity>>

    @Query("SELECT * FROM chats WHERE id = :id")
    suspend fun getChatById(id: String): ChatEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(chat: ChatEntity)

    @Query("DELETE FROM chats WHERE id = :id")
    suspend fun deleteChatById(id: String)
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages WHERE chatId = :chatId ORDER BY timestamp ASC")
    fun getMessagesForChat(chatId: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages WHERE id = :id")
    suspend fun deleteMessageById(id: String)

    @Query("DELETE FROM chat_messages WHERE chatId = :chatId")
    suspend fun clearMessagesForChat(chatId: String)
}

@Dao
interface SkillDao {
    @Query("SELECT * FROM skills")
    fun getAllSkills(): Flow<List<SkillEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(skill: SkillEntity)

    @Query("DELETE FROM skills WHERE id = :id")
    suspend fun deleteSkillById(id: String)
}

@Dao
interface PluginDao {
    @Query("SELECT * FROM plugins")
    fun getAllPlugins(): Flow<List<PluginEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(plugin: PluginEntity)
}

@Dao
interface CustomAgentDao {
    @Query("SELECT * FROM custom_agents")
    fun getAllAgents(): Flow<List<CustomAgentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(agent: CustomAgentEntity)

    @Query("DELETE FROM custom_agents WHERE id = :id")
    suspend fun deleteAgentById(id: String)
}

@Dao
interface KnowledgeDocDao {
    @Query("SELECT * FROM knowledge_docs WHERE workspaceId = :workspaceId ORDER BY uploadedAt DESC")
    fun getDocsForWorkspace(workspaceId: String): Flow<List<KnowledgeDocEntity>>

    @Query("SELECT * FROM knowledge_docs ORDER BY uploadedAt DESC")
    fun getAllDocs(): Flow<List<KnowledgeDocEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(doc: KnowledgeDocEntity)

    @Query("DELETE FROM knowledge_docs WHERE id = :id")
    suspend fun deleteDocById(id: String)
}

@Dao
interface GithubRepoDao {
    @Query("SELECT * FROM github_repos")
    fun getAllRepos(): Flow<List<GithubRepoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(repo: GithubRepoEntity)

    @Query("DELETE FROM github_repos WHERE id = :id")
    suspend fun deleteRepoById(id: String)
}

@Dao
interface MemoryDao {
    @Query("SELECT * FROM memories ORDER BY isPinned DESC, createdAt DESC")
    fun getAllMemories(): Flow<List<MemoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(memory: MemoryEntity)

    @Query("DELETE FROM memories WHERE id = :id")
    suspend fun deleteMemoryById(id: String)
}

@Dao
interface GeneratedImageDao {
    @Query("SELECT * FROM generated_images ORDER BY timestamp DESC")
    fun getAllImages(): Flow<List<GeneratedImageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(image: GeneratedImageEntity)

    @Query("DELETE FROM generated_images WHERE id = :id")
    suspend fun deleteImageById(id: String)
}
