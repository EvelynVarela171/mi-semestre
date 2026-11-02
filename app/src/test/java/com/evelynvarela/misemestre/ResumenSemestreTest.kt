package com.evelynvarela.misemestre

import com.evelynvarela.misemestre.data.CalculadoraNotas
import com.evelynvarela.misemestre.data.Materia
import com.evelynvarela.misemestre.data.Nota
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ResumenSemestreTest {

    private val apps = Materia(id = "a", nombre = "Apps", creditos = 3)
    private val bases = Materia(id = "b", nombre = "Bases", creditos = 1)

    @Test
    fun ponderaPorCreditosYEvaluado() {
        val notas = listOf(
            Nota(materiaId = "a", nombre = "Foro", peso = 40, valor = 4.0),
            Nota(materiaId = "b", nombre = "Quiz", peso = 80, valor = 5.0)
        )

        val resumen = CalculadoraNotas.resumenSemestre(listOf(apps, bases), notas)

        assertEquals(4.25, resumen.promedio!!, 0.001)
        assertEquals(50, resumen.avance)
    }

    @Test
    fun sinNotasNoInventaPromedio() {
        val resumen = CalculadoraNotas.resumenSemestre(listOf(apps), emptyList())

        assertNull(resumen.promedio)
        assertEquals(0, resumen.avance)
    }
}
