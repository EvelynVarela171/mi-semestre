package com.evelynvarela.misemestre.ui.asistente

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.evelynvarela.misemestre.R
import com.evelynvarela.misemestre.ui.componentes.CaraLumi
import com.evelynvarela.misemestre.ui.componentes.LumiAnimada
import com.evelynvarela.misemestre.ui.theme.Bricolage
import com.evelynvarela.misemestre.ui.theme.DuraznoClaro
import com.evelynvarela.misemestre.ui.theme.Lila
import com.evelynvarela.misemestre.ui.theme.LilaTinta
import com.evelynvarela.misemestre.ui.theme.Linea
import com.evelynvarela.misemestre.ui.theme.MiSemestreTheme
import com.evelynvarela.misemestre.ui.theme.TextoSuave

private val sugerencias = listOf(
    "¿Qué debo priorizar esta semana?" to Lila,
    "¿Cómo voy en mis materias?" to Color(0xFFDCD2F5),
    "Explícame Room" to Color(0xFFDCD2F5)
)

@Composable
fun AsistenteScreen(
    modifier: Modifier = Modifier,
    viewModel: AsistenteViewModel = viewModel()
) {
    AsistenteContenido(
        mensajes = viewModel.mensajes,
        pensando = viewModel.pensando,
        onEnviar = viewModel::enviar,
        modifier = modifier
    )
}

@Composable
private fun AsistenteContenido(
    mensajes: List<Mensaje>,
    pensando: Boolean,
    onEnviar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var pregunta by remember { mutableStateOf("") }
    val desplazamiento = rememberScrollState()

    LaunchedEffect(mensajes.size, mensajes.lastOrNull(), pensando) {
        desplazamiento.animateScrollTo(desplazamiento.maxValue)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Encabezado(pensando)

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(desplazamiento),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            mensajes.forEach { mensaje ->
                if (mensaje.deLumi) {
                    BurbujaLumi(mensaje.texto, mensaje.esError)
                } else {
                    BurbujaUsuario(mensaje.texto)
                }
            }
            if (pensando) BurbujaLumi("Lumi está pensando…", esError = false)
            if (mensajes.size == 1) Sugerencias(onEnviar)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = pregunta,
                onValueChange = { pregunta = it },
                placeholder = { Text("Pregúntale algo a Lumi") },
                singleLine = true,
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Linea
                ),
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (pensando) Linea else Lila)
                    .clickable(enabled = !pensando) {
                        onEnviar(pregunta)
                        pregunta = ""
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_enviar),
                    contentDescription = "Enviar",
                    tint = LilaTinta
                )
            }
        }
    }
}

@Composable
private fun Encabezado(pensando: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(DuraznoClaro, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            LumiAnimada(
                cara = if (pensando) CaraLumi.Sorpresa else CaraLumi.Normal,
                saludando = pensando,
                modifier = Modifier.size(44.dp)
            )
        }
        Column {
            Text(
                text = "Lumi",
                fontFamily = Bricolage,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 26.sp,
                lineHeight = 29.sp
            )
            Text(
                text = "Tu asistente · con Gemini",
                color = TextoSuave,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun BurbujaUsuario(texto: String) {
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
        Text(
            text = texto,
            color = LilaTinta,
            modifier = Modifier
                .fillMaxWidth(0.78f)
                .wrapContentWidth(Alignment.End)
                .background(Lila, RoundedCornerShape(22.dp, 22.dp, 6.dp, 22.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )
    }
}

@Composable
private fun BurbujaLumi(texto: String, esError: Boolean) {
    Text(
        text = texto,
        color = if (esError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .wrapContentWidth(Alignment.Start)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(22.dp, 22.dp, 22.dp, 6.dp))
            .border(1.5.dp, Linea, RoundedCornerShape(22.dp, 22.dp, 22.dp, 6.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp)
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Sugerencias(onEnviar: (String) -> Unit) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        sugerencias.forEach { (texto, borde) ->
            Text(
                text = texto,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .border(2.dp, borde, RoundedCornerShape(50))
                    .clickable { onEnviar(texto) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
fun AsistentePreview() {
    MiSemestreTheme {
        AsistenteContenido(
            mensajes = listOf(Mensaje("¡Hola! ¿En qué te ayudo hoy?", deLumi = true)),
            pensando = false,
            onEnviar = {}
        )
    }
}
