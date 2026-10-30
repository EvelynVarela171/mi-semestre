package com.evelynvarela.misemestre.ui.inicio

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.evelynvarela.misemestre.data.AppDatabase
import com.evelynvarela.misemestre.data.Materia
import com.evelynvarela.misemestre.data.MateriaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class InicioViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = MateriaRepository(AppDatabase.obtener(application).materiaDao())

    // null = todavía está leyendo la base de datos
    val materias = repositorio.materias.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    fun agregar(nombre: String, creditos: Int) {
        viewModelScope.launch {
            repositorio.agregar(Materia(nombre = nombre, creditos = creditos))
        }
    }

    fun borrar(materia: Materia) {
        viewModelScope.launch { repositorio.borrar(materia) }
    }
}
