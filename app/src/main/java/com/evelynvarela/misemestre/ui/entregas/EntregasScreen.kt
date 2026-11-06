package com.evelynvarela.misemestre.ui.entregas

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.evelynvarela.misemestre.R
import com.evelynvarela.misemestre.data.Entrega
import com.evelynvarela.misemestre.data.textoFecha
import com.evelynvarela.misemestre.ui.componentes.CaraLumi
import com.evelynvarela.misemestre.ui.componentes.Reaccion
import com.evelynvarela.misemestre.ui.componentes.ReaccionLumi
import com.evelynvarela.misemestre.ui.theme.Bricolage
import com.evelynvarela.misemestre.ui.theme.Lila
import com.evelynvarela.misemestre.ui.theme.LilaOscuro
import com.evelynvarela.misemestre.ui.theme.LilaTinta
import com.evelynvarela.misemestre.ui.theme.MiSemestreTheme
import com.evelynvarela.misemestre.ui.theme.TextoSuave
import java.time.LocalDate

private val felicitaciones = listOf("¡Una menos!", "¡Bien hecho!", "¡Lo lograste!", "¡Sigue así!")

@Composable
fun EntregasScreen(
    modifier: Modifier = Modifier,
    viewModel: EntregasViewModel = viewModel()
) {
    val entregas by viewModel.entregas.collectAsState()
    val materias by viewModel.materias.collectAsState()
    val sugeridas by viewModel.materiasSugeridas.collectAsState()

    var agregando by remember { mutableStateOf(false) }
    var entregaEditada by remember { mutableStateOf<Entrega?>(null) }
    var conectando by remember { mutableStateOf(false) }
    var desconectando by remember { mutableStateOf(false) }
    var creandoMaterias by remember { mutableStateOf(false) }
    var entregaPorBorrar by remember { mutableStateOf<Entrega?>(null) }
    var reaccion by remember { mutableStateOf<Reaccion?>(null) }

    val permiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}
    val pedirPermiso = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permiso.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Box(modifier.fillMaxSize()) {
        EntregasContenido(
            entregas = entregas.orEmpty(),
            calendarioConectado = viewModel.conectado,
            ocupado = viewModel.ocupado,
            mensaje = viewModel.mensaje,
            error = viewModel.error.takeUnless { conectando },
            materiasNuevas = sugeridas.size,
            onAgregar = { agregando = true },
            onEditar = { entregaEditada = it },
            onCambiar = { entrega ->
                viewModel.cambiar(entrega)
                if (!entrega.entregada) {
                    val quedan = entregas.orEmpty().count { !it.entregada } - 1
                    reaccion = Reaccion(
                        cara = CaraLumi.Feliz,
                        mensaje = if (quedan == 0) "¡Terminaste todo!" else felicitaciones.random()
                    )
                }
            },
            onBorrar = { entregaPorBorrar = it },
            onConectar = {
                viewModel.limpiarError()
                conectando = true
            },
            onActualizar = viewModel::actualizar,
            onDesconectar = { desconectando = true },
            onCrearMaterias = { creandoMaterias = true }
        )
        ReaccionLumi(reaccion, onTerminar = { reaccion = null })
    }

    if (agregando) {
        DialogoEntrega(
            entrega = null,
            materias = materias.map { it.nombre },
            onCancelar = { agregando = false },
            onGuardar = { nombre, materia, fecha ->
                agregando = false
                viewModel.agregar(nombre, materia, fecha)
                pedirPermiso()
            }
        )
    }

    entregaEditada?.let { entrega ->
        DialogoEntrega(
            entrega = entrega,
            materias = materias.map { it.nombre },
            onCancelar = { entregaEditada = null },
            onGuardar = { nombre, materia, fecha ->
                entregaEditada = null
                viewModel.editar(entrega, nombre, materia, fecha)
            }
        )
    }

    if (creandoMaterias) {
        DialogoCrearMaterias(
            materias = sugeridas,
            onAhoraNo = {
                creandoMaterias = false
                viewModel.ignorarMaterias(sugeridas)
            },
            onCrear = { creditos ->
                creandoMaterias = false
                viewModel.crearMaterias(creditos)
            }
        )
    }

    if (conectando) {
        DialogoConectarCalendario(
            ocupado = viewModel.ocupado,
            error = viewModel.error,
            onCancelar = { conectando = false },
            onConectar = { enlace ->
                viewModel.conectar(enlace) {
                    conectando = false
                    pedirPermiso()
                }
            }
        )
    }

    if (desconectando) {
        AlertDialog(
            onDismissRequest = { desconectando = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text("¿Desconectar el calendario?") },
            text = { Text("Se quitan las entregas importadas. Las que agregaste a mano se quedan.") },
            confirmButton = {
                Button(
                    onClick = {
                        desconectando = false
                        viewModel.desconectar()
                    }
                ) {
                    Text("Desconectar")
                }
            },
            dismissButton = {
                TextButton(onClick = { desconectando = false }) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        )
    }

    entregaPorBorrar?.let { entrega ->
        AlertDialog(
            onDismissRequest = { entregaPorBorrar = null },
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text("¿Borrar ${entrega.nombre}?") },
            confirmButton = {
                Button(
                    onClick = {
                        entregaPorBorrar = null
                        viewModel.borrar(entrega)
                    }
                ) {
                    Text("Borrar")
                }
            },
            dismissButton = {
                TextButton(onClick = { entregaPorBorrar = null }) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        )
    }
}

