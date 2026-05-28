package parth.appdev.edgeaiassistant.personalization

import parth.appdev.edgeaiassistant.data.repository.AnalyticsRepository
import parth.appdev.edgeaiassistant.domain.intent.IntentType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PersonalizationManager @Inject constructor(
    private val repository: AnalyticsRepository
) {
    suspend fun getTopIntents(): List<IntentType> = repository.getTopIntents()
}