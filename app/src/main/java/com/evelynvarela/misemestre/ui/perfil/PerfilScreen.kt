package com.evelynvarela.misemestre.ui.perfil

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.evelynvarela.misemestre.R
import com.evelynvarela.misemestre.data.entregasEjemplo
import com.evelynvarela.misemestre.data.perfilEjemplo
import com.evelynvarela.misemestre.ui.componentes.CaraLumi
import com.evelynvarela.misemestre.ui.componentes.LumiAnimada
import com.evelynvarela.misemestre.ui.componentes.Pildora
import com.evelynvarela.misemestre.ui.inicio.InicioViewModel
import com.evelynvarela.misemestre.ui.theme.Blanco
import com.evelynvarela.misemestre.ui.theme.Bricolage
import com.evelynvarela.misemestre.ui.theme.Durazno
import com.evelynvarela.misemestre.ui.theme.DuraznoClaro
import com.evelynvarela.misemestre.ui.theme.Lila
import com.evelynvarela.misemestre.ui.theme.LilaClaro
import com.evelynvarela.misemestre.ui.theme.LilaOscuro
import com.evelynvarela.misemestre.ui.theme.Linea
import com.evelynvarela.misemestre.ui.theme.MiSemestreTheme
import com.evelynvarela.misemestre.ui.theme.RosaClaro
import com.evelynvarela.misemestre.ui.theme.TextoSuave
import com.evelynvarela.misemestre.ui.theme.Tinta
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin

private data class Nivel(val nombre: String, val desde: Int)

// Lumi crece con los puntos: 10 por materia y 20 por entrega lista
private val niveles = listOf(
    Nivel("Huevito", 0),
    Nivel("Medusita", 30),
    Nivel("Medusa estudiosa", 80),
    Nivel("Reina del semestre", 150)
)

private data class Logro(val emoji: String, val nombre: String, val ganado: Boolean)

@Composable
fun PerfilScreen(
    onVolver: () -> Unit,
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InicioViewModel = viewModel()
) {
    val materias by viewModel.materias.collectAsState()
    var confirmando by remember { mutableStateOf(false) }

    PerfilContenido(
        totalMaterias = materias.orEmpty().size,
        onVolver = onVolver,
        onCerrarSesion = { confirmando = true },
        modifier = modifier
    )

    if (confirmando) {
        AlertDialog(
            onDismissRequest = { confirmando = false },
            containerColor = MaterialTheme.colorScheme.surface,
            icon = {
                LumiAnimada(
                    cara = CaraLumi.Sorpresa,
                    saludando = true,
                    modifier = Modifier.size(90.dp)
                )
            },
            title = { Text("¿Ya te vas?") },
            text = { Text("Lumi te va a esperar aquí en su acuario.") },
            confirmButton = {
                Button(
                    onClick = {
                        confirmando = false
                        onCerrarSesion()
                    }
                ) {
                    Text("Cerrar sesión")
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmando = false }) {
                    Text("Quedarme", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        )
    }
}

@Composable
private fun PerfilContenido(
    totalMaterias: Int,
    onVolver: () -> Unit,
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val entregasListas = entregasEjemplo.count { it.entregada }
    val puntos = totalMaterias * 10 + entregasListas * 20
    val nivel = niveles.indexOfLast { puntos >= it.desde }
    val logros = listOf(
        Logro("🌱", "Primera materia", totalMaterias >= 1),
        Logro("📬", "Primera entrega", entregasListas >= 1),
        Logro("📚", "Cuatro materias", totalMaterias >= 4),
        Logro("👑", "Reina del semestre", nivel == niveles.lastIndex)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.5.dp, Linea, RoundedCornerShape(14.dp))
                    .clickable(onClick = onVolver),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_volver),
                    contentDescription = "Volver",
                    tint = Tinta
                )
            }
            Text(
                text = "Mi perfil",
                fontFamily = Bricolage,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 26.sp
            )
        }

        Acuario(nivel)
        ProgresoNivel(puntos, nivel)
        Carne()

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Dato("$totalMaterias", "Materias", Modifier.weight(1f))
            Dato("$entregasListas", "Entregas listas", Modifier.weight(1f))
            Dato("$puntos", "Puntos", Modifier.weight(1f))
        }

        Text(
            text = "Logros",
            fontFamily = Bricolage,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            logros.forEach { InsigniaLogro(it, Modifier.weight(1f)) }
        }

        Button(
            onClick = onCerrarSesion,
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = RosaClaro,
                contentColor = Color(0xFF7A0F3A)
            ),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Cerrar sesión", fontWeight = FontWeight.Bold)
        }
    }
}

