package com.evelynvarela.misemestre.ui.materia

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.evelynvarela.misemestre.data.AppDatabase
import com.evelynvarela.misemestre.data.MateriaRepository
import com.evelynvarela.misemestre.data.Nota
import com.evelynvarela.misemestre.data.NotaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MateriaViewModel(
    application: Application,
    estado: SavedStateHandle
) : AndroidViewModel(application) {

    private val materiaId: Int = checkNotNull(estado["id"])
    private val base = AppDatabase.obtener(application)
    private val materias = MateriaRepository(base.materiaDao())
    private val repositorio = NotaRepository(base.notaDao())

    val materia = materias.materias
        .map { lista -> lista.find { it.id == materiaId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val notas = repositorio.notas(materiaId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun agregar(nombre: String, peso: Int, valor: Double?) {
        viewModelScope.launch {
            repositorio.agregar(
                Nota(materiaId = materiaId, nombre = nombre, peso = peso, valor = valor)
            )
        }
    }

    fun actualizar(nota: Nota) {
        viewModelScope.launch { repositorio.actualizar(nota) }
    }

    fun borrar(nota: Nota) {
        viewModelScope.launch { repositorio.borrar(nota) }
    }
}
