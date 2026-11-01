package com.evelynvarela.misemestre

import com.evelynvarela.misemestre.data.CalculadoraNotas
import com.evelynvarela.misemestre.data.Nota
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculadoraNotasTest {

    private val parcial = Nota(materiaId = 1, nombre = "Parcial", peso = 40, valor = 4.8)
    private val final = Nota(materiaId = 1, nombre = "Final", peso = 60)

    @Test
    fun calculaElPromedioDeLoEvaluado() {
        val notas = listOf(parcial, final)
        assertEquals(40, CalculadoraNotas.pesoEvaluado(notas))
        assertEquals(1.92, CalculadoraNotas.acumulado(notas), 0.001)
        assertEquals(4.8, CalculadoraNotas.promedioEvaluado(notas)!!, 0.001)
    }

    @Test
    fun sinNotasCalificadasNoHayPromedio() {
        assertNull(CalculadoraNotas.promedioEvaluado(listOf(final)))
    }

    @Test
    fun calculaLaNotaNecesaria() {
        val notas = listOf(parcial, final)
        assertEquals(3.47, CalculadoraNotas.notaNecesaria(notas, 4.0)!!, 0.01)
    }

    @Test
    fun laMetaYaAseguradaDaCeroOMenos() {
        val notas = listOf(Nota(materiaId = 1, nombre = "Parcial", peso = 80, valor = 5.0), final)
        assertTrue(CalculadoraNotas.notaNecesaria(notas, 3.0)!! <= 0)
    }

    @Test
    fun sinPorcentajeRestanteDevuelveNull() {
        val notas = listOf(parcial, final.copy(valor = 4.0))
        assertNull(CalculadoraNotas.notaNecesaria(notas, 4.0))
    }

    @Test
    fun laNotaNecesariaSeRedondeaHaciaArriba() {
        assertEquals("2.7", CalculadoraNotas.formatoNecesaria(2.633))
        assertEquals("1.8", CalculadoraNotas.formatoNecesaria(1.8))
    }
}