// La casa de Lumi: tiene más burbujas a medida que sube de nivel
@Composable
private fun Acuario(nivel: Int) {
    var caraLumi by remember { mutableStateOf(CaraLumi.Normal) }
    var lumiSaluda by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val agua = rememberInfiniteTransition(label = "agua")
    val marea by agua.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(7000, easing = LinearEasing)),
        label = "marea"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFFBFE8FF), LilaClaro)))
    ) {
        Canvas(Modifier.matchParentSize()) {
            // La arena del fondo
            drawOval(
                color = Durazno.copy(alpha = 0.55f),
                topLeft = Offset(-size.width * 0.1f, size.height * 0.84f),
                size = Size(size.width * 1.2f, size.height * 0.4f)
            )

            repeat(4 + nivel * 3) { i ->
                val avance = (marea * 2 + i * 0.29f) % 1f
                val lado = sin(avance * 12f + i) * 6.dp.toPx()
                drawCircle(
                    color = Blanco.copy(alpha = 0.75f * (1f - avance)),
                    radius = (3 + i % 3 * 2).dp.toPx(),
                    center = Offset(
                        size.width * ((i * 0.37f + 0.08f) % 1f) + lado,
                        size.height * (1f - avance)
                    )
                )
            }
        }

        Pildora(
            texto = "Nivel ${nivel + 1} · ${niveles[nivel].nombre}",
            fondo = Blanco,
            colorTexto = LilaOscuro,
            modifier = Modifier.align(Alignment.TopStart).padding(12.dp)
        )

        LumiAnimada(
            cara = caraLumi,
            saludando = lumiSaluda,
            modifier = Modifier
                .align(Alignment.Center)
                .offset {
                    IntOffset(0, (sin(marea * 4 * PI.toFloat()) * 8.dp.toPx()).roundToInt())
                }
                .size(150.dp)
                .clickable(interactionSource = null, indication = null) {
                    scope.launch {
                        caraLumi = CaraLumi.Sorpresa
                        delay(350)
                        caraLumi = CaraLumi.Feliz
                        lumiSaluda = true
                        delay(1400)
                        lumiSaluda = false
                        caraLumi = CaraLumi.Normal
                    }
                }
        )
    }
}

@Composable
private fun ProgresoNivel(puntos: Int, nivel: Int) {
    val siguiente = niveles.getOrNull(nivel + 1)
    val desde = niveles[nivel].desde
    val avance = if (siguiente == null) 1f else (puntos - desde).toFloat() / (siguiente.desde - desde)

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .background(LilaClaro, RoundedCornerShape(50))
        ) {
            Box(
                Modifier
                    .fillMaxWidth(avance)
                    .fillMaxHeight()
                    .background(Lila, RoundedCornerShape(50))
            )
        }
        Text(
            text = if (siguiente == null) {
                "Lumi llegó al nivel máximo"
            } else {
                "Te faltan ${siguiente.desde - puntos} puntos para ${siguiente.nombre}"
            },
            color = TextoSuave,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun Carne() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Tinta, RoundedCornerShape(24.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(DuraznoClaro, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = perfilEjemplo.iniciales,
                color = Tinta,
                fontFamily = Bricolage,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp
            )
        }
        Column {
            Text(
                text = "CARNÉ · ${perfilEjemplo.universidad}",
                color = Color(0xFFCFC6F0),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = perfilEjemplo.nombre,
                color = Blanco,
                fontFamily = Bricolage,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp
            )
            Text(perfilEjemplo.correo, color = Color(0xFFCFC6F0))
            Text(
                text = "Semestre ${perfilEjemplo.semestre}",
                color = Color(0xFFCFC6F0),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun Dato(valor: String, nombre: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(18.dp))
            .border(1.5.dp, Linea, RoundedCornerShape(18.dp))
            .padding(vertical = 12.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = valor,
            fontFamily = Bricolage,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 24.sp
        )
        Text(
            text = nombre,
            color = TextoSuave,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun InsigniaLogro(logro: Logro, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(if (logro.ganado) DuraznoClaro else Linea, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(if (logro.ganado) logro.emoji else "🔒", fontSize = 24.sp)
        }
        Text(
            text = logro.nombre,
            color = if (logro.ganado) Tinta else TextoSuave,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Preview(showBackground = true, heightDp = 1000)
@Composable
fun PerfilPreview() {
    MiSemestreTheme {
        PerfilContenido(totalMaterias = 2, onVolver = {}, onCerrarSesion = {})
    }
}
