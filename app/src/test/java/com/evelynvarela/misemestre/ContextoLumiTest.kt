package com.evelynvarela.misemestre

import com.evelynvarela.misemestre.data.Entrega
import com.evelynvarela.misemestre.data.Materia
import com.evelynvarela.misemestre.data.Nota
import com.evelynvarela.misemestre.data.contextoParaLumi
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class ContextoLumiTest {

    private val hoy = LocalDate.of(2026, 11, 2)
    private val materia = Materia(id = "m1", nombre = "Apps móviles", creditos = 3)

    @Test
    fun incluyeElAvanceDeCadaMateria() {
        val notas = listOf(
            Nota(materiaId = "m1", nombre = "Foro", peso = 40, valor = 4.8),
            Nota(materiaId = "m1", nombre = "Taller", peso = 60)
        )
        val contexto = contextoParaLumi(hoy, listOf(materia), notas, emptyList())

        assertTrue(contexto.contains("lleva 4.8 en el 40% evaluado"))
        assertTrue(contexto.contains("Foro 40%: 4.8"))
        assertTrue(contexto.contains("Taller 60%: sin calificar"))
    }

    @Test
    fun soloMuestraEntregasPendientesConSusDias() {
        val entregas = listOf(
            Entrega(nombre = "Taller ABP", materia = "Apps móviles", fecha = LocalDate.of(2026, 11, 16)),
            Entrega(nombre = "Infografía", materia = "Apps móviles", fecha = LocalDate.of(2026, 10, 28), entregada = true)
        )
        val contexto = contextoParaLumi(hoy, emptyList(), emptyList(), entregas)

        assertTrue(contexto.contains("Taller ABP de Apps móviles: 2026-11-16, faltan 14 días"))
        assertFalse(contexto.contains("Infografía"))
    }

    @Test
    fun avisaCuandoUnaEntregaYaVencio() {
        val entregas = listOf(
            Entrega(nombre = "Quiz", materia = "Apps móviles", fecha = LocalDate.of(2026, 10, 30))
        )
        val contexto = contextoParaLumi(hoy, emptyList(), emptyList(), entregas)

        assertTrue(contexto.contains("venció hace 3 días"))
    }

    @Test
    fun avisaCuandoNoHayDatos() {
        val contexto = contextoParaLumi(hoy, emptyList(), emptyList(), emptyList())

        assertTrue(contexto.contains("Aún no hay materias."))
        assertTrue(contexto.contains("No hay entregas pendientes."))
    }
}
