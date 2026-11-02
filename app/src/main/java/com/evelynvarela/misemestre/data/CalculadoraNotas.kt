package com.evelynvarela.misemestre.data

import java.util.Locale
import kotlin.math.ceil

data class ResumenSemestre(val promedio: Double?, val avance: Int)

object CalculadoraNotas {

    fun resumenSemestre(materias: List<Materia>, notas: List<Nota>): ResumenSemestre {
        val creditos = materias.sumOf { it.creditos }
        if (creditos == 0) return ResumenSemestre(null, 0)

        val porMateria = materias.map { materia ->
            materia to notas.filter { it.materiaId == materia.id }
        }
        val avance = porMateria.sumOf { (materia, propias) ->
            pesoEvaluado(propias).coerceAtMost(100) * materia.creditos
        } / creditos
        val evaluadas = porMateria.mapNotNull { (materia, propias) ->
            promedioEvaluado(propias)?.let { it to materia.creditos }
        }
        val creditosEvaluados = evaluadas.sumOf { it.second }
        val promedio = if (creditosEvaluados == 0) {
            null
        } else {
            evaluadas.sumOf { (nota, peso) -> nota * peso } / creditosEvaluados
        }
        return ResumenSemestre(promedio, avance)
    }

    fun pesoEvaluado(notas: List<Nota>): Int =
        notas.filter { it.valor != null }.sumOf { it.peso }

    fun acumulado(notas: List<Nota>): Double =
        notas.sumOf { (it.valor ?: 0.0) * it.peso / 100 }

    fun promedioEvaluado(notas: List<Nota>): Double? {
        val evaluado = pesoEvaluado(notas)
        if (evaluado == 0) return null
        return acumulado(notas) / (evaluado / 100.0)
    }

    fun notaNecesaria(notas: List<Nota>, meta: Double): Double? {
        val restante = 100 - pesoEvaluado(notas)
        if (restante <= 0) return null
        return (meta - acumulado(notas)) / (restante / 100.0)
    }

    fun formato(nota: Double): String = String.format(Locale.US, "%.1f", nota)

    fun formatoNecesaria(nota: Double): String = formato(ceil((nota - 0.000001) * 10) / 10)
}
