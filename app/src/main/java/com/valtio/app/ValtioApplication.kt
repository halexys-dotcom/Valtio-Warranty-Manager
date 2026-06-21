package com.valtio.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.valtio.app.worker.GarantiaAlertWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.valtio.app.data.LanguagePreferences
import com.valtio.app.util.LocaleManager
import kotlinx.coroutines.flow.first

@HiltAndroidApp
class ValtioApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        try {
            scheduleGarantiaAlerts()
        } catch (_: Exception) {
            // WorkManager might not be ready yet
        }

        // Apply persisted language preference on Main dispatcher
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val tag = LanguagePreferences.languageFlow(this@ValtioApplication).first()
                LocaleManager.applyLocale(this@ValtioApplication, tag)
            } catch (_: Exception) {
                // DataStore might not be ready yet; default locale will apply
            }
        }
    }
    private fun scheduleGarantiaAlerts() {
        val prefs = getSharedPreferences("valtio_prefs", MODE_PRIVATE)
        if (prefs.getBoolean("work_scheduled", false)) return
        prefs.edit().putBoolean("work_scheduled", true).apply()
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val alertWork = PeriodicWorkRequestBuilder<GarantiaAlertWorker>(
            24, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "garantia_alert_work",
            ExistingPeriodicWorkPolicy.KEEP,
            alertWork
        )
    }
}