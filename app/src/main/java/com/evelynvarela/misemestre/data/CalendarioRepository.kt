package com.evelynvarela.misemestre.data

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.time.ZoneId

private const val TAMANO_MAXIMO = 5 * 1024 * 1024
private const val ESPERA_MAXIMA = 15_000

class EnlaceCalendario(context: Context) {
    private val preferencias = context.getSharedPreferences("calendario", Context.MODE_PRIVATE)

    var url: String?
        get() = preferencias.getString("enlace", null)
        set(valor) = preferencias.edit { putString("enlace", valor) }

    var materiasIgnoradas: Set<String>
        get() = preferencias.getStringSet("materiasIgnoradas", emptySet()).orEmpty()
        set(valor) = preferencias.edit { putStringSet("materiasIgnoradas", valor) }

    var dueno: String?
        get() = preferencias.getString("dueno", null)
        set(valor) = preferencias.edit { putString("dueno", valor) }

    fun olvidar() = preferencias.edit { clear() }
}

fun enlaceDeOtraCuenta(dueno: String?, uid: String): Boolean = dueno != null && dueno != uid

class CalendarioRepository(
    private val entregas: EntregaRepository,
    private val enlace: EnlaceCalendario
) {
    val conectado: Boolean
        get() = enlace.url != null

    suspend fun conectar(texto: String): Int {
        val url = normalizarEnlace(texto) ?: throw CalendarioInvalido()
        val cambios = importar(url)
        enlace.url = url
        return cambios
    }

    suspend fun actualizar(): Int {
        val url = enlace.url ?: return 0
        return importar(url)
    }

    suspend fun desconectar() {
        enlace.olvidar()
        entregas.borrarImportadas()
    }

    private suspend fun importar(url: String): Int {
        val eventos = withContext(Dispatchers.IO) {
            leerCalendario(descargar(url), ZoneId.systemDefault())
        }
        val cambios = cambiosDeCalendario(entregas.todas(), eventos, LocalDate.now())
        if (cambios.isNotEmpty()) entregas.guardar(cambios)
        return cambios.size
    }

    private fun descargar(url: String): String {
        val conexion = URL(url).openConnection() as HttpURLConnection
        conexion.connectTimeout = ESPERA_MAXIMA
        conexion.readTimeout = ESPERA_MAXIMA
        try {
            val datos = ByteArrayOutputStream()
            conexion.inputStream.use { entrada ->
                val bufer = ByteArray(8192)
                while (true) {
                    val leidos = entrada.read(bufer)
                    if (leidos < 0) break
                    datos.write(bufer, 0, leidos)
                    if (datos.size() > TAMANO_MAXIMO) throw CalendarioInvalido()
                }
            }
            return datos.toString(Charsets.UTF_8.name())
        } finally {
            conexion.disconnect()
        }
    }
}
