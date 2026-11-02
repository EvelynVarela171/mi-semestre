package com.evelynvarela.misemestre.data

class NotaRepository(
    private val dao: NotaDao,
    private val nube: NubeRepository = NubeRepository()
) {
    val todas = dao.verTodas()

    fun notas(materiaId: String) = dao.verDeMateria(materiaId)

    suspend fun agregar(nota: Nota) {
        dao.agregar(nota)
        nube.guardar(nota)
    }

    suspend fun actualizar(nota: Nota) {
        dao.actualizar(nota)
        nube.guardar(nota)
    }

    suspend fun borrar(nota: Nota) {
        dao.borrar(nota)
        nube.borrar(nota)
    }
}
