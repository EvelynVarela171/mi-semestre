package com.evelynvarela.misemestre

import com.evelynvarela.misemestre.data.CalendarioInvalido
import com.evelynvarela.misemestre.data.Entrega
import com.evelynvarela.misemestre.data.EventoCalendario
import com.evelynvarela.misemestre.data.cambiosDeCalendario
import com.evelynvarela.misemestre.data.idDeEvento
import com.evelynvarela.misemestre.data.leerCalendario
import com.evelynvarela.misemestre.data.normalizarEnlace
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class CalendarioTest {

    private val bogota = ZoneId.of("America/Bogota")
    private val hoy = LocalDate.of(2026, 11, 2)

    private fun calendario(vararg eventos: String) =
        "BEGIN:VCALENDAR\r\n" +
            eventos.joinToString("") { "BEGIN:VEVENT\r\n$it\r\nEND:VEVENT\r\n" } +
            "END:VCALENDAR\r\n"

    @Test
    fun leeTareaDeCanvasConMateriaYHoraUtc() {
        val texto = calendario(
            "UID:event-assignment-1\r\nDTSTART:20261117T045900Z\r\nSUMMARY:Taller ABP [DAM-30526]"
        )

        val evento = leerCalendario(texto, bogota).single()

        assertEquals("Taller ABP", evento.nombre)
        assertEquals("DAM-30526", evento.materia)
        assertEquals(LocalDate.of(2026, 11, 16), evento.fecha)
    }

    @Test
    fun limpiaElNombreDelCursoDeCanvas() {
        val texto = calendario(
            "UID:event-assignment-2\r\nDTSTART;VALUE=DATE;VALUE=DATE:20261116\r\n" +
                "SUMMARY:Taller ABP  [DISEÑO DE APLICACIONES PARA MÓVILES_B2A_27106590_20262]"
        )

        val evento = leerCalendario(texto, bogota).single()

        assertEquals("Taller ABP", evento.nombre)
        assertEquals("Diseño de aplicaciones para móviles", evento.materia)
        assertEquals(LocalDate.of(2026, 11, 16), evento.fecha)
    }

    @Test
    fun uneLineasPartidasYQuitaEscapes() {
        val texto = calendario(
            "UID:a\r\nDTSTART;VALUE=DATE:20261120\r\nSUMMARY:Parcial\\, primera\r\n  parte"
        )

        val evento = leerCalendario(texto, bogota).single()

        assertEquals("Parcial, primera parte", evento.nombre)
        assertEquals("Calendario", evento.materia)
        assertEquals(LocalDate.of(2026, 11, 20), evento.fecha)
    }

    @Test
    fun respetaLaZonaHorariaDelEvento() {
        val texto = calendario(
            "UID:a\r\nDTSTART;TZID=Asia/Tokyo:20261120T080000\r\nSUMMARY:Reunión"
        )

        assertEquals(LocalDate.of(2026, 11, 19), leerCalendario(texto, bogota).single().fecha)
    }

    @Test
    fun ignoraEventosCanceladosOIncompletos() {
        val texto = calendario(
            "UID:a\r\nDTSTART:20261120\r\nSUMMARY:Cancelado\r\nSTATUS:CANCELLED",
            "UID:b\r\nSUMMARY:Sin fecha",
            "DTSTART:20261120\r\nSUMMARY:Sin identificador"
        )

        assertTrue(leerCalendario(texto, bogota).isEmpty())
    }

    @Test(expected = CalendarioInvalido::class)
    fun rechazaTextoQueNoEsCalendario() {
        leerCalendario("<html>Inicia sesión</html>", bogota)
    }

    @Test
    fun aceptaEnlacesSegurosYWebcal() {
        assertEquals("https://u.edu/a.ics", normalizarEnlace(" https://u.edu/a.ics "))
        assertEquals("https://u.edu/a.ics", normalizarEnlace("webcal://u.edu/a.ics"))
        assertNull(normalizarEnlace("http://u.edu/a.ics"))
        assertNull(normalizarEnlace("hola"))
    }

    @Test
    fun importaSoloEventosDentroDelPeriodo() {
        val eventos = listOf(
            EventoCalendario("pasado", "Pasado", "Apps", hoy.minusDays(1)),
            EventoCalendario("proximo", "Próximo", "Apps", hoy.plusDays(3)),
            EventoCalendario("lejano", "Lejano", "Apps", hoy.plusDays(181))
        )

        val cambios = cambiosDeCalendario(emptyList(), eventos, hoy)

        assertEquals(listOf("Próximo"), cambios.map { it.nombre })
        assertTrue(cambios.single().importada)
    }

    @Test
    fun actualizaFechaSinPerderElEstado() {
        val existente = Entrega(
            id = idDeEvento("uid"),
            nombre = "Taller",
            materia = "Apps",
            fecha = hoy.plusDays(3),
            entregada = true,
            importada = true
        )
        val evento = EventoCalendario("uid", "Taller", "Apps", hoy.plusDays(5))

        val cambio = cambiosDeCalendario(listOf(existente), listOf(evento), hoy).single()

        assertEquals(hoy.plusDays(5), cambio.fecha)
        assertTrue(cambio.entregada)
    }

    @Test
    fun noRepiteNiDevuelveLoQueYaEstaOculto() {
        val oculta = Entrega(
            id = idDeEvento("uid"),
            nombre = "Taller",
            materia = "Apps",
            fecha = hoy.plusDays(3),
            importada = true,
            oculta = true
        )
        val evento = EventoCalendario("uid", "Taller", "Apps", hoy.plusDays(3))

        assertTrue(cambiosDeCalendario(listOf(oculta), listOf(evento), hoy).isEmpty())
    }
}
