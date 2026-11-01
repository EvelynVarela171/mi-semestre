package com.evelynvarela.misemestre.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Lila,
    onPrimary = LilaTinta,
    secondary = Durazno,
    onSecondary = Tinta,
    tertiary = Cielo,
    onTertiary = Tinta,
    background = Nube,
    onBackground = Tinta,
    surface = Blanco,
    onSurface = Tinta,
    error = Fucsia,
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