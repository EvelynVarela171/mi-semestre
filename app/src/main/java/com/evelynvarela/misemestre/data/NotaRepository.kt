package com.evelynvarela.misemestre.data

class NotaRepository(private val dao: NotaDao) {
    fun notas(materiaId: Int) = dao.verDeMateria(materiaId)

    suspend fun agregar(nota: Nota) = dao.agregar(nota)

    suspend fun actualizar(nota: Nota) = dao.actualizar(nota)

    suspend fun borrar(nota: Nota) = dao.borrar(nota)
}
