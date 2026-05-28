package parth.appdev.edgeaiassistant.data.repository

import parth.appdev.edgeaiassistant.data.local.dao.AnalyticsDao
import parth.appdev.edgeaiassistant.data.local.entity.AnalyticsEntity
import parth.appdev.edgeaiassistant.data.local.model.DailyStat
import parth.appdev.edgeaiassistant.data.local.model.UsageStat
import parth.appdev.edgeaiassistant.domain.intent.IntentType
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class AnalyticsRepository @Inject constructor(
    private val dao: AnalyticsDao
) {
    suspend fun log(
        intent        : IntentType,
        input         : String,
        success       : Boolean,
        executionTime : Long
    ) {
        dao.insert(
            AnalyticsEntity(
                intentType    = intent.name,
                inputText     = input,
                success       = success,
                executionTime = executionTime
            )
        )
    }

    suspend fun getTotalCount()           = dao.getTotalCount()
    suspend fun getAverageExecutionTime() = dao.getAverageExecutionTime() ?: 0f
    suspend fun getUsageStats()           = dao.getUsageStats()
    suspend fun deleteAll()               = dao.deleteAll()

    suspend fun getDailyStats(): List<DailyStat> {
        val sevenDaysAgo = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000)
        val timestamps   = dao.getTimestampsAfter(sevenDaysAgo)

        val sdf = SimpleDateFormat("MM/dd", Locale.getDefault())

        // Group timestamps by day label
        val grouped = mutableMapOf<String, Int>()
        timestamps.forEach { ts ->
            val day = sdf.format(Date(ts))
            grouped[day] = (grouped[day] ?: 0) + 1
        }

        // Fill in missing days with 0 so the chart always shows 7 points
        val result = mutableListOf<DailyStat>()
        val cal    = Calendar.getInstance()
        repeat(7) { i ->
            cal.timeInMillis = System.currentTimeMillis() - ((6 - i).toLong() * 24 * 60 * 60 * 1000)
            val day = sdf.format(cal.time)
            result.add(DailyStat(day = day, count = grouped[day] ?: 0))
        }
        return result
    }

    suspend fun getTopIntents(limit: Int = 3): List<IntentType> {
        return dao.getUsageStats()
            .take(limit)
            .mapNotNull {
                try { IntentType.valueOf(it.intentType) }
                catch (e: Exception) { null }
            }
    }
}