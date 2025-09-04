package com.polyhydragames.pixelwatch.complication

import android.content.Context
import androidx.health.services.client.HealthServices
import androidx.health.services.client.data.ExerciseType
import androidx.health.services.client.getCapabilities

// HrReader.kt (wear)
class HrReader(private val context: Context) {
    private val healthClient = HealthServices.getClient(context)
    suspend fun isHrAvailable(): Boolean {
        val caps = healthClient.exerciseClient.getCapabilities()
        return ExerciseType.ELLIPTICAL in caps.supportedExerciseTypes
    }
}


