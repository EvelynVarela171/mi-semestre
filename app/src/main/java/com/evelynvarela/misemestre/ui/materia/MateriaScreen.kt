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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.evelynvarela.misemestre.R
import com.evelynvarela.misemestre.data.CalculadoraNotas
import com.evelynvarela.misemestre.data.Materia
import com.evelynvarela.misemestre.data.Nota
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

private val metas = listOf(3.0, 3.5, 4.0, 4.5)
private val ColorMorado = Color(0xFF5B3FD1)
private val ColorAlerta = Color(0xFF7A0F3A)

@Composable
fun MateriaScreen(
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MateriaViewModel = viewModel()
) {
    val materia by viewModel.materia.collectAsState()
    val notas by viewModel.notas.collectAsState()

    var agregando by remember { mutableStateOf(false) }
    var notaEditada by remember { mutableStateOf<Nota?>(null) }

    materia?.let {
        MateriaContenido(
            materia = it,
            notas = notas,
            onVolver = onVolver,
            onAgregarNota = { agregando = true },
            onEditarNota = { nota -> notaEditada = nota },
            modifier = modifier
        )
    }

    if (agregando) {
        DialogoNota(
            nota = null,
            pesoDisponible = 100 - notas.sumOf { it.peso },
            onCancelar = { agregando = false },
            onGuardar = { nombre, peso, valor ->
                agregando = false
                viewModel.agregar(nombre, peso, valor)
            }
        )
    }

    notaEditada?.let { nota ->
        DialogoNota(
            nota = nota,
            pesoDisponible = 100 - notas.filter { it.id != nota.id }.sumOf { it.peso },
            onCancelar = { notaEditada = null },
            onGuardar = { nombre, peso, valor ->
                notaEditada = null
                viewModel.actualizar(nota.copy(nombre = nombre, peso = peso, valor = valor))
            },
            onBorrar = {
                notaEditada = null
                viewModel.borrar(nota)
            }
        )
    }
}

@Composable
private fun MateriaContenido(
    materia: Materia,
    notas: List<Nota>,
    onVolver: () -> Unit,
    onAgregarNota: () -> Unit,
    onEditarNota: (Nota) -> Unit,
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

        TarjetaMateriaGrande(materia, notas)
        EncabezadoNotas(notas, onAgregarNota)

        if (notas.isEmpty()) {
            Text(
                text = "Aún no tienes notas. Toca + para agregar la primera.",
                color = TextoSuave
            )
        } else {
            notas.forEach { nota -> FilaNota(nota, onEditar = { onEditarNota(nota) }) }
            Calculadora(notas)
        }
    }
}

@Composable
private fun TarjetaMateriaGrande(materia: Materia, notas: List<Nota>) {
    val (fondo, texto) = colorDeMateria(materia.id)
    val promedio = CalculadoraNotas.promedioEvaluado(notas)

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
                text = if (promedio == null) {
                    "Aún no tienes notas calificadas"
                } else {
                    val evaluado = CalculadoraNotas.pesoEvaluado(notas)
                    "Llevas ${CalculadoraNotas.formato(promedio)} en el $evaluado% evaluado"
                },
                color = texto,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun EncabezadoNotas(notas: List<Nota>, onAgregarNota: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(
                text = "Notas",
                fontFamily = Bricolage,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            )
            Text(
                text = "${notas.sumOf { it.peso }}% de 100% definido",
                color = TextoSuave,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Lila)
                .clickable(onClick = onAgregarNota),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_agregar),
                contentDescription = "Agregar nota",
                tint = LilaTinta
            )
        }
    }
}

@Composable
private fun FilaNota(nota: Nota, onEditar: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.5.dp, Linea, RoundedCornerShape(16.dp))
            .clickable(onClick = onEditar)
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
            text = nota.valor?.let(CalculadoraNotas::formato) ?: "—",
            fontFamily = Bricolage,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp,
            color = if (nota.valor == null) Color(0xFF9590B0) else Tinta
        )
    }
}

