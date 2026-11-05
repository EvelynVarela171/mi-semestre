package com.evelynvarela.misemestre.ui.inicio

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.evelynvarela.misemestre.data.AppDatabase
import com.evelynvarela.misemestre.data.CalculadoraNotas
import com.evelynvarela.misemestre.data.DatosPerfil
import com.evelynvarela.misemestre.data.Entrega
import com.evelynvarela.misemestre.data.EntregaRepository
import com.evelynvarela.misemestre.data.FraseDelPlan
import com.evelynvarela.misemestre.data.Materia
import com.evelynvarela.misemestre.data.MateriaRepository
import com.evelynvarela.misemestre.data.NotaRepository
import com.evelynvarela.misemestre.data.PerfilRepository
import com.evelynvarela.misemestre.data.PlanDelDia
import com.evelynvarela.misemestre.data.ResumenSemestre
import com.evelynvarela.misemestre.data.SesionRepository
import com.evelynvarela.misemestre.data.Sincronizador
import com.evelynvarela.misemestre.data.Usuario
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class InicioViewModel(application: Application) : AndroidViewModel(application) {

    private val base = AppDatabase.obtener(application)
    private val repositorio = MateriaRepository(base.materiaDao(), base.notaDao())
    private val notas = NotaRepository(base.notaDao())
    private val repositorioEntregas = EntregaRepository(base.entregaDao())
    private val repositorioPerfil = PerfilRepository()
    private val frases = FraseDelPlan(application)

    val usuario: Usuario = SesionRepository().usuario ?: Usuario("", "")
    val estadoNube = Sincronizador.obtener(application).estado

    val materias = repositorio.materias.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    val resumen = combine(
        repositorio.materias,
        notas.todas,
        CalculadoraNotas::resumenSemestre
    ).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ResumenSemestre(null, 0)
    )

    val entregas = repositorioEntregas.entregas.stateIn<List<Entrega>?>(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    val perfil = repositorioPerfil.datos.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        DatosPerfil()
    )

    var fraseDelPlan by mutableStateOf<String?>(null)
        private set
    private var pedidoDeFrase: Job? = null

    fun pedirFrase(plan: PlanDelDia) {
        pedidoDeFrase?.cancel()
        pedidoDeFrase = viewModelScope.launch {
            fraseDelPlan = frases.obtener(LocalDate.now(), plan)
        }
    }

    fun guardarPerfil(datos: DatosPerfil) {
        repositorioPerfil.guardar(datos)
    }

    fun agregar(nombre: String, creditos: Int) {
        viewModelScope.launch {
            repositorio.agregar(Materia(nombre = nombre, creditos = creditos))
        }
    }

    fun borrar(materia: Materia) {
        viewModelScope.launch { repositorio.borrar(materia) }
    }
}
