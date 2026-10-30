package com.evelynvarela.misemestre.ui.componentes

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.evelynvarela.misemestre.ui.theme.Blanco
import com.evelynvarela.misemestre.ui.theme.Cielo
import com.evelynvarela.misemestre.ui.theme.Durazno
import com.evelynvarela.misemestre.ui.theme.Menta
import com.evelynvarela.misemestre.ui.theme.MiSemestreTheme
import com.evelynvarela.misemestre.ui.theme.Rosa
import com.evelynvarela.misemestre.ui.theme.Tinta
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin

enum class CaraLumi { Normal, Feliz, Guino, Sorpresa }

private val Cupula = Color(0xFFC9B8FD)
private val Borde = Color(0xFFB9A6FB)
private val Estrella = Color(0xFFFFE27A)

// Lumi dibujada por partes: así cada parte se puede mover por separado.
// Usa las mismas coordenadas del prototipo (de 20 a 140 en cada eje).
@Composable
fun LumiAnimada(
    cara: CaraLumi,
    saludando: Boolean,
    modifier: Modifier = Modifier,
    tapandoOjos: Boolean = false
) {
    val movimiento = rememberInfiniteTransition(label = "movimiento")
    val onda by movimiento.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(1600, easing = LinearEasing)),
        label = "onda"
    )
    val manito by movimiento.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(220), RepeatMode.Reverse),
        label = "manito"
    )
    val saludo by animateFloatAsState(
        targetValue = if (saludando) 1f else 0f,
        animationSpec = tween(250),
        label = "saludo"
    )
    val tapar by animateFloatAsState(
        targetValue = if (tapandoOjos) 1f else 0f,
        animationSpec = tween(300),
        label = "tapar"
    )

    // Con los ojos tapados, abre un huequito entre las cintas para espiar
    val espiando = remember { Animatable(0f) }
    var espiaDerecha by remember { mutableStateOf(true) }
    LaunchedEffect(tapandoOjos) {
        espiando.snapTo(0f)
        while (tapandoOjos) {
            delay(1600)
            espiando.animateTo(1f, tween(160))
            delay(800)
            espiando.animateTo(0f, tween(160))
            espiaDerecha = !espiaDerecha
        }
    }

    // Parpadea cada cierto tiempo: 1 = ojos abiertos
    val ojos = remember { Animatable(1f) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(2600)
            ojos.animateTo(0.08f, tween(70))
            ojos.animateTo(1f, tween(90))
        }
    }

    val domo = remember { trazo("M38 82a42 42 0 0 1 84 0q0 8-8 8H46q-8 0-8-8z") }
    val brillo = remember { trazo("M54 60a28 28 0 0 1 20-16") }

    Canvas(modifier, contentDescription = "Lumi, la mascota de Mi Semestre") {
        val escala = size.minDimension / 120f
        withTransform({
            scale(escala, escala, pivot = Offset.Zero)
            translate(-20f, -24f)
        }) {
            drawCircle(Estrella, 3f, Offset(70f, 36f))
            drawCircle(Estrella, 3.5f, Offset(80f, 31f))
            drawCircle(Estrella, 3f, Offset(90f, 36f))

            val huecoIzquierdo = if (espiaDerecha) 0f else espiando.value * 4f
            val huecoDerecho = if (espiaDerecha) espiando.value * 4f else 0f
            val cintas: () -> Unit = {
                dibujarCinta(Durazno, 56f, 0f, onda, tapar = tapar, ojoX = 64.4f - huecoIzquierdo)
                dibujarCinta(Cielo, 72f, 1.5f, onda, tapar = tapar, ojoX = 71.6f + huecoIzquierdo)
                dibujarCinta(Rosa, 88f, 3f, onda, tapar = tapar, ojoX = 88.4f - huecoDerecho)
                dibujarCinta(Menta, 104f, 4.5f, onda, saludo, manito, tapar, 95.6f + huecoDerecho)
            }

            // Colgando van detrás de Lumi; para tapar los ojos pasan por delante
            if (tapar == 0f) cintas()

            drawPath(domo, Cupula)
            listOf(48f to 90f, 62f to 91f, 76f to 91f, 90f to 91f, 104f to 91f).forEach { (x, y) ->
                drawCircle(Borde, 6f, Offset(x, y))
            }
            drawCircle(Borde, 5f, Offset(114f, 89f))
            drawPath(
                brillo,
                Blanco,
                alpha = 0.85f,
                style = Stroke(width = 5f, cap = StrokeCap.Round)
            )

            when (cara) {
                CaraLumi.Normal -> {
                    dibujarOjo(68f, ojos.value)
                    dibujarOjo(92f, ojos.value)
                    dibujarBoca("M76 80q4 4 8 0")
                }
                CaraLumi.Feliz -> {
                    dibujarOjoCerrado(68f)
                    dibujarOjoCerrado(92f)
                    drawPath(trazo("M72 78q8 12 16 0z"), Tinta)
                }
                CaraLumi.Guino -> {
                    dibujarOjo(68f, 1f)
                    dibujarOjoCerrado(92f)
                    dibujarBoca("M74 80q6 6 12 0")
                }
                CaraLumi.Sorpresa -> {
                    dibujarOjo(68f, 1.2f)
                    dibujarOjo(92f, 1.2f)
                    drawOval(Tinta, Offset(77f, 79f), Size(6f, 8f))
                }
            }

            drawOval(Rosa, Offset(51f, 76.8f), Size(12f, 6.4f))
            drawOval(Rosa, Offset(97f, 76.8f), Size(12f, 6.4f))

            if (tapar > 0f) cintas()
        }
    }
}

