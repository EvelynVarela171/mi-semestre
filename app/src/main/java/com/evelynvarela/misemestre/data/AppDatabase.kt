package com.evelynvarela.misemestre.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Materia::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun materiaDao(): MateriaDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        fun obtener(context: Context): AppDatabase {
            return instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mi_semestre.db"
                ).build().also { instancia = it }
            }
        }
    }
}
