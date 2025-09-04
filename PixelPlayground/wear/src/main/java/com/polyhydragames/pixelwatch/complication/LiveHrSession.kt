package com.polyhydragames.pixelwatch.complication

import android.content.Context
import androidx.health.services.client.ExerciseUpdateCallback
import androidx.health.services.client.HealthServices
import androidx.health.services.client.clearUpdateCallback
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.DataType.Companion.HEART_RATE_BPM
import androidx.health.services.client.data.ExerciseConfig
import androidx.health.services.client.data.ExerciseLapSummary
import androidx.health.services.client.data.ExerciseType.Companion.ELLIPTICAL
import androidx.health.services.client.data.ExerciseUpdate
import androidx.health.services.client.data.SampleDataPoint
import androidx.health.services.client.endExercise
import androidx.health.services.client.startExercise
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.Executors
import kotlin.text.get
@OptIn(ExperimentalStdlibApi::class)
class LiveHrSession(private val context: Context) {
    private val client = HealthServices.getClient(context).exerciseClient

    private val _bpm = MutableStateFlow<Int?>(null)
    val bpm: StateFlow<Int?> = _bpm

    private val callback = object : ExerciseUpdateCallback {

        override fun onAvailabilityChanged(dataType: DataType<*, *>, availability: Availability) {}
        override fun onExerciseUpdateReceived(update: ExerciseUpdate) {
            val point = update.latestMetrics.sampleDataPoints.lastOrNull()
            if (point is SampleDataPoint<*>) {
                _bpm.value = point.value.toString().toInt();
            }
     }

        override fun onLapSummaryReceived(lapSummary: ExerciseLapSummary) {
            TODO("Not yet implemented")
        }

        override fun onRegistered() {
            TODO("Not yet implemented")
        }

        override fun onRegistrationFailed(throwable: Throwable) {
            TODO("Not yet implemented")
        }

    }

    suspend fun start() {
        // Request runtime permissions first (BODY_SENSORS [+ BACKGROUND if needed])
        var configBuilder =  ExerciseConfig.builder(ELLIPTICAL);
        configBuilder.setDataTypes(setOf(HEART_RATE_BPM))
        configBuilder.setIsAutoPauseAndResumeEnabled(false);
        var request = configBuilder.build();
        client.setUpdateCallback(Executors.newSingleThreadExecutor(), callback)
        client.startExercise(request)
    }

    suspend fun stop() {
        client.clearUpdateCallback(callback)
        client.endExercise()
    }
}
