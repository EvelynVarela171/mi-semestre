package com.evelynvarela.misemestre.data

class MateriaRepository(
    private val dao: MateriaDao,
    private val notaDao: NotaDao,
    private val nube: NubeRepository = NubeRepository()
) {
    val materias = dao.verTodas()

    suspend fun agregar(materia: Materia) {
        dao.agregar(materia)
        nube.guardar(materia)
    }

    suspend fun borrar(materia: Materia) {
        val notas = notaDao.deMateria(materia.id)
        dao.borrar(materia)
        nube.borrar(materia, notas)
    }
}
