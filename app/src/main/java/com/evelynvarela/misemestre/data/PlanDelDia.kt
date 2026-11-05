package com.evelynvarela.misemestre.data

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import com.google.firebase.ai.type.FirebaseAIException
import java.io.IOException
import java.time.LocalDate
import java.time.temporal.ChronoUnit

private const val DIAS_DE_PRIORIDAD = 7L
private const val DIAS_DE_ALERTA = 3L
private val PORCENTAJE = Regex("""(\d{1,3})\s*%""")

enum class AnimoLumi { Contenta, Tranquila, Atenta, Preocupada }

data class PlanDelDia(
    val titulo: String,
    val detalle: String,
    val animo: AnimoLumi,
    val entrega: Entrega? = null
)

fun pesoDeEntrega(nombre: String): Int =
    PORCENTAJE.find(nombre)?.groupValues?.get(1)?.toInt() ?: 0

fun planDelDia(hoy: LocalDate, entregas: List<Entrega>): PlanDelDia {
    val pendientes = entregas.filterNot { it.entregada }.sortedBy { it.fecha }
    val vencidas = pendientes.filter { it.fecha.isBefore(hoy) }
    val deHoy = pendientes.filter { it.fecha == hoy }
    val siguientes = pendientes.filter { it.fecha.isAfter(hoy) }

    return when {
        vencidas.isNotEmpty() -> PlanDelDia(
            titulo = if (vencidas.size == 1) "Tienes 1 entrega vencida" else "Tienes ${vencidas.size} entregas vencidas",
            detalle = "Revisa ${vencidas.first().nombre}",
            animo = AnimoLumi.Preocupada,
            entrega = vencidas.first()
        )
        deHoy.isNotEmpty() -> PlanDelDia(
            titulo = "Hoy entregas ${deHoy.first().nombre}",
            detalle = if (deHoy.size == 1) deHoy.first().materia else "Y ${deHoy.size - 1} más para hoy",
            animo = AnimoLumi.Preocupada,
            entrega = deHoy.first()
        )
        siguientes.isEmpty() -> PlanDelDia(
            titulo = "Vas al día",
            detalle = "No tienes entregas pendientes",
            animo = AnimoLumi.Contenta
        )
        else -> planDeAvance(hoy, siguientes)
    }
}

private fun planDeAvance(hoy: LocalDate, siguientes: List<Entrega>): PlanDelDia {
    val cercanas = siguientes.filter { ChronoUnit.DAYS.between(hoy, it.fecha) <= DIAS_DE_PRIORIDAD }
    val prioridad = cercanas.maxByOrNull { pesoDeEntrega(it.nombre) }
        ?.takeIf { pesoDeEntrega(it.nombre) > 0 }
        ?: siguientes.first()
    val dias = ChronoUnit.DAYS.between(hoy, prioridad.fecha)
    val plazo = if (dias == 1L) "vence mañana" else "vence en $dias días"
    val peso = pesoDeEntrega(prioridad.nombre)

    return PlanDelDia(
        titulo = "Hoy avanza en ${prioridad.nombre}",
        detalle = if (peso > 0) "$plazo y vale $peso%" else "${prioridad.materia}: $plazo",
        animo = if (dias <= DIAS_DE_ALERTA) AnimoLumi.Atenta else AnimoLumi.Tranquila,
        entrega = prioridad
    )
}

class FraseDelPlan(
    context: Context,
    private val lumi: LumiRepository = LumiRepository()
) {
    private val preferencias = context.getSharedPreferences("plan", Context.MODE_PRIVATE)

    suspend fun obtener(hoy: LocalDate, plan: PlanDelDia): String? {
        val entrega = plan.entrega ?: return null
        val clave = "$hoy|${entrega.id}"
        if (preferencias.getString("clave", null) == clave) {
            return preferencias.getString("frase", null)
        }

        val frase = try {
            lumi.fraseDelDia("${entrega.nombre} de ${entrega.materia}")
        } catch (e: FirebaseAIException) {
            Log.e("Lumi", "No pude crear la frase del plan", e)
            null
        } catch (e: IOException) {
            Log.e("Lumi", "No pude crear la frase del plan", e)
            null
        } ?: return null

        preferencias.edit {
            putString("clave", clave)
            putString("frase", frase)
        }
        return frase
    }
}
