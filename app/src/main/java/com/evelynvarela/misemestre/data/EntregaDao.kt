package com.evelynvarela.misemestre.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface EntregaDao {
    @Query("SELECT * FROM entregas WHERE oculta = 0 ORDER BY fecha")
    fun verVisibles(): Flow<List<Entrega>>

    @Query("SELECT * FROM entregas")
    suspend fun todas(): List<Entrega>

    @Query(
        "SELECT * FROM entregas WHERE oculta = 0 AND entregada = 0 " +
            "AND fecha BETWEEN :desde AND :hasta ORDER BY fecha"
    )
    suspend fun pendientesEntre(desde: LocalDate, hasta: LocalDate): List<Entrega>

    @Upsert
    suspend fun guardar(entrega: Entrega)

    @Upsert
    suspend fun guardar(entregas: List<Entrega>)

    @Delete
    suspend fun borrar(entrega: Entrega)

    @Delete
    suspend fun borrar(entregas: List<Entrega>)
}
