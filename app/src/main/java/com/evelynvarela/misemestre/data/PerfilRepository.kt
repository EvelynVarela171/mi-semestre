package com.evelynvarela.misemestre.data

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.time.LocalDate

fun semestreDe(fecha: LocalDate): String =
    "${fecha.year}-${if (fecha.monthValue <= 6) 1 else 2}"

data class DatosPerfil(
    val universidad: String = "",
    val semestre: String = semestreDe(LocalDate.now())
)

class PerfilRepository(
    private val auth: FirebaseAuth = Firebase.auth,
    private val firestore: FirebaseFirestore = Firebase.firestore
) {
    private fun documento(): DocumentReference? =
        auth.currentUser?.let {
            firestore.collection("usuarios").document(it.uid)
                .collection("perfil").document("datos")
        }

    val datos: Flow<DatosPerfil> = callbackFlow {
        val escucha = documento()?.addSnapshotListener { datos, _ ->
            if (datos != null) {
                val porDefecto = DatosPerfil()
                trySend(
                    DatosPerfil(
                        universidad = datos.getString("universidad") ?: porDefecto.universidad,
                        semestre = datos.getString("semestre") ?: porDefecto.semestre
                    )
                )
            }
        }
        awaitClose { escucha?.remove() }
    }

    fun guardar(datos: DatosPerfil) {
        documento()?.set(mapOf("universidad" to datos.universidad, "semestre" to datos.semestre))
    }
}
