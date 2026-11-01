package com.evelynvarela.misemestre.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evelynvarela.misemestre.data.SesionRepository
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val sesion = SesionRepository()

    var cargando by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    fun entrar(correo: String, clave: String, onListo: () -> Unit) {
        intentar(onListo) { sesion.entrar(correo.trim(), clave) }
    }

    fun registrar(nombre: String, correo: String, clave: String, onListo: () -> Unit) {
        intentar(onListo) { sesion.registrar(nombre.trim(), correo.trim(), clave) }
    }

    fun limpiarError() {
        error = null
    }

    private fun intentar(onListo: () -> Unit, accion: suspend () -> Unit) {
        viewModelScope.launch {
            cargando = true
            error = null
            try {
                accion()
                onListo()
            } catch (e: FirebaseException) {
                error = mensajeDe(e)
            } finally {
                cargando = false
            }
        }
    }

    private fun mensajeDe(e: FirebaseException): String = when (e) {
        is FirebaseNetworkException -> "No hay conexión. Revisa tu internet e inténtalo de nuevo."
        is FirebaseAuthWeakPasswordException -> "La contraseña debe tener al menos 6 caracteres."
        is FirebaseAuthUserCollisionException -> "Ya existe una cuenta con ese correo."
        is FirebaseAuthInvalidCredentialsException,
        is FirebaseAuthInvalidUserException -> "Correo o contraseña incorrectos."
        else -> "No se pudo continuar. Inténtalo de nuevo."
    }
}
