package com.evelynvarela.misemestre.data

import java.time.LocalDate
import java.time.temporal.ChronoUnit

fun contextoParaLumi(
    hoy: LocalDate,
    materias: List<Materia>,
    notas: List<Nota>,
    entregas: List<Entrega>
): String {
    val lineasMaterias = materias.map { materia ->
        val propias = notas.filter { it.materiaId == materia.id }
        val detalle = propias.joinToString { nota ->
            val valor = nota.valor?.let(CalculadoraNotas::formato) ?: "sin calificar"
            "${nota.nombre} ${nota.peso}%: $valor"
        }
        val promedio = CalculadoraNotas.promedioEvaluado(propias)
        val avance = if (promedio == null) {
            "sin notas calificadas"
        } else {
            val evaluado = CalculadoraNotas.pesoEvaluado(propias)
            "lleva ${CalculadoraNotas.formato(promedio)} en el $evaluado% evaluado"
        }
        "- ${materia.nombre} (${materia.creditos} créditos): $avance. $detalle"
    }

    val lineasEntregas = entregas
        .filterNot { it.entregada }
        .sortedBy { it.fecha }
        .map { entrega ->
            val dias = ChronoUnit.DAYS.between(hoy, entrega.fecha)
            val plazo = if (dias < 0) "venció hace ${-dias} días" else "faltan $dias días"
            "- ${entrega.nombre} de ${entrega.materia}: ${entrega.fecha}, $plazo"
        }

    return buildString {
        appendLine("Hoy es $hoy.")
        appendLine("Materias y notas (escala de 0 a 5):")
        appendLine(lineasMaterias.joinToString("\n").ifEmpty { "- Aún no hay materias." })
        appendLine("Entregas pendientes:")
        append(lineasEntregas.joinToString("\n").ifEmpty { "- No hay entregas pendientes." })
    }
}
