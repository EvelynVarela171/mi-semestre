package com.evelynvarela.misemestre.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Materia::class, Nota::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun materiaDao(): MateriaDao
    abstract fun notaDao(): NotaDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        fun obtener(context: Context): AppDatabase {
            return instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mi_semestre.db"
                ).fallbackToDestructiveMigration(dropAllTables = true)
                    .build().also { instancia = it }
            }
        }
    }
}
