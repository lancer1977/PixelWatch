/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.polyhydragames.pixelwatch.presentation

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.health.services.client.HealthServices
import androidx.health.services.client.PassiveListenerCallback
import androidx.health.services.client.data.DataPointContainer
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.PassiveListenerConfig
import androidx.lifecycle.lifecycleScope
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.tooling.preview.devices.WearDevices
import com.polyhydragames.pixelwatch.R
import com.polyhydragames.pixelwatch.presentation.theme.PixelPlaygroundTheme
import kotlinx.coroutines.launch
import android.util.Log
import androidx.health.services.client.ExerciseUpdateCallback
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.DataType.Companion.HEART_RATE_BPM
import androidx.health.services.client.data.ExerciseConfig
import androidx.health.services.client.data.ExerciseEndReason
import androidx.health.services.client.data.ExerciseLapSummary
import androidx.health.services.client.data.ExerciseType
import androidx.health.services.client.data.ExerciseUpdate
import androidx.health.services.client.startExercise


class MainActivity : ComponentActivity() {
    private val session = SimpleLiveHrSession()
    private val fakeBpm = 0;
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        Log.d("HeartRate", "start")
        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)
        // Start listening for HR
        lifecycleScope.launch {
            Log.i("HeartRate", "listening")
            startActiveHeartRate(this@MainActivity, session)

        }

        setContent {
            WearApp(session)
        }
    }

    @Composable
    fun HeartRateTile(session: SimpleLiveHrSession, modifier: Modifier = Modifier) {
        val bpm by session.bpm.collectAsState(null)
        Text(
            text = bpm?.let { "$it bpm" } ?: "?",
            modifier = modifier,
            style = MaterialTheme.typography.display2
        )
    }



    suspend fun startActiveHeartRate(context: Context, session: SimpleLiveHrSession) {
        val exerciseClient = HealthServices.getClient(context).exerciseClient

        // Register a callback for updates
        val callback = object : ExerciseUpdateCallback {
            override fun onAvailabilityChanged(
                dataType: DataType<*, *>,
                availability: Availability
            ) {
            }

            override fun onExerciseUpdateReceived(update: ExerciseUpdate) {
                // latestMetrics is a DataPointContainer
                val hrSamples = update.latestMetrics.getData(DataType.HEART_RATE_BPM)

                for (dp in hrSamples) {
                    val bpm = (dp.value as Double).toInt()
                    Log.d("HeartRate", "Active HR: $bpm bpm")
                    session.updateBpm(bpm)
                }
            }
            override fun onLapSummaryReceived(lapSummary: ExerciseLapSummary) {}
            override fun onRegistered() {             }

            override fun onRegistrationFailed(throwable: Throwable) {            }

        }

        // Register callback
        exerciseClient.setUpdateCallback(callback)
        // Start an "exercise" to stream heart rate
        val config = ExerciseConfig.builder(ExerciseType.WALKING) .setDataTypes(setOf(HEART_RATE_BPM)) .build()
        HealthServices.getClient(this@MainActivity).exerciseClient.startExercise(     config      )

    }

    @Composable
    fun WearApp(session: SimpleLiveHrSession) {
        PixelPlaygroundTheme {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background),
                contentAlignment = Alignment.Center
            ) {
                TimeText()
                HeartRateTile(session = session)
            }
        }
    }

    @Composable
    fun Greeting(greetingName: String) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.primary,
            text = stringResource(R.string.hello_world, greetingName)
        )
    }

    @Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
    @Composable
    fun DefaultPreview() {
        val session = SimpleLiveHrSession()
        session.updateBpm(200)
        WearApp(session)
    }
}