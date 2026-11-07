package com.evelynvarela.misemestre.ui.entregas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.evelynvarela.misemestre.R
import com.evelynvarela.misemestre.data.Entrega
import com.evelynvarela.misemestre.ui.componentes.CaraLumi
import com.evelynvarela.misemestre.ui.componentes.LumiAnimada
import com.evelynvarela.misemestre.ui.theme.DuraznoClaro
import com.evelynvarela.misemestre.ui.theme.Lila
import com.evelynvarela.misemestre.ui.theme.LilaClaro
import com.evelynvarela.misemestre.ui.theme.LilaOscuro
import com.evelynvarela.misemestre.ui.theme.LilaTinta
import com.evelynvarela.misemestre.ui.theme.Linea
import com.evelynvarela.misemestre.ui.theme.RosaClaro
import com.evelynvarela.misemestre.ui.theme.TextoSuave
import com.evelynvarela.misemestre.ui.theme.Tinta
import java.time.LocalDate

internal val ColorDurazno = Color(0xFF5C2200)

@Composable
internal fun SemanaActual(
    pendientes: List<Entrega>,
    elegido: LocalDate?,
    onElegir: (LocalDate) -> Unit
) {
    val hoy = LocalDate.now()
    val lunes = hoy.minusDays(hoy.dayOfWeek.value - 1L)
    val letras = listOf("L", "M", "M", "J", "V", "S", "D")

    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        letras.forEachIndexed { indice, letra ->
            val dia = lunes.plusDays(indice.toLong())
            val tieneEntrega = pendientes.any { it.fecha == dia }
            val fondo = when {
                dia == hoy -> Lila
                tieneEntrega -> RosaClaro
                else -> MaterialTheme.colorScheme.surface
            }
            val colorTexto = when {
                dia == hoy -> LilaTinta
                tieneEntrega -> Color(0xFF7A0F3A)
                else -> Tinta
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(fondo)
                    .border(
                        if (dia == elegido) 2.5.dp else 1.5.dp,
                        when {
                            dia == elegido -> Tinta
                            dia == hoy || tieneEntrega -> fondo
                            else -> Linea
                        },
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { onElegir(dia) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = letra,
                    color = if (dia == hoy) LilaOscuro else TextoSuave,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall
                )
                Text("${dia.dayOfMonth}", color = colorTexto, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
internal fun TarjetaCalendario(
    conectado: Boolean,
    ocupado: Boolean,
    mensaje: String?,
    error: String?,
    onConectar: () -> Unit,
    onActualizar: () -> Unit,
    onDesconectar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(LilaClaro)
            .then(if (conectado) Modifier else Modifier.clickable(onClick = onConectar))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = if (conectado) "Calendario conectado" else "Conecta tu calendario",
            color = LilaTinta,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = when {
                ocupado -> "Leyendo tu calendario…"
                error != null -> error
                mensaje != null -> mensaje
                conectado -> "Se actualiza solo cada día"
                else -> "Trae tus fechas de Canvas, Moodle, Google u Outlook"
            },
            color = LilaOscuro,
            style = MaterialTheme.typography.bodySmall
        )
        if (conectado) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(onClick = onActualizar, enabled = !ocupado) {
                    Text("Actualizar", color = LilaTinta, fontWeight = FontWeight.Bold)
                }
                TextButton(onClick = onDesconectar, enabled = !ocupado) {
                    Text("Desconectar", color = LilaTinta, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
internal fun TarjetaMateriasNuevas(cantidad: Int, onCrear: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(DuraznoClaro)
            .clickable(onClick = onCrear)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        LumiAnimada(cara = CaraLumi.Guino, saludando = false, modifier = Modifier.size(44.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = if (cantidad == 1) "Encontré 1 materia nueva" else "Encontré $cantidad materias nuevas",
                color = ColorDurazno,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (cantidad == 1) "Toca para agregarla a tus materias" else "Toca para agregarlas a tus materias",
                color = ColorDurazno,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
internal fun Filtro(texto: String, activo: Boolean, onClick: () -> Unit) {
    Text(
        text = texto,
        color = if (activo) LilaTinta else TextoSuave,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (activo) Lila else Color.Transparent)
            .border(2.dp, if (activo) Lila else Color(0xFFDCD2F5), RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    )
}

@Composable
internal fun AvisoRecordatorio() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 76.dp)
            .background(DuraznoClaro, RoundedCornerShape(18.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_campana),
            contentDescription = null,
            tint = ColorDurazno,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = "Te aviso 1 día antes y el mismo día, desde las 8:00",
            color = ColorDurazno,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
