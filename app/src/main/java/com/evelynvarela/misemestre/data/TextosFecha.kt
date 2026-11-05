package com.evelynvarela.misemestre.data

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

fun textoFecha(fecha: LocalDate): String {
    val formato = DateTimeFormatter.ofPattern("d MMM", Locale.forLanguageTag("es"))
    return fecha.format(formato).replace(".", "")
}

fun textoDias(fecha: LocalDate): String {
    val dias = ChronoUnit.DAYS.between(LocalDate.now(), fecha)
    return when {
        dias < 0L -> "Vencida"
        dias == 0L -> "Hoy"
        dias == 1L -> "Mañana"
        else -> "$dias días"
    }
}

fun esUrgente(fecha: LocalDate): Boolean = ChronoUnit.DAYS.between(LocalDate.now(), fecha) <= 3L
