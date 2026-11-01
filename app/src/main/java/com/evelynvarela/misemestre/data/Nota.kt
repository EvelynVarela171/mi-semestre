package com.evelynvarela.misemestre.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "notas",
    foreignKeys = [
        ForeignKey(
            entity = Materia::class,
            parentColumns = ["id"],
            childColumns = ["materiaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("materiaId")]
)
data class Nota(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val materiaId: Int,
    val nombre: String,
    val peso: Int,
    val valor: Double? = null
)
