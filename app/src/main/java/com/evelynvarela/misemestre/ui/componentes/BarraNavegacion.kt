package com.evelynvarela.misemestre.ui.componentes

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.evelynvarela.misemestre.R
import com.evelynvarela.misemestre.ui.theme.Lila
import com.evelynvarela.misemestre.ui.theme.LilaTinta
import com.evelynvarela.misemestre.ui.theme.Tinta

private data class Pestana(val ruta: String, val nombre: String, @param:DrawableRes val icono: Int)

private val pestanas = listOf(
    Pestana("inicio", "Inicio", R.drawable.ic_inicio),
    Pestana("materias", "Materias", R.drawable.ic_materias),
    Pestana("asistente", "Asistente Lumi", R.drawable.ic_asistente),
    Pestana("entregas", "Entregas", R.drawable.ic_entregas)
)

private val IconoInactivo = Color(0xFFCFC6F0)

@Composable
fun BarraNavegacion(
    rutaActiva: String?,
    onIrA: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp)
            .background(Tinta, RoundedCornerShape(24.dp)),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        pestanas.forEach { pestana ->
            val activa = pestana.ruta == rutaActiva
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (activa) Lila else Color.Transparent)
                    .clickable { onIrA(pestana.ruta) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(pestana.icono),
                    contentDescription = pestana.nombre,
                    tint = if (activa) LilaTinta else IconoInactivo
                )
            }
        }
    }
}
