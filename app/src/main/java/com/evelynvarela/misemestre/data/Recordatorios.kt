package com.evelynvarela.misemestre.data

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.edit
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.evelynvarela.misemestre.MainActivity
import com.evelynvarela.misemestre.R
import java.io.IOException
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

private const val CANAL = "entregas"
private const val HORA_DE_AVISO = 8

object Recordatorios {
    fun programar(context: Context) {
        val ahora = LocalDateTime.now()
        val hoyALaHora = ahora.toLocalDate().atTime(HORA_DE_AVISO, 0)
        val proxima = if (ahora.isBefore(hoyALaHora)) hoyALaHora else hoyALaHora.plusDays(1)
        val diario = PeriodicWorkRequestBuilder<RecordatorioWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(Duration.between(ahora, proxima).toMinutes(), TimeUnit.MINUTES)
            .build()

        val trabajos = WorkManager.getInstance(context)
        trabajos.enqueueUniquePeriodicWork(
            "recordatorios",
            ExistingPeriodicWorkPolicy.KEEP,
            diario
        )
        if (ahora.hour >= HORA_DE_AVISO) {
            trabajos.enqueueUniqueWork(
                "recordatorios-al-abrir",
                ExistingWorkPolicy.KEEP,
                OneTimeWorkRequestBuilder<RecordatorioWorker>().build()
            )
        }
    }
}

class RecordatorioWorker(
    context: Context,
    parametros: WorkerParameters
) : CoroutineWorker(context, parametros) {

    override suspend fun doWork(): Result {
        val contexto = applicationContext
        val dao = AppDatabase.obtener(contexto).entregaDao()
        val repositorio = EntregaRepository(dao)

        try {
            CalendarioRepository(repositorio, EnlaceCalendario(contexto)).actualizar()
        } catch (_: IOException) {
        } catch (_: CalendarioInvalido) {
        }

        val preferencias = contexto.getSharedPreferences("recordatorios", Context.MODE_PRIVATE)
        val hoy = LocalDate.now()
        val notificaciones = NotificationManagerCompat.from(contexto)
        if (preferencias.getString("ultimoAviso", null) == hoy.toString() ||
            !notificaciones.areNotificationsEnabled()
        ) {
            return Result.success()
        }

        val pendientes = dao.pendientesEntre(hoy, hoy.plusDays(1))
        if (pendientes.isNotEmpty()) {
            crearCanal(contexto)
            pendientes.forEach { avisar(contexto, notificaciones, it, hoy) }
            preferencias.edit { putString("ultimoAviso", hoy.toString()) }
        }
        return Result.success()
    }

    private fun crearCanal(contexto: Context) {
        val canal = NotificationChannel(
            CANAL,
            "Recordatorios de entregas",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        contexto.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
    }

    @SuppressLint("MissingPermission")
    private fun avisar(
        contexto: Context,
        notificaciones: NotificationManagerCompat,
        entrega: Entrega,
        hoy: LocalDate
    ) {
        val abrirApp = PendingIntent.getActivity(
            contexto,
            0,
            Intent(contexto, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        val cuando = if (entrega.fecha == hoy) "Hoy" else "Mañana"
        val aviso = NotificationCompat.Builder(contexto, CANAL)
            .setSmallIcon(R.drawable.ic_campana)
            .setContentTitle("$cuando entregas: ${entrega.nombre}")
            .setContentText("${entrega.materia} · Lumi te lo recuerda")
            .setContentIntent(abrirApp)
            .setAutoCancel(true)
            .build()
        notificaciones.notify(entrega.id.hashCode(), aviso)
    }
}
