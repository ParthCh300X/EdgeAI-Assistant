package parth.appdev.edgeaiassistant.analytics

import parth.appdev.edgeaiassistant.data.repository.AnalyticsRepository
import parth.appdev.edgeaiassistant.domain.intent.IntentType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsManager @Inject constructor(
    private val repository: AnalyticsRepository
) {
    // Caller must call from a coroutine scope (viewModelScope)
    suspend fun log(
        intent: IntentType,
        input: String,
        success: Boolean,
        executionTime: Long
    ) {
        repository.log(intent, input, success, executionTime)
    }
}