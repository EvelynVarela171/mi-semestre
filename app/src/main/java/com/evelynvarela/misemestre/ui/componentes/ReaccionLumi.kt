package com.evelynvarela.misemestre.ui.componentes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.evelynvarela.misemestre.ui.theme.Cielo
import com.evelynvarela.misemestre.ui.theme.Durazno
import com.evelynvarela.misemestre.ui.theme.Lila
import com.evelynvarela.misemestre.ui.theme.Linea
import com.evelynvarela.misemestre.ui.theme.Menta
import com.evelynvarela.misemestre.ui.theme.Nube
import com.evelynvarela.misemestre.ui.theme.Rosa
import com.evelynvarela.misemestre.ui.theme.Tinta
import kotlinx.coroutines.delay

private const val PIEZAS_DE_CONFETI = 48
private const val DURACION_REACCION = 2200L
private val coloresConfeti = listOf(Lila, Durazno, Cielo, Rosa, Menta)

data class Reaccion(val cara: CaraLumi, val mensaje: String, val confeti: Boolean = true)

fun reaccionANota(valor: Double): Reaccion = when {
    valor >= 4.0 -> Reaccion(CaraLumi.Feliz, "¡Qué buena nota!")
    valor >= 3.0 -> Reaccion(CaraLumi.Guino, "¡Vas bien, sigue así!", confeti = false)
    else -> Reaccion(
        CaraLumi.Preocupada,
        "No pasa nada, aún puedes recuperarla",
        confeti = false
    )
}

@Composable
fun ReaccionLumi(reaccion: Reaccion?, onTerminar: () -> Unit) {
    var ultima by remember { mutableStateOf(reaccion) }
    if (reaccion != null) ultima = reaccion

    LaunchedEffect(reaccion) {
        if (reaccion != null) {
            delay(DURACION_REACCION)
            onTerminar()
        }
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        AnimatedVisibility(
            visible = reaccion != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(Modifier.fillMaxSize().background(Nube.copy(alpha = 0.8f)))
        }

        if (reaccion?.confeti == true) Confeti()

        AnimatedVisibility(
            visible = reaccion != null,
            enter = fadeIn() + scaleIn(spring(dampingRatio = 0.5f)),
            exit = fadeOut() + scaleOut()
        ) {
            ultima?.let { actual ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LumiAnimada(
                        cara = actual.cara,
                        saludando = actual.cara != CaraLumi.Preocupada,
                        modifier = Modifier.size(130.dp)
                    )
                    Text(
                        text = actual.mensaje,
                        color = Tinta,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(18.dp))
                            .border(1.5.dp, Linea, RoundedCornerShape(18.dp))
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun Confeti() {
    val avance = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        avance.animateTo(1f, tween(DURACION_REACCION.toInt(), easing = LinearEasing))
    }

    Canvas(Modifier.fillMaxSize()) {
        repeat(PIEZAS_DE_CONFETI) { i ->
            val inicio = (i * 0.37f) % 0.45f
            val caida = ((avance.value - inicio) / 0.55f).coerceIn(0f, 1f)
            if (caida > 0f && caida < 1f) {
                val centro = Offset(
                    x = size.width * ((i * 0.618f) % 1f),
                    y = size.height * caida - 20f
                )
                rotate(degrees = caida * 720f + i * 40f, pivot = centro) {
                    drawRect(
                        color = coloresConfeti[i % coloresConfeti.size],
                        topLeft = centro,
                        size = Size(18f, 10f)
                    )
                }
            }
        }
    }
}
