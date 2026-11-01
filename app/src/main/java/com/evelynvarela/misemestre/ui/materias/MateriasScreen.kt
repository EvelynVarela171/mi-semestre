package com.evelynvarela.misemestre.ui.materias

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.evelynvarela.misemestre.data.Materia
import com.evelynvarela.misemestre.ui.componentes.CaraLumi
import com.evelynvarela.misemestre.ui.componentes.LumiAnimada
import com.evelynvarela.misemestre.ui.inicio.InicioViewModel
import com.evelynvarela.misemestre.ui.theme.Cielo
import com.evelynvarela.misemestre.ui.theme.Durazno
import com.evelynvarela.misemestre.ui.theme.LilaClaro
import com.evelynvarela.misemestre.ui.theme.Menta
import com.evelynvarela.misemestre.ui.theme.MiSemestreTheme
import com.evelynvarela.misemestre.ui.theme.Rosa
import com.evelynvarela.misemestre.ui.theme.TextoSuave

private val coloresMateria = listOf(
    Durazno to Color(0xFF5C2200),
    Cielo to Color(0xFF06395A),
    Rosa to Color(0xFF5C0F36),
    Menta to Color(0xFF0D4A2A)
)

fun colorDeMateria(id: Int): Pair<Color, Color> = coloresMateria[(id - 1).mod(coloresMateria.size)]

@Composable
fun MateriasScreen(
    onAbrirMateria: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InicioViewModel = viewModel()
) {
    val materias by viewModel.materias.collectAsState()

    var agregando by remember { mutableStateOf(false) }
    var materiaPorBorrar by remember { mutableStateOf<Materia?>(null) }

    MateriasContenido(
        materias = materias,
        onAbrirMateria = onAbrirMateria,
        onAgregar = { agregando = true },
        onBorrar = { materiaPorBorrar = it },
        modifier = modifier
    )

    if (agregando) {
        DialogoAgregarMateria(
            onCancelar = { agregando = false },
            onGuardar = { nombre, creditos ->
                agregando = false
                viewModel.agregar(nombre, creditos)
            }
        )
    }

    materiaPorBorrar?.let { materia ->
        AlertDialog(
            onDismissRequest = { materiaPorBorrar = null },
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text("¿Borrar ${materia.nombre}?") },
            confirmButton = {
                Button(
                    onClick = {
                        materiaPorBorrar = null
                        viewModel.borrar(materia)
                    }
                ) {
                    Text("Borrar")
                }
            },
            dismissButton = {
                TextButton(onClick = { materiaPorBorrar = null }) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        )
    }
}

@Composable
private fun MateriasContenido(
    materias: List<Materia>?,
    onAbrirMateria: (Int) -> Unit,
    onAgregar: () -> Unit,
    onBorrar: (Materia) -> Unit,
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Text(
                text = "Materias",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(20.dp))

            if (materias != null && materias.isEmpty()) {
                SinMaterias(Modifier.weight(1f).fillMaxWidth())
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(materias.orEmpty(), key = { it.id }) { materia ->
                        TarjetaMateria(
                            materia = materia,
                            onAbrir = { onAbrirMateria(materia.id) },
                            onBorrar = { onBorrar(materia) }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onAgregar,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Agregar materia", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun TarjetaMateria(
    materia: Materia,
    onAbrir: () -> Unit,
    modifier: Modifier = Modifier,
    onBorrar: (() -> Unit)? = null
) {
    val (fondo, texto) = colorDeMateria(materia.id)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(fondo)
            .clickable(onClick = onAbrir)
            .padding(start = 14.dp, top = 4.dp, bottom = 4.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f).padding(vertical = 8.dp)) {
            Text(materia.nombre, color = texto, fontWeight = FontWeight.Bold)
            Text(
                text = if (materia.creditos == 1) "1 crédito" else "${materia.creditos} créditos",
                color = texto,
                style = MaterialTheme.typography.bodySmall
            )
        }
        if (onBorrar != null) {
            IconButton(onClick = onBorrar) {
                Text("✕", color = texto)
            }
        }
    }
}

@Composable
private fun SinMaterias(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LumiAnimada(
            cara = CaraLumi.Normal,
            saludando = false,
            modifier = Modifier.size(170.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Aún no tienes materias",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Agrega la primera para empezar.",
            color = TextoSuave
        )
    }
}

@Composable
private fun DialogoAgregarMateria(
    onCancelar: () -> Unit,
    onGuardar: (nombre: String, creditos: Int) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var creditos by remember { mutableStateOf("") }
    val creditosValidos = creditos.toIntOrNull()?.takeIf { it > 0 }
    val coloresCampo = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
    )

    AlertDialog(
        onDismissRequest = onCancelar,
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text("Nueva materia") },
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
                    value = creditos,
                    onValueChange = { creditos = it.filter(Char::isDigit) },
                    label = { Text("Créditos") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(16.dp),
                    colors = coloresCampo
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onGuardar(nombre.trim(), creditosValidos ?: return@Button) },
                enabled = nombre.isNotBlank() && creditosValidos != null
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) {
                Text("Cancelar", color = MaterialTheme.colorScheme.onSurface)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun MateriasPreview() {
    MiSemestreTheme {
        MateriasContenido(
            materias = listOf(
                Materia(id = 1, nombre = "Apps móviles", creditos = 3),
                Materia(id = 2, nombre = "Bases de datos", creditos = 3)
            ),
            onAbrirMateria = {},
            onAgregar = {},
            onBorrar = {}
        )
    }
}
