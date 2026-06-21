package com.valtio.app.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.valtio.app.MainActivity
import com.valtio.app.R
import com.valtio.app.data.repository.ProdutoRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.temporal.ChronoUnit

val Context.dataStore by preferencesDataStore(name = "alert_settings")

@HiltWorker
class GarantiaAlertWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val produtoRepository: ProdutoRepository
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val CHANNEL_ID = "garantia_alerts"
    }

    override suspend fun doWork(): Result {
        createNotificationChannel()

        val dias90 = isAlertEnabled("alert_90") && getAlertDays("alert_90_days") == 90
        val dias30 = isAlertEnabled("alert_30") && getAlertDays("alert_30_days") == 30
        val dias7 = isAlertEnabled("alert_7") && getAlertDays("alert_7_days") == 7
        val ultimoDia = isAlertEnabled("alert_last_day")

        val diasList = mutableListOf<Int>()
        if (dias90) diasList.add(90)
        if (dias30) diasList.add(30)
        if (dias7) diasList.add(7)
        if (ultimoDia) diasList.add(0)

        if (diasList.isEmpty()) return Result.success()

        val hoje = LocalDate.now()

        for (diasRestantesAlvo in diasList) {
            val produtos = produtoRepository.getProdutosComGarantiaAExpirarEm(diasRestantesAlvo)
            for (produto in produtos) {
                val diasReais = produto.dataFimGarantia?.let { ChronoUnit.DAYS.between(hoje, it) } ?: continue
                if (diasReais in 0..diasRestantesAlvo) {
                    sendNotification(produto.nome, diasReais, produto.id)
                }
            }
        }

        return Result.success()
    }

    private suspend fun isAlertEnabled(key: String): Boolean {
        val dataStoreKey = booleanPreferencesKey(key)
        return applicationContext.dataStore.data.map { prefs -> prefs[dataStoreKey] ?: true }.first()
    }

    private suspend fun getAlertDays(key: String): Int {
        val dataStoreKey = intPreferencesKey(key)
        return applicationContext.dataStore.data.map { prefs ->
            prefs[dataStoreKey]
                ?: when { key.contains("90") -> 90; key.contains("30") -> 30; key.contains("7") -> 7; else -> 0 }
        }.first()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                applicationContext.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = applicationContext.getString(R.string.notification_channel_desc) }
            applicationContext.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun sendNotification(produtoNome: String, diasRestantes: Long, produtoId: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            if (!hasPermission) return
        }

        val ctx = applicationContext
        val titulo = when {
            diasRestantes <= 0 -> ctx.getString(R.string.notification_expires_today_title)
            diasRestantes <= 7 -> ctx.getString(R.string.notification_expires_soon_title)
            diasRestantes <= 30 -> ctx.getString(R.string.notification_expiring_title)
            else -> ctx.getString(R.string.notification_alert_title)
        }

        val mensagem = when {
            diasRestantes <= 0 -> ctx.getString(R.string.notification_expires_today_body, produtoNome)
            diasRestantes == 1L -> ctx.getString(R.string.notification_expires_tomorrow_body, produtoNome)
            else -> ctx.getString(R.string.notification_expires_in_days_body, produtoNome, diasRestantes)
        }

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("produto_id", produtoId)
        }
        val pendingIntent = PendingIntent.getActivity(applicationContext, produtoId.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(titulo)
            .setContentText(mensagem)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(1000 + produtoId.toInt(), notification)
    }
}