package com.evelynvarela.misemestre

import android.app.Application
import com.evelynvarela.misemestre.data.Sincronizador

class MiSemestreApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Sincronizador.obtener(this).iniciar()
    }
}
