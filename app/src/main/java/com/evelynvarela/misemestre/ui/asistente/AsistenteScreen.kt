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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.evelynvarela.misemestre.R
import com.evelynvarela.misemestre.ui.componentes.CaraLumi
import com.evelynvarela.misemestre.ui.componentes.LumiAnimada
import com.evelynvarela.misemestre.ui.theme.Bricolage
import com.evelynvarela.misemestre.ui.theme.DuraznoClaro
import com.evelynvarela.misemestre.ui.theme.Lila
import com.evelynvarela.misemestre.ui.theme.LilaClaro
import com.evelynvarela.misemestre.ui.theme.LilaTinta
import com.evelynvarela.misemestre.ui.theme.Linea
import com.evelynvarela.misemestre.ui.theme.MiSemestreTheme
import com.evelynvarela.misemestre.ui.theme.RosaClaro
import com.evelynvarela.misemestre.ui.theme.TextoSuave
import com.evelynvarela.misemestre.ui.theme.Tinta

private data class PasoPlan(val dia: String, val tarea: String, val fondo: Color, val texto: Color)

private val planEjemplo = listOf(
    PasoPlan("LUN", "Terminar Firebase y probar login", LilaClaro, Tinta),
    PasoPlan("MAR", "README con diagramas", LilaClaro, Tinta),
    PasoPlan("MIÉ", "Diapositivas y guion", DuraznoClaro, Color(0xFF5C2200)),
    PasoPlan("JUE", "Grabar el video de 5 a 10 min", RosaClaro, Color(0xFF7A0F3A))
)

@Composable
fun AsistenteScreen(modifier: Modifier = Modifier) {
    var pregunta by remember { mutableStateOf("") }
    val enviados = remember { mutableStateListOf<String>() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Encabezado()

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            BurbujaUsuario("¿Qué debo priorizar esta semana?")
            BurbujaPlan()
            Sugerencias()
            enviados.forEach { BurbujaUsuario(it) }
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
                    .background(Lila)
                    .clickable {
                        if (pregunta.isNotBlank()) {
                            enviados.add(pregunta.trim())
                            pregunta = ""
                        }
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
private fun Encabezado() {
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
            LumiAnimada(cara = CaraLumi.Normal, saludando = false, modifier = Modifier.size(44.dp))
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
private fun BurbujaPlan() {
    Column(
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(22.dp, 22.dp, 22.dp, 6.dp))
            .border(1.5.dp, Linea, RoundedCornerShape(22.dp, 22.dp, 22.dp, 6.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Tienes 2 entregas el 16 de noviembre. Te propongo esto:")

        planEjemplo.forEach { paso ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = paso.dia,
                    color = paso.texto,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .background(paso.fondo, RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                )
                Text(paso.tarea, fontWeight = FontWeight.Bold)
            }
        }

        Text(
            text = "Basado en tus entregas guardadas",
            color = TextoSuave,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Sugerencias() {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf("Pásalo a mis entregas" to Lila, "Explícame Room" to Color(0xFFDCD2F5)).forEach { (texto, borde) ->
            Text(
                text = texto,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .border(2.dp, borde, RoundedCornerShape(50))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
fun AsistentePreview() {
    MiSemestreTheme {
        AsistenteScreen()
    }
}
