package com.evelynvarela.misemestre.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

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
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val materiaId: String,
    val nombre: String,
    val peso: Int,
    val valor: Double? = null,
    val creadaEn: Long = System.currentTimeMillis()
)
