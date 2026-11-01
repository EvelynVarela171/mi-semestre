package com.evelynvarela.misemestre

import com.evelynvarela.misemestre.data.EstadoNube
import com.evelynvarela.misemestre.data.Usuario
import com.evelynvarela.misemestre.data.estadoDe
import org.junit.Assert.assertEquals
import org.junit.Test

class SincronizacionTest {

    @Test
    fun lasInicialesUsanLasDosPrimerasPalabras() {
        assertEquals("EV", Usuario("Evelyn Varela", "a@b.co").iniciales)
        assertEquals("EV", Usuario("Evelyn", "a@b.co").iniciales)
        assertEquals("EV", Usuario("  evelyn   varela  ", "a@b.co").iniciales)
        assertEquals("?", Usuario("", "a@b.co").iniciales)
    }

    @Test
    fun elSaludoUsaElPrimerNombre() {
        assertEquals("Evelyn", Usuario("Evelyn Varela", "a@b.co").primerNombre)
    }

    @Test
    fun loPendienteGanaSobreLaFaltaDeConexion() {
        assertEquals(EstadoNube.Pendiente, estadoDe(hayPendientes = true, soloCache = true))
        assertEquals(EstadoNube.SinConexion, estadoDe(hayPendientes = false, soloCache = true))
        assertEquals(EstadoNube.Sincronizado, estadoDe(hayPendientes = false, soloCache = false))
    }
}
