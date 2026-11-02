package com.evelynvarela.misemestre.data

class EntregaRepository(
    private val dao: EntregaDao,
    private val nube: NubeRepository = NubeRepository()
) {
    val entregas = dao.verVisibles()

    suspend fun todas() = dao.todas()

    suspend fun guardar(entrega: Entrega) {
        dao.guardar(entrega)
        nube.guardar(entrega)
    }

    suspend fun guardar(entregas: List<Entrega>) {
        dao.guardar(entregas)
        nube.guardar(entregas)
    }

    suspend fun borrar(entrega: Entrega) {
        if (entrega.importada) {
            guardar(entrega.copy(oculta = true))
        } else {
            dao.borrar(entrega)
            nube.borrar(entrega)
        }
    }

    suspend fun borrarImportadas() {
        val importadas = dao.todas().filter { it.importada }
        dao.borrar(importadas)
        nube.borrar(importadas)
    }
}
