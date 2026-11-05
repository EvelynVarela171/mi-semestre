package com.evelynvarela.misemestre

import com.evelynvarela.misemestre.data.AnimoLumi
import com.evelynvarela.misemestre.data.Entrega
import com.evelynvarela.misemestre.data.pesoDeEntrega
import com.evelynvarela.misemestre.data.planDelDia
import com.evelynvarela.misemestre.data.semestreDe
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class PlanDelDiaTest {

    private val hoy = LocalDate.of(2026, 11, 2)

    private fun entrega(nombre: String, dias: Long, entregada: Boolean = false) =
        Entrega(nombre = nombre, materia = "Apps", fecha = hoy.plusDays(dias), entregada = entregada)

    @Test
    fun sinPendientesVaAlDia() {
        val plan = planDelDia(hoy, listOf(entrega("Lista", 2, entregada = true)))

        assertEquals("Vas al día", plan.titulo)
        assertEquals(AnimoLumi.Contenta, plan.animo)
    }

    @Test
    fun lasVencidasVanPrimero() {
        val plan = planDelDia(hoy, listOf(entrega("Hoy", 0), entrega("Vieja", -2)))

        assertEquals("Tienes 1 entrega vencida", plan.titulo)
        assertEquals(AnimoLumi.Preocupada, plan.animo)
    }

    @Test
    fun avisaLoQueVenceHoy() {
        val plan = planDelDia(hoy, listOf(entrega("Quiz", 0), entrega("Taller", 0)))

        assertEquals("Hoy entregas Quiz", plan.titulo)
        assertEquals("Y 1 más para hoy", plan.detalle)
    }

    @Test
    fun priorizaLoQueMasValeEnLaSemana() {
        val plan = planDelDia(
            hoy,
            listOf(entrega("Foro (5%)", 2), entrega("Parcial (30%)", 6), entrega("Final (50%)", 20))
        )

        assertEquals("Hoy avanza en Parcial (30%)", plan.titulo)
        assertEquals("vence en 6 días y vale 30%", plan.detalle)
    }

    @Test
    fun sinPorcentajesEligeLoMasCercano() {
        val plan = planDelDia(hoy, listOf(entrega("Lejos", 30), entrega("Cerca", 2)))

        assertEquals("Hoy avanza en Cerca", plan.titulo)
        assertEquals(AnimoLumi.Atenta, plan.animo)
        assertTrue(plan.detalle.contains("vence en 2 días"))
    }

    @Test
    fun leeElPesoDelNombre() {
        assertEquals(20, pesoDeEntrega("Actividad 3 (Taller 3, 20%)"))
        assertEquals(0, pesoDeEntrega("Taller ABP"))
    }

    @Test
    fun calculaElSemestre() {
        assertEquals("2026-1", semestreDe(LocalDate.of(2026, 3, 1)))
        assertEquals("2026-2", semestreDe(hoy))
    }
}
