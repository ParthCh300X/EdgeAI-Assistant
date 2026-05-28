package parth.appdev.edgeaiassistant.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import parth.appdev.edgeaiassistant.data.local.entity.AnalyticsEntity
import parth.appdev.edgeaiassistant.data.local.model.UsageStat

@Dao
interface AnalyticsDao {

    @Insert
    suspend fun insert(event: AnalyticsEntity)

    @Query("SELECT COUNT(*) FROM analytics")
    suspend fun getTotalCount(): Int

    @Query("SELECT intentType, COUNT(*) as count FROM analytics GROUP BY intentType ORDER BY count DESC")
    suspend fun getUsageStats(): List<UsageStat>

    @Query("SELECT AVG(executionTime) FROM analytics")
    suspend fun getAverageExecutionTime(): Float?

    @Query("DELETE FROM analytics")
    suspend fun deleteAll()

    // Return raw timestamps for last 7 days — we'll process in Kotlin
    @Query("SELECT timestamp FROM analytics WHERE timestamp >= :since ORDER BY timestamp ASC")
    suspend fun getTimestampsAfter(since: Long): List<Long>
}