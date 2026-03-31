package parth.appdev.edgeaiassistant.analytics

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import parth.appdev.edgeaiassistant.data.local.AppDatabase
import parth.appdev.edgeaiassistant.data.local.entity.AnalyticsEntity
import parth.appdev.edgeaiassistant.domain.intent.IntentType

class AnalyticsManager(
    context: Context
) {

    private val dao = AppDatabase.getInstance(context).analyticsDao()

    fun log(
        intent: IntentType,
        input: String,
        success: Boolean,
        executionTime: Long
    ) {

        CoroutineScope(Dispatchers.IO).launch {

            dao.insert(
                AnalyticsEntity(
                    intentType = intent.name,
                    inputText = input,
                    success = success,
                    executionTime = executionTime
                )
            )
        }
    }
}