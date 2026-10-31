package com.evelynvarela.misemestre.ui.materia

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
import com.evelynvarela.misemestre.data.Materia
import com.evelynvarela.misemestre.data.NotaEjemplo
import com.evelynvarela.misemestre.data.necesitasEjemplo
import com.evelynvarela.misemestre.data.notasEjemplo
import com.evelynvarela.misemestre.ui.inicio.InicioViewModel
import com.evelynvarela.misemestre.ui.materias.colorDeMateria
import com.evelynvarela.misemestre.ui.theme.Bricolage
import com.evelynvarela.misemestre.ui.theme.Lila
import com.evelynvarela.misemestre.ui.theme.LilaClaro
import com.evelynvarela.misemestre.ui.theme.LilaOscuro
import com.evelynvarela.misemestre.ui.theme.LilaTinta
import com.evelynvarela.misemestre.ui.theme.Linea
import com.evelynvarela.misemestre.ui.theme.MiSemestreTheme
import com.evelynvarela.misemestre.ui.theme.TextoSuave
import com.evelynvarela.misemestre.ui.theme.Tinta

@Composable
fun MateriaScreen(
    materiaId: Int,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InicioViewModel = viewModel()
) {
    val materias by viewModel.materias.collectAsState()

    materias?.find { it.id == materiaId }?.let { materia ->
        MateriaContenido(materia, onVolver, modifier)
    }
}

@Composable
private fun MateriaContenido(
    materia: Materia,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            Text("Materias", fontWeight = FontWeight.Bold, color = TextoSuave)
        }

        TarjetaMateriaGrande(materia)

        Text(
            text = "Notas",
            fontFamily = Bricolage,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp
        )
        notasEjemplo.forEach { FilaNota(it) }

        Calculadora()
    }
}

@Composable
private fun TarjetaMateriaGrande(materia: Materia) {
    val (fondo, texto) = colorDeMateria(materia.id)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(fondo)
    ) {
        Box(
            Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 40.dp, y = 50.dp)
                .size(150.dp)
                .background(texto.copy(alpha = 0.08f), CircleShape)
        )
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "${materia.creditos} créditos · nota mínima 3.0",
                color = texto,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = materia.nombre,
                color = texto,
                fontFamily = Bricolage,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 28.sp,
                lineHeight = 31.sp
            )
            Text(
                text = "Llevas 4.8 en el 40% evaluado",
                color = texto,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun FilaNota(nota: NotaEjemplo) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .border(1.5.dp, Linea, RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(nota.nombre, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        Text(
            text = "${nota.peso}%",
            color = TextoSuave,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .background(Color(0xFFF1ECFD), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp)
        )
        Text(
            text = nota.valor?.toString() ?: "—",
            fontFamily = Bricolage,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp,
            color = if (nota.valor == null) Color(0xFF9590B0) else Tinta
        )
    }
}

@Composable
private fun Calculadora() {
    var meta by remember { mutableStateOf(3.0) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LilaClaro, RoundedCornerShape(24.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("¿Qué nota quieres sacar?", fontWeight = FontWeight.Bold, color = LilaOscuro)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            necesitasEjemplo.keys.forEach { opcion ->
                val elegida = opcion == meta
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (elegida) Lila else Color.Transparent)
                        .border(
                            2.dp,
                            if (elegida) Lila else Color(0xFFB9A6FB),
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { meta = opcion },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = opcion.toString(),
                        fontWeight = FontWeight.Bold,
                        color = if (elegida) LilaTinta else LilaOscuro
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = necesitasEjemplo.getValue(meta),
                fontFamily = Bricolage,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 44.sp,
                color = Color(0xFF5B3FD1)
            )
            Text(
                text = "es lo que necesitas en promedio en el 60% que falta",
                fontWeight = FontWeight.Bold,
                color = LilaOscuro
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
fun MateriaPreview() {
    MiSemestreTheme {
        MateriaContenido(Materia(id = 1, nombre = "Diseño de Apps para Móviles", creditos = 3), {})
    }
}
