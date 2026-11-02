package com.evelynvarela.misemestre.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface SincronizacionDao {
    @Query("DELETE FROM materias")
    suspend fun borrarMaterias()

    @Query("DELETE FROM entregas")
    suspend fun borrarEntregas()

    @Insert
    suspend fun agregarMaterias(materias: List<Materia>)

    @Insert
    suspend fun agregarNotas(notas: List<Nota>)

    @Insert
    suspend fun agregarEntregas(entregas: List<Entrega>)

    @Transaction
    suspend fun borrarTodo() {
        borrarMaterias()
        borrarEntregas()
    }

    @Transaction
    suspend fun reemplazar(materias: List<Materia>, notas: List<Nota>, entregas: List<Entrega>) {
        borrarTodo()
        agregarMaterias(materias)
        val ids = materias.map { it.id }.toSet()
        agregarNotas(notas.filter { it.materiaId in ids })
        agregarEntregas(entregas)
    }
}
