package com.evelynvarela.misemestre.ui.inicio

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.evelynvarela.misemestre.R
import com.evelynvarela.misemestre.data.EntregaEjemplo
import com.evelynvarela.misemestre.data.Materia
import com.evelynvarela.misemestre.data.entregasEjemplo
import com.evelynvarela.misemestre.data.textoDias
import com.evelynvarela.misemestre.ui.componentes.CaraLumi
import com.evelynvarela.misemestre.ui.componentes.LumiAnimada
import com.evelynvarela.misemestre.ui.componentes.Pildora
import com.evelynvarela.misemestre.ui.materias.TarjetaMateria
import com.evelynvarela.misemestre.ui.theme.Bricolage
import com.evelynvarela.misemestre.ui.theme.Durazno
import com.evelynvarela.misemestre.ui.theme.DuraznoClaro
import com.evelynvarela.misemestre.ui.theme.Fucsia
import com.evelynvarela.misemestre.ui.theme.Lila
import com.evelynvarela.misemestre.ui.theme.LilaClaro
import com.evelynvarela.misemestre.ui.theme.LilaOscuro
import com.evelynvarela.misemestre.ui.theme.LilaTinta
import com.evelynvarela.misemestre.ui.theme.Linea
import com.evelynvarela.misemestre.ui.theme.MiSemestreTheme
import com.evelynvarela.misemestre.ui.theme.RosaClaro
import com.evelynvarela.misemestre.ui.theme.TextoSuave
import com.evelynvarela.misemestre.ui.theme.Tinta
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun InicioScreen(
    onAbrirMateria: (Int) -> Unit,
    onVerMaterias: () -> Unit,
    onVerEntregas: () -> Unit,
    onVerAsistente: () -> Unit,
    onVerPerfil: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InicioViewModel = viewModel()
) {
    val materias by viewModel.materias.collectAsState()

    InicioContenido(
        materias = materias,
        onAbrirMateria = onAbrirMateria,
        onVerMaterias = onVerMaterias,
        onVerEntregas = onVerEntregas,
        onVerAsistente = onVerAsistente,
        onVerPerfil = onVerPerfil,
        modifier = modifier
    )
}

@Composable
private fun InicioContenido(
    materias: List<Materia>?,
    onAbrirMateria: (Int) -> Unit,
    onVerMaterias: () -> Unit,
    onVerEntregas: () -> Unit,
    onVerAsistente: () -> Unit,
    onVerPerfil: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            Modifier
                .align(Alignment.TopEnd)
                .offset(x = 70.dp, y = (-60).dp)
                .size(220.dp)
                .background(LilaClaro, RoundedCornerShape(46, 54, 60, 40))
        )
        Box(
            Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-40).dp, y = 90.dp)
                .size(60.dp)
                .background(DuraznoClaro, CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Encabezado(onVerPerfil)
            ChipNube()
            TarjetaPromedio()
            SeccionMaterias(materias, onAbrirMateria, onVerMaterias)
            entregasEjemplo
                .filterNot { it.entregada }
                .minByOrNull { it.fecha }
                ?.let { SeccionLoQueViene(it, onVerEntregas) }
            TarjetaLumi(onVerAsistente)
        }
    }
}

@Composable
private fun Encabezado(onVerPerfil: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(
                text = fechaDeHoy(),
                fontWeight = FontWeight.Bold,
                color = TextoSuave
            )
            Text(
                text = "¡Hola, Evelyn!",
                fontFamily = Bricolage,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 32.sp,
                lineHeight = 36.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(2.dp, Tinta, RoundedCornerShape(18.dp))
                .clickable(onClick = onVerPerfil),
            contentAlignment = Alignment.Center
        ) {
            Text("EV", fontFamily = Bricolage, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
        }
    }
}

@Composable
private fun ChipNube() {
    Row(
        modifier = Modifier
            .background(LilaClaro, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_check),
            contentDescription = null,
            tint = Tinta,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = "Todo sincronizado en la nube",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun TarjetaPromedio() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Lila, RoundedCornerShape(28.dp))
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text("Tu semestre va en", fontWeight = FontWeight.Bold, color = LilaOscuro)
            Text(
                text = "4.2",
                fontFamily = Bricolage,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 44.sp,
                lineHeight = 48.sp,
                color = LilaTinta
            )
            Text(
                text = "promedio · meta 4.5",
                color = LilaOscuro,
                style = MaterialTheme.typography.bodySmall
            )
        }
        ProgresoSemestre(progreso = 0.78f)
    }
}

