package com.evelynvarela.misemestre

import android.app.Application
import com.evelynvarela.misemestre.data.Recordatorios
import com.evelynvarela.misemestre.data.Sincronizador

class MiSemestreApp : Application() {
    override fun onCreate() {
        super.onCreate()
        activarAppCheck()
        Sincronizador.obtener(this).iniciar()
        Recordatorios.programar(this)
    }
}
