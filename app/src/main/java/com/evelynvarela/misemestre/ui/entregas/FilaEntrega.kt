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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.evelynvarela.misemestre.R
import com.evelynvarela.misemestre.data.Entrega
import com.evelynvarela.misemestre.data.esUrgente
import com.evelynvarela.misemestre.data.textoDias
import com.evelynvarela.misemestre.data.textoFecha
import com.evelynvarela.misemestre.ui.componentes.Pildora
import com.evelynvarela.misemestre.ui.theme.DuraznoClaro
import com.evelynvarela.misemestre.ui.theme.Fucsia
import com.evelynvarela.misemestre.ui.theme.Lila
import com.evelynvarela.misemestre.ui.theme.LilaClaro
import com.evelynvarela.misemestre.ui.theme.LilaTinta
import com.evelynvarela.misemestre.ui.theme.Linea
import com.evelynvarela.misemestre.ui.theme.TextoSuave
import com.evelynvarela.misemestre.ui.theme.Tinta

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FilaDeslizable(
    entrega: Entrega,
    onCambiar: () -> Unit,
    contenido: @Composable () -> Unit
) {
    val estado = rememberSwipeToDismissBoxState(
        confirmValueChange = { valor ->
            if (valor == SwipeToDismissBoxValue.StartToEnd) onCambiar()
            false
        }
    )

    SwipeToDismissBox(
        state = estado,
        enableDismissFromEndToStart = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Lila, RoundedCornerShape(20.dp))
                    .padding(start = 22.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = if (entrega.entregada) "Pendiente" else "¡Lista!",
                    color = LilaTinta,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) {
        contenido()
    }
}

@Composable
internal fun FilaEntrega(
    entrega: Entrega,
    onEditar: () -> Unit,
    onCambiar: () -> Unit,
    onBorrar: () -> Unit
) {
    val hecha = entrega.entregada
    val accion = if (hecha) "Marcar pendiente" else "Marcar lista"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.5.dp, Linea, RoundedCornerShape(20.dp))
            .clickable(onClick = onEditar)
            .padding(start = 14.dp, end = 4.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (hecha) Lila else Color.Transparent)
                .border(2.5.dp, if (hecha) Lila else Color(0xFF8B6CF0), RoundedCornerShape(14.dp))
                .semantics { contentDescription = "$accion: ${entrega.nombre}" }
                .clickable(onClick = onCambiar),
            contentAlignment = Alignment.Center
        ) {
            if (hecha) {
                Icon(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = null,
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
            esUrgente(entrega.fecha) -> Pildora(textoDias(entrega.fecha), Fucsia, Color(0xFF3D0718))
            else -> Pildora(textoDias(entrega.fecha), DuraznoClaro, ColorDurazno)
        }

        IconButton(
            onClick = onBorrar,
            modifier = Modifier
                .size(36.dp)
                .semantics { contentDescription = "Borrar: ${entrega.nombre}" }
        ) {
            Text("✕", color = TextoSuave)
        }
    }
}