@Composable
private fun ProgresoSemestre(progreso: Float) {
    Box(Modifier.size(76.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val grosor = 10.dp.toPx()
            val tamano = Size(size.width - grosor, size.height - grosor)
            val inicio = Offset(grosor / 2, grosor / 2)
            drawArc(
                color = Color(0xFFC9B8FD),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = inicio,
                size = tamano,
                style = Stroke(grosor)
            )
            drawArc(
                color = Tinta,
                startAngle = -90f,
                sweepAngle = 360f * progreso,
                useCenter = false,
                topLeft = inicio,
                size = tamano,
                style = Stroke(grosor)
            )
        }
        Text("${(progreso * 100).toInt()}%", fontWeight = FontWeight.Bold, color = LilaTinta)
    }
}

@Composable
private fun EncabezadoSeccion(titulo: String, accion: String, onAccion: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = titulo,
            fontFamily = Bricolage,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = accion,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onAccion)
                .padding(4.dp)
        )
    }
}

@Composable
private fun SeccionMaterias(
    materias: List<Materia>?,
    onAbrirMateria: (Int) -> Unit,
    onVerMaterias: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        EncabezadoSeccion("Mis materias", "Ver todas", onVerMaterias)

        if (materias != null && materias.isEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.5.dp, Linea, RoundedCornerShape(20.dp))
                    .clickable(onClick = onVerMaterias)
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Aún no tienes materias", fontWeight = FontWeight.Bold)
                    Text(
                        text = "Toca aquí para agregar la primera",
                        color = TextoSuave,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Icon(
                    painter = painterResource(R.drawable.ic_siguiente),
                    contentDescription = null,
                    tint = Tinta
                )
            }
        }

        materias.orEmpty().take(4).chunked(2).forEach { fila ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                fila.forEach { materia ->
                    TarjetaMateria(
                        materia = materia,
                        onAbrir = { onAbrirMateria(materia.id) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (fila.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun SeccionLoQueViene(entrega: EntregaEjemplo, onVerEntregas: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        EncabezadoSeccion("Lo que viene", "Ver todo", onVerEntregas)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
                .border(1.5.dp, Linea, RoundedCornerShape(20.dp))
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .size(46.dp)
                    .background(RosaClaro, RoundedCornerShape(14.dp)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${entrega.fecha.dayOfMonth}",
                    fontWeight = FontWeight.Bold,
                    lineHeight = 18.sp,
                    color = ColorRosaOscuro
                )
                Text(
                    text = mesCorto(entrega.fecha),
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    lineHeight = 12.sp,
                    color = ColorRosaOscuro
                )
            }
            Column(Modifier.weight(1f)) {
                Text(entrega.nombre, fontWeight = FontWeight.Bold)
                Text(
                    text = entrega.materia,
                    color = TextoSuave,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Pildora(textoDias(entrega.fecha), Fucsia, Color(0xFF3D0718))
        }
    }
}

@Composable
private fun TarjetaLumi(onVerAsistente: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(DuraznoClaro)
            .clickable(onClick = onVerAsistente)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        LumiAnimada(cara = CaraLumi.Normal, saludando = false, modifier = Modifier.size(46.dp))
        Column(Modifier.weight(1f)) {
            Text("Lumi tiene un plan para hoy", fontWeight = FontWeight.Bold, color = ColorDurazno)
            Text(
                text = "Toca para ver qué priorizar",
                color = ColorDurazno,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Icon(
            painter = painterResource(R.drawable.ic_siguiente),
            contentDescription = null,
            tint = ColorDurazno
        )
    }
}

private val ColorRosaOscuro = Color(0xFF7A0F3A)
private val ColorDurazno = Color(0xFF5C2200)

private fun mesCorto(fecha: LocalDate): String {
    val formato = DateTimeFormatter.ofPattern("MMM", Locale.forLanguageTag("es"))
    return fecha.format(formato).replace(".", "").uppercase()
}

private fun fechaDeHoy(): String {
    val formato = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", Locale.forLanguageTag("es"))
    return LocalDate.now().format(formato).replaceFirstChar { it.uppercase() }
}

@Preview(showBackground = true)
@Composable
fun InicioPreview() {
    MiSemestreTheme {
        InicioContenido(
            materias = listOf(
                Materia(id = 1, nombre = "Apps móviles", creditos = 3),
                Materia(id = 2, nombre = "Bases de datos", creditos = 3)
            ),
            onAbrirMateria = {},
            onVerMaterias = {},
            onVerEntregas = {},
            onVerAsistente = {},
            onVerPerfil = {}
        )
    }
}
