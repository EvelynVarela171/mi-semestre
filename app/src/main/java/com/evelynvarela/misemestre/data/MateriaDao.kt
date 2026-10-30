package com.evelynvarela.misemestre.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MateriaDao {
    @Query("SELECT * FROM materias ORDER BY nombre")
    fun verTodas(): Flow<List<Materia>>

    @Insert
    suspend fun agregar(materia: Materia)

    @Delete
    suspend fun borrar(materia: Materia)
}
