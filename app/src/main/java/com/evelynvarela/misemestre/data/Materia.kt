package com.evelynvarela.misemestre.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "materias")
data class Materia(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val nombre: String,
    val creditos: Int
)
