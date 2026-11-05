package com.evelynvarela.misemestre.ui.entregas

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.evelynvarela.misemestre.data.AppDatabase
import com.evelynvarela.misemestre.data.CalendarioInvalido
import com.evelynvarela.misemestre.data.CalendarioRepository
import com.evelynvarela.misemestre.data.EnlaceCalendario
import com.evelynvarela.misemestre.data.Entrega
import com.evelynvarela.misemestre.data.EntregaRepository
import com.evelynvarela.misemestre.data.Materia
import com.evelynvarela.misemestre.data.MateriaRepository
import com.evelynvarela.misemestre.data.materiasSinCrear
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.LocalDate

private const val ERROR_AL_ACTUALIZAR =
    "Hoy no pude actualizar. Tu calendario sigue conectado; prueba más tarde."

class EntregasViewModel(application: Application) : AndroidViewModel(application) {

    private val base = AppDatabase.obtener(application)
    private val repositorio = EntregaRepository(base.entregaDao())
    private val repositorioMaterias = MateriaRepository(base.materiaDao(), base.notaDao())
    private val enlace = EnlaceCalendario(application)
    private val calendario = CalendarioRepository(repositorio, enlace)
    private val ignoradas = MutableStateFlow(enlace.materiasIgnoradas)

    val entregas = repositorio.entregas.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )
    val materias = repositorioMaterias.materias.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
    val materiasSugeridas = combine(
        repositorio.entregas,
        repositorioMaterias.materias,
        ignoradas,
        ::materiasSinCrear
    ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var conectado by mutableStateOf(calendario.conectado)
        private set
    var ocupado by mutableStateOf(false)
        private set
    var mensaje by mutableStateOf<String?>(null)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    fun agregar(nombre: String, materia: String, fecha: LocalDate) {
        viewModelScope.launch {
            repositorio.guardar(Entrega(nombre = nombre, materia = materia, fecha = fecha))
        }
    }

    fun editar(entrega: Entrega, nombre: String, materia: String, fecha: LocalDate) {
        viewModelScope.launch {
            repositorio.guardar(
                entrega.copy(nombre = nombre, materia = materia, fecha = fecha, editada = true)
            )
        }
    }

    fun cambiar(entrega: Entrega) {
        viewModelScope.launch {
            repositorio.guardar(entrega.copy(entregada = !entrega.entregada))
        }
    }

    fun borrar(entrega: Entrega) {
        viewModelScope.launch { repositorio.borrar(entrega) }
    }

    fun crearMaterias(creditos: Map<String, Int>) {
        viewModelScope.launch {
            creditos.forEach { (nombre, valor) ->
                repositorioMaterias.agregar(Materia(nombre = nombre, creditos = valor))
            }
        }
    }

    fun ignorarMaterias(nombres: List<String>) {
        val nuevas = enlace.materiasIgnoradas + nombres
        enlace.materiasIgnoradas = nuevas
        ignoradas.value = nuevas
    }

    fun limpiarError() {
        error = null
    }

    fun conectar(enlace: String, alTerminar: () -> Unit) {
        importar {
            val cambios = calendario.conectar(enlace)
            conectado = true
            alTerminar()
            cambios
        }
    }

    fun actualizar() {
        enlace.materiasIgnoradas = emptySet()
        ignoradas.value = emptySet()
        importar { calendario.actualizar() }
    }

    fun desconectar() {
        viewModelScope.launch {
            calendario.desconectar()
            ignoradas.value = emptySet()
            conectado = false
            mensaje = null
        }
    }

    private fun importar(accion: suspend () -> Int) {
        if (ocupado) return
        ocupado = true
        error = null
        viewModelScope.launch {
            try {
                val cambios = accion()
                mensaje = when (cambios) {
                    0 -> "Todo está al día"
                    1 -> "1 entrega nueva o actualizada"
                    else -> "$cambios entregas nuevas o actualizadas"
                }
            } catch (_: CalendarioInvalido) {
                error = if (conectado) ERROR_AL_ACTUALIZAR else "Ese enlace no parece ser de un calendario."
            } catch (_: IOException) {
                error = if (conectado) {
                    ERROR_AL_ACTUALIZAR
                } else {
                    "No pude leer el calendario. Revisa el enlace y tu internet."
                }
            } finally {
                ocupado = false
            }
        }
    }
}
