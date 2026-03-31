package parth.appdev.edgeaiassistant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "analytics")
data class AnalyticsEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val intentType: String,

    val inputText: String,

    val success: Boolean,

    val executionTime: Long,

    val timestamp: Long = System.currentTimeMillis()
)