@Composable
private fun EntregasContenido(
    entregas: List<Entrega>,
    calendarioConectado: Boolean,
    ocupado: Boolean,
    mensaje: String?,
    error: String?,
    materiasNuevas: Int,
    onAgregar: () -> Unit,
    onEditar: (Entrega) -> Unit,
    onCambiar: (Entrega) -> Unit,
    onBorrar: (Entrega) -> Unit,
    onConectar: () -> Unit,
    onActualizar: () -> Unit,
    onDesconectar: () -> Unit,
    onCrearMaterias: () -> Unit,
    modifier: Modifier = Modifier
) {
    var verEntregadas by remember { mutableStateOf(false) }
    var diaElegido by remember { mutableStateOf<LocalDate?>(null) }
    val (listas, pendientes) = entregas.partition { it.entregada }

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

            SemanaActual(
                pendientes = pendientes,
                elegido = diaElegido,
                onElegir = { dia -> diaElegido = dia.takeUnless { it == diaElegido } }
            )

            TarjetaCalendario(
                conectado = calendarioConectado,
                ocupado = ocupado,
                mensaje = mensaje,
                error = error,
                onConectar = onConectar,
                onActualizar = onActualizar,
                onDesconectar = onDesconectar
            )

            if (materiasNuevas > 0) {
                TarjetaMateriasNuevas(materiasNuevas, onCrearMaterias)
            }

            val dia = diaElegido
            if (dia == null) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Filtro("Pendientes · ${pendientes.size}", !verEntregadas) { verEntregadas = false }
                    Filtro("Entregadas · ${listas.size}", verEntregadas) { verEntregadas = true }
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Entregas del ${textoFecha(dia)}",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = { diaElegido = null }) {
                        Text("Ver todas", color = LilaOscuro, fontWeight = FontWeight.Bold)
                    }
                }
            }

            val visibles = when {
                dia != null -> entregas.filter { it.fecha == dia }
                verEntregadas -> listas
                else -> pendientes
            }
            if (visibles.isEmpty()) {
                Text(
                    text = when {
                        dia != null -> "Ese día no tienes entregas."
                        verEntregadas -> "Aún no has marcado entregas."
                        else -> "No tienes entregas pendientes."
                    },
                    color = TextoSuave
                )
            }
            visibles.forEach { entrega ->
                key(entrega.id) {
                    FilaDeslizable(entrega, onCambiar = { onCambiar(entrega) }) {
                        FilaEntrega(
                            entrega = entrega,
                            onEditar = { onEditar(entrega) },
                            onCambiar = { onCambiar(entrega) },
                            onBorrar = { onBorrar(entrega) }
                        )
                    }
                }
            }

            AvisoRecordatorio()
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .size(60.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(Lila)
                .clickable(onClick = onAgregar),
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


@Preview(showBackground = true, heightDp = 800)
@Composable
fun EntregasPreview() {
    MiSemestreTheme {
        EntregasContenido(
            entregas = listOf(
                Entrega(nombre = "Taller ABP", materia = "Apps móviles", fecha = LocalDate.now().plusDays(2)),
                Entrega(nombre = "Video", materia = "Apps móviles", fecha = LocalDate.now().plusDays(7))
            ),
            calendarioConectado = false,
            ocupado = false,
            mensaje = null,
            error = null,
            materiasNuevas = 2,
            onAgregar = {},
            onEditar = {},
            onCambiar = {},
            onBorrar = {},
            onConectar = {},
            onActualizar = {},
            onDesconectar = {},
            onCrearMaterias = {}
        )
    }
}
