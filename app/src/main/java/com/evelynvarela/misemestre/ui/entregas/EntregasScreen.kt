package com.evelynvarela.misemestre.ui.entregas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.evelynvarela.misemestre.R
import com.evelynvarela.misemestre.data.EntregaEjemplo
import com.evelynvarela.misemestre.data.entregasEjemplo
import com.evelynvarela.misemestre.data.textoDias
import com.evelynvarela.misemestre.data.textoFecha
import com.evelynvarela.misemestre.ui.componentes.Pildora
import com.evelynvarela.misemestre.ui.theme.Bricolage
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

private val ColorDurazno = Color(0xFF5C2200)

@Composable
fun EntregasScreen(modifier: Modifier = Modifier) {
    val entregadas = remember {
        mutableStateListOf(*entregasEjemplo.filter { it.entregada }.map { it.id }.toTypedArray())
    }
    var verEntregadas by remember { mutableStateOf(false) }

    val (listas, pendientes) = entregasEjemplo.partition { it.id in entregadas }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Entregas",
                fontFamily = Bricolage,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 30.sp
            )

            SemanaActual(pendientes)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Filtro("Pendientes · ${pendientes.size}", !verEntregadas) { verEntregadas = false }
                Filtro("Entregadas · ${listas.size}", verEntregadas) { verEntregadas = true }
            }

            (if (verEntregadas) listas else pendientes).sortedBy { it.fecha }.forEach { entrega ->
                val hecha = entrega.id in entregadas
                FilaEntrega(
                    entrega = entrega,
                    hecha = hecha,
                    onCambiar = {
                        if (hecha) entregadas.remove(entrega.id) else entregadas.add(entrega.id)
                    }
                )
            }

            AvisoRecordatorio()
        }

        // El formulario de nueva entrega llega con los recordatorios
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .size(60.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(Lila)
                .clickable { },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_agregar),
                contentDescription = "Nueva entrega",
                tint = LilaTinta,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun SemanaActual(pendientes: List<EntregaEjemplo>) {
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
                    .background(fondo, RoundedCornerShape(16.dp))
                    .border(
                        1.5.dp,
                        if (dia == hoy || tieneEntrega) fondo else Linea,
                        RoundedCornerShape(16.dp)
                    ),
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
private fun Filtro(texto: String, activo: Boolean, onClick: () -> Unit) {
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
private fun FilaEntrega(entrega: EntregaEjemplo, hecha: Boolean, onCambiar: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
            .border(1.5.dp, Linea, RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (hecha) Lila else Color.Transparent)
                .border(2.5.dp, if (hecha) Lila else Color(0xFF8B6CF0), RoundedCornerShape(14.dp))
                .clickable(onClick = onCambiar),
            contentAlignment = Alignment.Center
        ) {
            if (hecha) {
                Icon(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = "Marcar pendiente: ${entrega.nombre}",
                    tint = LilaTinta,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Column(Modifier.weight(1f)) {
            Text(
                text = entrega.nombre,
                fontWeight = FontWeight.Bold,
                color = if (hecha) TextoSuave else Tinta,
                textDecoration = if (hecha) TextDecoration.LineThrough else null
            )
            Text(
                text = "${entrega.materia} · ${textoFecha(entrega.fecha)}",
                color = TextoSuave,
                style = MaterialTheme.typography.bodySmall
            )
        }

        when {
            hecha -> Pildora("Lista", LilaClaro, Tinta)
            entrega.urgente -> Pildora(textoDias(entrega.fecha), Fucsia, Color(0xFF3D0718))
            else -> Pildora(textoDias(entrega.fecha), DuraznoClaro, ColorDurazno)
        }
    }
}

@Composable
private fun AvisoRecordatorio() {
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
            text = "Te aviso 1 día antes y el mismo día a las 8:00",
            color = ColorDurazno,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
fun EntregasPreview() {
    MiSemestreTheme {
        EntregasScreen()
    }
}
