package com.evelynvarela.misemestre.data

data class Usuario(val nombre: String, val correo: String) {
    val primerNombre: String = nombre.trim().substringBefore(" ")

    val iniciales: String = nombre.trim().split(" ").filter { it.isNotEmpty() }.let { partes ->
        when {
            partes.isEmpty() -> "?"
            partes.size == 1 -> partes[0].take(2).uppercase()
            else -> "${partes[0].first()}${partes[1].first()}".uppercase()
        }
    }
}
