package com.evelynvarela.misemestre.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.time.LocalDate
import java.util.UUID

@Entity(tableName = "entregas")
data class Entrega(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val nombre: String,
    val materia: String,
    val fecha: LocalDate,
    val entregada: Boolean = false,
    val importada: Boolean = false,
    val oculta: Boolean = false,
    val editada: Boolean = false
)

class Convertidores {
    @TypeConverter
    fun aTexto(fecha: LocalDate): String = fecha.toString()

    @TypeConverter
    fun aFecha(texto: String): LocalDate = LocalDate.parse(texto)
}
