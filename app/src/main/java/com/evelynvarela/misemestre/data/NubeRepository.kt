package com.evelynvarela.misemestre.data

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class NubeRepository(
    private val auth: FirebaseAuth = Firebase.auth,
    private val firestore: FirebaseFirestore = Firebase.firestore
) {
    private fun cuenta(): DocumentReference? =
        auth.currentUser?.let { firestore.collection("usuarios").document(it.uid) }

    fun guardar(materia: Materia) {
        cuenta()?.collection("materias")?.document(materia.id)
            ?.set(mapOf("nombre" to materia.nombre, "creditos" to materia.creditos))
    }

    fun guardar(nota: Nota) {
        cuenta()?.collection("notas")?.document(nota.id)?.set(
            mapOf(
                "materiaId" to nota.materiaId,
                "nombre" to nota.nombre,
                "peso" to nota.peso,
                "valor" to nota.valor,
                "creadaEn" to nota.creadaEn
            )
        )
    }

    fun borrar(nota: Nota) {
        cuenta()?.collection("notas")?.document(nota.id)?.delete()
    }

    fun borrar(materia: Materia, notas: List<Nota>) {
        val cuenta = cuenta() ?: return
        firestore.batch().apply {
            delete(cuenta.collection("materias").document(materia.id))
            notas.forEach { delete(cuenta.collection("notas").document(it.id)) }
        }.commit()
    }
}

fun DocumentSnapshot.aMateria(): Materia? {
    val nombre = getString("nombre") ?: return null
    val creditos = getLong("creditos")?.toInt() ?: return null
    return Materia(id = id, nombre = nombre, creditos = creditos)
}

fun DocumentSnapshot.aNota(): Nota? {
    val materiaId = getString("materiaId") ?: return null
    val nombre = getString("nombre") ?: return null
    val peso = getLong("peso")?.toInt() ?: return null
    return Nota(
        id = id,
        materiaId = materiaId,
        nombre = nombre,
        peso = peso,
        valor = getDouble("valor"),
        creadaEn = getLong("creadaEn") ?: 0L
    )
}
