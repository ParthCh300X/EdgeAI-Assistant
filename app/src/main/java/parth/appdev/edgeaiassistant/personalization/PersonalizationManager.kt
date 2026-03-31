package parth.appdev.edgeaiassistant.personalization

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import parth.appdev.edgeaiassistant.data.local.AppDatabase
import parth.appdev.edgeaiassistant.domain.intent.IntentType

class PersonalizationManager(
    context: Context
) {

    private val dao = AppDatabase.getInstance(context).analyticsDao()

    suspend fun getTopIntents(): List<IntentType> = withContext(Dispatchers.IO) {

        val stats = dao.getUsageStats()

        stats
            .take(3) // top 3 most used
            .mapNotNull {
                try {
                    IntentType.valueOf(it.intentType)
                } catch (e: Exception) {
                    null
                }
            }
    }
}