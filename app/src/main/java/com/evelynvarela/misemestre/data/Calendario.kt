package com.evelynvarela.misemestre.data

import java.net.URI
import java.security.MessageDigest
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

private const val DIAS_A_IMPORTAR = 180L
private const val MATERIA_POR_DEFECTO = "Calendario"
private val MATERIA_EN_TITULO = Regex("""^(.*?)\s*\[([^\]]+)]\s*$""")
private val ESCAPADO = Regex("""\\(.)""")
private val ZONA_HORARIA = Regex("""TZID=([^;]+)""")
private val FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")

class CalendarioInvalido : Exception()

data class EventoCalendario(
    val uid: String,
    val nombre: String,
    val materia: String,
    val fecha: LocalDate
)

fun normalizarEnlace(texto: String): String? {
    val limpio = texto.trim().replaceFirst(Regex("^webcal://", RegexOption.IGNORE_CASE), "https://")
    val uri = runCatching { URI(limpio) }.getOrNull() ?: return null
    if (!uri.scheme.equals("https", ignoreCase = true) || uri.host.isNullOrBlank()) return null
    return limpio
}

fun leerCalendario(texto: String, zona: ZoneId): List<EventoCalendario> {
    if (!texto.contains("BEGIN:VCALENDAR")) throw CalendarioInvalido()

    val lineas = texto.replace("\r\n", "\n").replace("\n ", "").replace("\n\t", "").lines()
    val eventos = mutableListOf<EventoCalendario>()
    var campos: MutableMap<String, Pair<String, String>>? = null

    for (linea in lineas) {
        when (linea.trim()) {
            "BEGIN:VEVENT" -> campos = mutableMapOf()
            "END:VEVENT" -> {
                campos?.let { eventoDe(it, zona) }?.let(eventos::add)
                campos = null
            }
            else -> {
                val clave = linea.substringBefore(':', "")
                if (clave.isNotEmpty()) {
                    campos?.put(
                        clave.substringBefore(';'),
                        clave.substringAfter(';', "") to linea.substringAfter(':').trim()
                    )
                }
            }
        }
    }
    return eventos
}

private fun eventoDe(campos: Map<String, Pair<String, String>>, zona: ZoneId): EventoCalendario? {
    if (campos["STATUS"]?.second.equals("CANCELLED", ignoreCase = true)) return null
    val uid = campos["UID"]?.second?.takeIf { it.isNotBlank() } ?: return null
    val titulo = campos["SUMMARY"]?.second?.let(::desescapar)?.takeIf { it.isNotBlank() } ?: return null
    val (parametros, valor) = campos["DTSTART"] ?: return null
    val fecha = fechaDe(parametros, valor, zona) ?: return null

    val conMateria = MATERIA_EN_TITULO.find(titulo)
    return EventoCalendario(
        uid = uid,
        nombre = conMateria?.groupValues?.get(1)?.ifBlank { titulo } ?: titulo,
        materia = conMateria?.groupValues?.get(2)?.let(::nombreDeMateria) ?: MATERIA_POR_DEFECTO,
        fecha = fecha
    )
}

private fun nombreDeMateria(texto: String): String {
    val nombre = texto.substringBefore('_').trim().ifEmpty { texto }
    if (' ' !in nombre || nombre != nombre.uppercase()) return nombre
    return nombre.lowercase().replaceFirstChar { it.uppercase() }
}

private fun desescapar(texto: String): String =
    ESCAPADO.replace(texto) { coincidencia ->
        val letra = coincidencia.groupValues[1]
        if (letra.equals("n", ignoreCase = true)) " " else letra
    }

private fun fechaDe(parametros: String, valor: String, zona: ZoneId): LocalDate? = runCatching {
    val zonaEvento = ZONA_HORARIA.find(parametros)?.groupValues?.get(1)?.trim('"')
    when {
        valor.length == 8 -> LocalDate.parse(valor, DateTimeFormatter.BASIC_ISO_DATE)
        valor.endsWith("Z") -> LocalDateTime.parse(valor.dropLast(1), FORMATO_FECHA_HORA)
            .atZone(ZoneOffset.UTC)
            .withZoneSameInstant(zona)
            .toLocalDate()
        zonaEvento != null -> LocalDateTime.parse(valor, FORMATO_FECHA_HORA)
            .atZone(runCatching { ZoneId.of(zonaEvento) }.getOrDefault(zona))
            .withZoneSameInstant(zona)
            .toLocalDate()
        else -> LocalDateTime.parse(valor, FORMATO_FECHA_HORA).toLocalDate()
    }
}.getOrNull()

fun idDeEvento(uid: String): String {
    val resumen = MessageDigest.getInstance("SHA-256").digest(uid.toByteArray())
    return "cal-" + resumen.take(16).joinToString("") { "%02x".format(it) }
}

fun materiasSinCrear(
    entregas: List<Entrega>,
    materias: List<Materia>,
    ignoradas: Set<String>
): List<String> {
    val existentes = materias.map { it.nombre.lowercase() }.toSet()
    return entregas
        .filter { it.importada && it.materia != MATERIA_POR_DEFECTO }
        .map { it.materia }
        .distinct()
        .filterNot { it.lowercase() in existentes || it in ignoradas }
}

fun cambiosDeCalendario(
    existentes: List<Entrega>,
    eventos: List<EventoCalendario>,
    hoy: LocalDate
): List<Entrega> {
    val porId = existentes.associateBy { it.id }
    val limite = hoy.plusDays(DIAS_A_IMPORTAR)

    return eventos.mapNotNull { evento ->
        val id = idDeEvento(evento.uid)
        val actual = porId[id]
        when {
            actual?.editada == true -> null
            actual != null -> actual
                .copy(nombre = evento.nombre, materia = evento.materia, fecha = evento.fecha)
                .takeIf { it != actual }
            evento.fecha.isBefore(hoy) || evento.fecha.isAfter(limite) -> null
            else -> Entrega(
                id = id,
                nombre = evento.nombre,
                materia = evento.materia,
                fecha = evento.fecha,
                importada = true
            )
        }
    }.distinctBy { it.id }
}
