package com.evelynvarela.misemestre.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NotaDao {
    @Query("SELECT * FROM notas WHERE materiaId = :materiaId ORDER BY creadaEn")
    fun verDeMateria(materiaId: String): Flow<List<Nota>>

    @Query("SELECT * FROM notas WHERE materiaId = :materiaId")
    suspend fun deMateria(materiaId: String): List<Nota>

    @Insert
    suspend fun agregar(nota: Nota)

    @Update
    suspend fun actualizar(nota: Nota)

    @Delete
    suspend fun borrar(nota: Nota)
}