private fun trazo(datos: String): Path = PathParser().parsePathString(datos).toPath()

private fun DrawScope.dibujarCinta(
    color: Color,
    x: Float,
    fase: Float,
    onda: Float,
    saludo: Float = 0f,
    manito: Float = 0f,
    tapar: Float = 0f,
    ojoX: Float = x
) {
    val cinta = Path()
    for (i in 0..14) {
        val t = i / 14f
        // Cinta colgando: se ondula más hacia la punta
        val xCuelga = x + sin(onda + fase + t * 5f) * (1.5f + 5f * t)
        val yCuelga = 94f + 40f * t
        // Cinta levantada para saludar: la punta se mueve de lado a lado
        val xBrazo = 112f + 26f * t + manito * 7f * t * t
        val yBrazo = 88f - 34f * t * t
        val puntoX = xCuelga + (xBrazo - xCuelga) * saludo
        val puntoY = yCuelga + (yBrazo - yCuelga) * saludo
        // Cinta subiendo hasta tapar el ojo
        val xOjo = x + (ojoX - x) * t
        val yOjo = 94f - 34f * t
        val finalX = puntoX + (xOjo - puntoX) * tapar
        val finalY = puntoY + (yOjo - puntoY) * tapar
        if (i == 0) cinta.moveTo(finalX, finalY) else cinta.lineTo(finalX, finalY)
    }
    drawPath(
        cinta,
        color,
        style = Stroke(width = 6f + 2f * tapar, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
}

private fun DrawScope.dibujarOjo(x: Float, abierto: Float) {
    val alto = 18f * abierto
    drawOval(Tinta, Offset(x - 7f, 70f - alto / 2f), Size(14f, alto))
    if (abierto > 0.5f) {
        drawCircle(Blanco, 3f, Offset(x + 2.5f, 66.5f))
        drawCircle(Blanco, 1.4f, Offset(x - 2f, 74f))
    }
}

private fun DrawScope.dibujarOjoCerrado(x: Float) {
    val ojo = Path().apply {
        moveTo(x - 7f, 73f)
        quadraticTo(x, 61f, x + 7f, 73f)
    }
    drawPath(ojo, Tinta, style = Stroke(width = 3.5f, cap = StrokeCap.Round))
}

private fun DrawScope.dibujarBoca(datos: String) {
    drawPath(trazo(datos), Tinta, style = Stroke(width = 2.5f, cap = StrokeCap.Round))
}

@Preview(showBackground = true)
@Composable
fun LumiAnimadaPreview() {
    MiSemestreTheme {
        LumiAnimada(cara = CaraLumi.Feliz, saludando = true, modifier = Modifier.size(190.dp))
    }
}
