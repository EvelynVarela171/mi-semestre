package com.evelynvarela.misemestre.ui.bienvenida

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.evelynvarela.misemestre.ui.componentes.CaraLumi
import com.evelynvarela.misemestre.ui.componentes.LumiAnimada
import com.evelynvarela.misemestre.ui.theme.MiSemestreTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BienvenidaScreen(onTerminar: () -> Unit, modifier: Modifier = Modifier) {
    val recorrido = remember { Animatable(0f) }
    val salto = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    var cara by remember { mutableStateOf(CaraLumi.Normal) }
    var saludando by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf(false) }

    val flotar = rememberInfiniteTransition(label = "flotar")
    val subeBaja by flotar.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            tween(900, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "subeBaja"
    )
    val balanceo by flotar.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            tween(1300, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "balanceo"
    )

    LaunchedEffect(Unit) {
        delay(400)
        cara = CaraLumi.Feliz
        saludando = true
        mensaje = true
        delay(1900)
        saludando = false
        cara = CaraLumi.Guino
        delay(800)
        mensaje = false
        cara = CaraLumi.Feliz
        recorrido.animateTo(-0.12f, tween(450, easing = FastOutSlowInEasing))
        recorrido.animateTo(1f, tween(850, easing = FastOutSlowInEasing))
        onTerminar()
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AnimatedVisibility(
            visible = mensaje,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-150).dp)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(18.dp),
                shadowElevation = 3.dp
            ) {
                Text(
                    text = "¡Hola! Soy Lumi",
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        LumiAnimada(
            cara = cara,
            saludando = saludando,
            modifier = Modifier
                .align(Alignment.Center)
                .size(190.dp)
                .graphicsLayer {
                    translationX = recorrido.value * (maxWidth + 190.dp).toPx()
                    translationY = (subeBaja + salto.value).dp.toPx()
                    rotationZ = balanceo
                }
                .clickable(interactionSource = null, indication = null) {
                    cara = CaraLumi.Sorpresa
                    scope.launch {
                        salto.animateTo(-80f, tween(180))
                        salto.animateTo(
                            0f,
                            spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                        )
                        cara = CaraLumi.Feliz
                    }
                }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BienvenidaPreview() {
    MiSemestreTheme {
        BienvenidaScreen(onTerminar = {})
    }
}
