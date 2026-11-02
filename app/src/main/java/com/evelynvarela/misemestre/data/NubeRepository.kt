package com.evelynvarela.misemestre.data

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.time.LocalDate

private const val TAMANO_LOTE = 400

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

    fun guardar(entrega: Entrega) {
        cuenta()?.collection("entregas")?.document(entrega.id)?.set(datosDe(entrega))
    }

    fun guardar(entregas: List<Entrega>) {
        val coleccion = cuenta()?.collection("entregas") ?: return
        entregas.chunked(TAMANO_LOTE).forEach { lote ->
            firestore.batch().apply {
                lote.forEach { set(coleccion.document(it.id), datosDe(it)) }
            }.commit()
        }
    }

    private fun datosDe(entrega: Entrega) = mapOf(
        "nombre" to entrega.nombre,
        "materia" to entrega.materia,
        "fecha" to entrega.fecha.toString(),
        "entregada" to entrega.entregada,
        "importada" to entrega.importada,
        "oculta" to entrega.oculta
    )

    fun borrar(entrega: Entrega) {
        cuenta()?.collection("entregas")?.document(entrega.id)?.delete()
    }

    fun borrar(entregas: List<Entrega>) {
        val coleccion = cuenta()?.collection("entregas") ?: return
        entregas.chunked(TAMANO_LOTE).forEach { lote ->
            firestore.batch().apply {
                lote.forEach { delete(coleccion.document(it.id)) }
            }.commit()
        }
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

fun DocumentSnapshot.aEntrega(): Entrega? {
    val nombre = getString("nombre") ?: return null
    val materia = getString("materia") ?: return null
    val fecha = getString("fecha")
        ?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
        ?: return null
    return Entrega(
        id = id,
        nombre = nombre,
        materia = materia,
        fecha = fecha,
        entregada = getBoolean("entregada") ?: false,
        importada = getBoolean("importada") ?: false,
        oculta = getBoolean("oculta") ?: false
    )
}
