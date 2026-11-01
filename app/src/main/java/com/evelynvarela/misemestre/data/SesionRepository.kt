package com.evelynvarela.misemestre.data

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.tasks.await

class SesionRepository(private val auth: FirebaseAuth = Firebase.auth) {

    val usuario: Usuario?
        get() = auth.currentUser?.let { cuenta ->
            val correo = cuenta.email.orEmpty()
            Usuario(cuenta.displayName.orEmpty().ifBlank { correo.substringBefore("@") }, correo)
        }

    suspend fun entrar(correo: String, clave: String) {
        auth.signInWithEmailAndPassword(correo, clave).await()
    }

    suspend fun registrar(nombre: String, correo: String, clave: String) {
        val resultado = auth.createUserWithEmailAndPassword(correo, clave).await()
        resultado.user?.updateProfile(userProfileChangeRequest { displayName = nombre })?.await()
    }

    fun salir() = auth.signOut()
}
