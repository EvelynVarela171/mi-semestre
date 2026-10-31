package com.evelynvarela.misemestre.data

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

// Datos de mentira para ver las pantallas. Se van reemplazando por los reales.

data class PerfilEjemplo(
    val nombre: String,
    val iniciales: String,
    val correo: String,
    val universidad: String,
    val semestre: String
)

val perfilEjemplo = PerfilEjemplo("Evelyn", "EV", "estudiante@correo.com", "UCN", "2026-2")

data class NotaEjemplo(val nombre: String, val peso: Int, val valor: Double?)

val notasEjemplo = listOf(
    NotaEjemplo("Foro temático", 20, 4.6),
    NotaEjemplo("Cátedra académica", 20, 5.0),
    NotaEjemplo("Actividad 2", 20, null),
    NotaEjemplo("Taller ABP", 20, null),
    NotaEjemplo("Sustentación", 20, null)
)

// Lo que debe dar la calculadora para cada meta
val necesitasEjemplo = mapOf(3.0 to "1.8", 3.5 to "2.6", 4.0 to "3.5", 4.5 to "4.3")

data class EntregaEjemplo(
    val id: Int,
    val nombre: String,
    val materia: String,
    val fecha: LocalDate,
    val entregada: Boolean = false,
    val urgente: Boolean = false
)

val entregasEjemplo = listOf(
    EntregaEjemplo(0, "Taller ABP · GitHub", "Apps móviles", LocalDate.of(2026, 11, 16), urgente = true),
    EntregaEjemplo(1, "Video de sustentación", "Apps móviles", LocalDate.of(2026, 11, 16)),
    EntregaEjemplo(2, "Diapositivas del proyecto", "Apps móviles", LocalDate.of(2026, 11, 15)),
    EntregaEjemplo(3, "Infografía Actividad 2", "Apps móviles", LocalDate.of(2026, 10, 28), entregada = true)
)

fun textoFecha(fecha: LocalDate): String {
    val formato = DateTimeFormatter.ofPattern("d MMM", Locale.forLanguageTag("es"))
    return fecha.format(formato).replace(".", "")
}

fun textoDias(fecha: LocalDate): String {
    return when (val dias = ChronoUnit.DAYS.between(LocalDate.now(), fecha)) {
        0L -> "Hoy"
        1L -> "Mañana"
        else -> "$dias días"
    }
}
