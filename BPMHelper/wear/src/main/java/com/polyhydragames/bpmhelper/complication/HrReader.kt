package com.polyhydragames.bpmhelper.complication

import android.renderscript.Element
import kotlin.text.get

// Watch: HR reader using Health Services
class HrReader(private val context: Context) {
    private val exerciseClient = HealthServices.getClient(context).exerciseClient

    suspend fun startHrStreaming(onBpm: (Int) -> Unit) {
        // Check capabilities
        val capabilities = exerciseClient.getCapabilities()
        require(Element.DataType.HEART_RATE_BPM in capabilities.supportedDataTypes[ExerciseType.OTHER]
                ?: emptySet()) { "HR not supported" }

        // Start session
        exerciseClient.startExercise(
            StartExerciseRequest.builder()
                .setExerciseType(ExerciseType.OTHER)
                .addDataType(DataType.HEART_RATE_BPM)
                .build()
        )

        exerciseClient.exerciseUpdateFlow.collect { update ->
            update.latestMetrics[DataType.HEART_RATE_BPM]?.let { sampleSet ->
                sampleSet.samples.lastOrNull()?.value?.let { bpm ->
                    onBpm(bpm.roundToInt())
                }
            }
        }
    }

    suspend fun stop() {
        exerciseClient.endExercise()
    }
}