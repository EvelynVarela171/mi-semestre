package com.evelynvarela.misemestre

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.evelynvarela.misemestre.navegacion.AppNavegacion
import com.evelynvarela.misemestre.ui.theme.MiSemestreTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Debe ir antes de super.onCreate para mostrar a Lumi al abrir la app
        installSplashScreen()
        super.onCreate(savedInstanceState)
        // La app solo tiene tema claro: íconos oscuros en las barras del sistema
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        setContent {
            MiSemestreTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavegacion(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}