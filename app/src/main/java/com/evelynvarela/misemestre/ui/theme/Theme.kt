package com.evelynvarela.misemestre.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Lila,            // botones, ícono activo, tarjeta del promedio
    onPrimary = LilaTinta,     // texto ENCIMA del lila
    secondary = Durazno,       // un pastel de materia
    onSecondary = Tinta,
    tertiary = Cielo,          // otro pastel de materia
    onTertiary = Tinta,
    background = Nube,         // fondo de todas las pantallas
    onBackground = Tinta,      // texto sobre el fondo
    surface = Blanco,     // tarjetas
    onSurface = Tinta,         // texto en las tarjetas
    error = Fucsia,            // lo urgente
    onError = Tinta
)

@Composable
fun MiSemestreTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}