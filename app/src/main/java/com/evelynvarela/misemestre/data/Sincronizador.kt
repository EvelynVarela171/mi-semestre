package com.evelynvarela.misemestre.data

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class EstadoNube { Conectando, Sincronizado, Pendiente, SinConexion }

fun estadoDe(hayPendientes: Boolean, soloCache: Boolean): EstadoNube = when {
    hayPendientes -> EstadoNube.Pendiente
    soloCache -> EstadoNube.SinConexion
    else -> EstadoNube.Sincronizado
}

class Sincronizador private constructor(context: Context) {

    private val dao = AppDatabase.obtener(context).sincronizacionDao()
    private val enlace = EnlaceCalendario(context)
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO.limitedParallelism(1))

    private val _estado = MutableStateFlow(EstadoNube.Conectando)
    val estado = _estado.asStateFlow()

    private var uidActual: String? = null
    private var escuchas: List<ListenerRegistration> = emptyList()
    private var materias: QuerySnapshot? = null
    private var notas: QuerySnapshot? = null
    private var entregas: QuerySnapshot? = null

    fun iniciar() {
        auth.addAuthStateListener { cuenta ->
            val uid = cuenta.currentUser?.uid
            if (uid == uidActual) return@addAuthStateListener
            val habiaSesion = uidActual != null
            detener()
            uidActual = uid
            if (habiaSesion) scope.launch { dao.borrarTodo() }
            if (uid != null) {
                if (enlaceDeOtraCuenta(enlace.dueno, uid)) enlace.olvidar()
                enlace.dueno = uid
                escuchar(uid)
            }
        }
    }

    private fun escuchar(uid: String) {
        val usuario = firestore.collection("usuarios").document(uid)
        escuchas = listOf(
            usuario.collection("materias")
                .addSnapshotListener(MetadataChanges.INCLUDE) { datos, error ->
                    if (error != null) _estado.value = EstadoNube.SinConexion
                    if (datos != null) {
                        materias = datos
                        aplicar()
                    }
                },
            usuario.collection("notas")
                .addSnapshotListener(MetadataChanges.INCLUDE) { datos, error ->
                    if (error != null) _estado.value = EstadoNube.SinConexion
                    if (datos != null) {
                        notas = datos
                        aplicar()
                    }
                },
            usuario.collection("entregas")
                .addSnapshotListener(MetadataChanges.INCLUDE) { datos, error ->
                    if (error != null) _estado.value = EstadoNube.SinConexion
                    if (datos != null) {
                        entregas = datos
                        aplicar()
                    }
                }
        )
    }

    private fun aplicar() {
        val datosMaterias = materias ?: return
        val datosNotas = notas ?: return
        val datosEntregas = entregas ?: return
        _estado.value = estadoDe(
            hayPendientes = datosMaterias.metadata.hasPendingWrites() ||
                datosNotas.metadata.hasPendingWrites() ||
                datosEntregas.metadata.hasPendingWrites(),
            soloCache = datosMaterias.metadata.isFromCache ||
                datosNotas.metadata.isFromCache ||
                datosEntregas.metadata.isFromCache
        )
        val listaMaterias = datosMaterias.documents.mapNotNull { it.aMateria() }
        val listaNotas = datosNotas.documents.mapNotNull { it.aNota() }
        val listaEntregas = datosEntregas.documents.mapNotNull { it.aEntrega() }
        scope.launch { dao.reemplazar(listaMaterias, listaNotas, listaEntregas) }
    }

    private fun detener() {
        escuchas.forEach { it.remove() }
        escuchas = emptyList()
        materias = null
        notas = null
        entregas = null
        _estado.value = EstadoNube.Conectando
    }

    companion object {
        @Volatile
        private var instancia: Sincronizador? = null

        fun obtener(context: Context): Sincronizador {
            return instancia ?: synchronized(this) {
                instancia ?: Sincronizador(context.applicationContext).also { instancia = it }
            }
        }
    }
}
