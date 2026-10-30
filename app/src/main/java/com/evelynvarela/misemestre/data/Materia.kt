package com.evelynvarela.misemestre.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "materias")
data class Materia(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val creditos: Int
)
