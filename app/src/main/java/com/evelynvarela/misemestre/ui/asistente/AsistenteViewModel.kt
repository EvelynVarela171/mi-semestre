package com.evelynvarela.misemestre.ui.asistente

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.evelynvarela.misemestre.data.AppDatabase
import com.evelynvarela.misemestre.data.LumiRepository
import com.evelynvarela.misemestre.data.MateriaRepository
import com.evelynvarela.misemestre.data.NotaRepository
import com.evelynvarela.misemestre.data.contextoParaLumi
import com.evelynvarela.misemestre.data.entregasEjemplo
import com.google.firebase.ai.type.FirebaseAIException
import com.google.firebase.ai.type.QuotaExceededException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.LocalDate

data class Mensaje(val texto: String, val deLumi: Boolean, val esError: Boolean = false)

class AsistenteViewModel(application: Application) : AndroidViewModel(application) {

    private val base = AppDatabase.obtener(application)
    private val materias = MateriaRepository(base.materiaDao(), base.notaDao())
    private val notas = NotaRepository(base.notaDao())
    private val lumi = LumiRepository()

    val mensajes = mutableStateListOf(
        Mensaje(
            texto = "¡Hola! Conozco tus materias, notas y entregas. ¿En qué te ayudo hoy?",
            deLumi = true
        )
    )
    var pensando by mutableStateOf(false)
        private set

    fun enviar(pregunta: String) {
        if (pregunta.isBlank() || pensando) return
        mensajes.add(Mensaje(pregunta.trim(), deLumi = false))
        pensando = true

        viewModelScope.launch {
            var respuesta = ""
            try {
                val contexto = contextoParaLumi(
                    hoy = LocalDate.now(),
                    materias = materias.materias.first(),
                    notas = notas.todas.first(),
                    entregas = entregasEjemplo
                )
                lumi.responder(pregunta.trim(), contexto).collect { parte ->
                    if (respuesta.isEmpty()) {
                        pensando = false
                        mensajes.add(Mensaje(parte, deLumi = true))
                    } else {
                        mensajes[mensajes.lastIndex] = Mensaje(respuesta + parte, deLumi = true)
                    }
                    respuesta += parte
                }
            } catch (e: QuotaExceededException) {
                mostrarError("Ya respondí muchas preguntas por hoy. Inténtalo más tarde.")
            } catch (e: FirebaseAIException) {
                Log.e("Lumi", "Gemini no pudo responder", e)
                mostrarError("No pude responder. Revisa tu internet e inténtalo de nuevo.")
            } catch (e: IOException) {
                mostrarError("No pude responder. Revisa tu internet e inténtalo de nuevo.")
            } finally {
                pensando = false
            }
        }
    }

    private fun mostrarError(texto: String) {
        mensajes.add(Mensaje(texto, deLumi = true, esError = true))
    }
}