@Composable
private fun Calculadora(notas: List<Nota>) {
    var meta by remember { mutableStateOf(metas.first()) }
    val necesaria = CalculadoraNotas.notaNecesaria(notas, meta)
    val restante = 100 - CalculadoraNotas.pesoEvaluado(notas)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LilaClaro, RoundedCornerShape(24.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (necesaria == null) {
            ResultadoCalculo(
                valor = CalculadoraNotas.formato(CalculadoraNotas.acumulado(notas)),
                mensaje = "es tu nota final: ya está todo evaluado"
            )
        } else {
            Text("¿Qué nota quieres sacar?", fontWeight = FontWeight.Bold, color = LilaOscuro)
            SelectorMeta(meta, onElegir = { meta = it })

            when {
                necesaria <= 0 -> ResultadoCalculo("0.0", "Esta meta ya la tienes asegurada")
                necesaria > 5 -> ResultadoCalculo(
                    valor = CalculadoraNotas.formatoNecesaria(necesaria),
                    mensaje = "Ni con 5.0 en el $restante% que falta llegas a esta meta",
                    color = ColorAlerta
                )
                else -> ResultadoCalculo(
                    valor = CalculadoraNotas.formatoNecesaria(necesaria),
                    mensaje = "es lo que necesitas en promedio en el $restante% que falta"
                )
            }
        }
    }
}

@Composable
private fun SelectorMeta(meta: Double, onElegir: (Double) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        metas.forEach { opcion ->
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
                    .clickable { onElegir(opcion) },
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
}

@Composable
private fun ResultadoCalculo(valor: String, mensaje: String, color: Color = ColorMorado) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = valor,
            fontFamily = Bricolage,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 44.sp,
            color = color
        )
        Text(mensaje, fontWeight = FontWeight.Bold, color = LilaOscuro)
    }
}

@Composable
private fun DialogoNota(
    nota: Nota?,
    pesoDisponible: Int,
    onCancelar: () -> Unit,
    onGuardar: (nombre: String, peso: Int, valor: Double?) -> Unit,
    onBorrar: (() -> Unit)? = null
) {
    var nombre by remember { mutableStateOf(nota?.nombre.orEmpty()) }
    var peso by remember { mutableStateOf(nota?.peso?.toString().orEmpty()) }
    var valor by remember { mutableStateOf(nota?.valor?.let(CalculadoraNotas::formato).orEmpty()) }

    val pesoValido = peso.toIntOrNull()?.takeIf { it in 1..pesoDisponible }
    val valorNumero = valor.replace(',', '.').toDoubleOrNull()
    val valorValido = valor.isBlank() || (valorNumero != null && valorNumero in 0.0..5.0)
    val coloresCampo = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
    )

    AlertDialog(
        onDismissRequest = onCancelar,
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text(if (nota == null) "Nueva nota" else "Editar nota") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = coloresCampo
                )
                OutlinedTextField(
                    value = peso,
                    onValueChange = { peso = it.filter(Char::isDigit) },
                    label = { Text("Porcentaje") },
                    supportingText = { Text("Disponible: $pesoDisponible%") },
                    isError = peso.isNotEmpty() && pesoValido == null,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(16.dp),
                    colors = coloresCampo
                )
                OutlinedTextField(
                    value = valor,
                    onValueChange = { nuevo -> valor = nuevo.filter { it.isDigit() || it in ".," } },
                    label = { Text("Nota de 0 a 5") },
                    supportingText = { Text("Déjala vacía si aún no la tienes") },
                    isError = !valorValido,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(16.dp),
                    colors = coloresCampo
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onGuardar(nombre.trim(), pesoValido ?: return@Button, valorNumero) },
                enabled = nombre.isNotBlank() && pesoValido != null && valorValido
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            Row {
                if (onBorrar != null) {
                    TextButton(onClick = onBorrar) {
                        Text("Borrar", color = ColorAlerta)
                    }
                }
                TextButton(onClick = onCancelar) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    )
}

@Preview(showBackground = true, heightDp = 900)
@Composable
fun MateriaPreview() {
    MiSemestreTheme {
        MateriaContenido(
            materia = Materia(id = 1, nombre = "Diseño de Apps para Móviles", creditos = 3),
            notas = listOf(
                Nota(materiaId = 1, nombre = "Foro temático", peso = 20, valor = 4.6),
                Nota(materiaId = 1, nombre = "Cátedra académica", peso = 20, valor = 5.0),
                Nota(materiaId = 1, nombre = "Taller ABP", peso = 60)
            ),
            onVolver = {},
            onAgregarNota = {},
            onEditarNota = {}
        )
    }
}
