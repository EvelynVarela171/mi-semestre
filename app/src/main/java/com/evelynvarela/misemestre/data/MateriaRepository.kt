package com.evelynvarela.misemestre.data

class MateriaRepository(private val dao: MateriaDao) {
    val materias = dao.verTodas()

    suspend fun agregar(materia: Materia) = dao.agregar(materia)

    suspend fun borrar(materia: Materia) = dao.borrar(materia)
}
