package com.evelynvarela.misemestre.ui.entregas

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.evelynvarela.misemestre.R
import com.evelynvarela.misemestre.data.Entrega
import com.evelynvarela.misemestre.data.esUrgente
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
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

private val ColorDurazno = Color(0xFF5C2200)

@Composable
fun EntregasScreen(
    modifier: Modifier = Modifier,
    viewModel: EntregasViewModel = viewModel()
) {
    val entregas by viewModel.entregas.collectAsState()
    val materias by viewModel.materias.collectAsState()

    var agregando by remember { mutableStateOf(false) }
    var conectando by remember { mutableStateOf(false) }
    var desconectando by remember { mutableStateOf(false) }
    var entregaPorBorrar by remember { mutableStateOf<Entrega?>(null) }

    val permiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}
    val pedirPermiso = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permiso.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    EntregasContenido(
        entregas = entregas.orEmpty(),
        calendarioConectado = viewModel.conectado,
        ocupado = viewModel.ocupado,
        mensaje = viewModel.mensaje,
        error = viewModel.error.takeUnless { conectando },
        onAgregar = { agregando = true },
        onCambiar = viewModel::cambiar,
        onBorrar = { entregaPorBorrar = it },
        onConectar = {
            viewModel.limpiarError()
            conectando = true
        },
        onActualizar = viewModel::actualizar,
        onDesconectar = { desconectando = true },
        modifier = modifier
    )

    if (agregando) {
        DialogoNuevaEntrega(
            materias = materias.map { it.nombre },
            onCancelar = { agregando = false },
            onGuardar = { nombre, materia, fecha ->
                agregando = false
                viewModel.agregar(nombre, materia, fecha)
                pedirPermiso()
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
    onAgregar: () -> Unit,
    onCambiar: (Entrega) -> Unit,
    onBorrar: (Entrega) -> Unit,
    onConectar: () -> Unit,
    onActualizar: () -> Unit,
    onDesconectar: () -> Unit,
    modifier: Modifier = Modifier
) {
    var verEntregadas by remember { mutableStateOf(false) }
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

            SemanaActual(pendientes)

            TarjetaCalendario(
                conectado = calendarioConectado,
                ocupado = ocupado,
                mensaje = mensaje,
                error = error,
                onConectar = onConectar,
                onActualizar = onActualizar,
                onDesconectar = onDesconectar
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Filtro("Pendientes · ${pendientes.size}", !verEntregadas) { verEntregadas = false }
                Filtro("Entregadas · ${listas.size}", verEntregadas) { verEntregadas = true }
            }

            val visibles = if (verEntregadas) listas else pendientes
            if (visibles.isEmpty()) {
                Text(
                    text = if (verEntregadas) {
                        "Aún no has marcado entregas."
                    } else {
                        "No tienes entregas pendientes."
                    },
                    color = TextoSuave
                )
            }
            visibles.forEach { entrega ->
                FilaEntrega(
                    entrega = entrega,
                    onCambiar = { onCambiar(entrega) },
                    onBorrar = { onBorrar(entrega) }
                )
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

@Composable
private fun SemanaActual(pendientes: List<Entrega>) {
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
private fun TarjetaCalendario(
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
                ocupado -> "Leyendo tu calendario..."
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
private fun FilaEntrega(entrega: Entrega, onCambiar: () -> Unit, onBorrar: () -> Unit) {
    val hecha = entrega.entregada
    val accion = if (hecha) "Marcar pendiente" else "Marcar lista"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
            .border(1.5.dp, Linea, RoundedCornerShape(20.dp))
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
            text = "Te aviso 1 día antes y el mismo día, desde las 8:00",
            color = ColorDurazno,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogoNuevaEntrega(
    materias: List<String>,
    onCancelar: () -> Unit,
    onGuardar: (nombre: String, materia: String, fecha: LocalDate) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var materia by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf<LocalDate?>(null) }
    var eligiendoFecha by remember { mutableStateOf(false) }
    val coloresCampo = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
    )

    AlertDialog(
        onDismissRequest = onCancelar,
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text("Nueva entrega") },
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
                    value = materia,
                    onValueChange = { materia = it },
                    label = { Text("Materia") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = coloresCampo
                )
                if (materias.isNotEmpty()) {
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        materias.forEach { opcion ->
                            Filtro(opcion, materia == opcion) { materia = opcion }
                        }
                    }
                }
                OutlinedButton(
                    onClick = { eligiendoFecha = true },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(fecha?.let(::textoFecha) ?: "Elegir fecha", color = Tinta)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onGuardar(nombre.trim(), materia.trim(), fecha ?: return@Button) },
                enabled = nombre.isNotBlank() && materia.isNotBlank() && fecha != null
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

    if (eligiendoFecha) {
        val estado = rememberDatePickerState(
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                    !fechaDeMilis(utcTimeMillis).isBefore(LocalDate.now())
            }
        )
        DatePickerDialog(
            onDismissRequest = { eligiendoFecha = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        estado.selectedDateMillis?.let { fecha = fechaDeMilis(it) }
                        eligiendoFecha = false
                    },
                    enabled = estado.selectedDateMillis != null
                ) {
                    Text("Listo")
                }
            },
            dismissButton = {
                TextButton(onClick = { eligiendoFecha = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = estado)
        }
    }
}

private fun fechaDeMilis(milis: Long): LocalDate =
    Instant.ofEpochMilli(milis).atZone(ZoneOffset.UTC).toLocalDate()

@Composable
private fun DialogoConectarCalendario(
    ocupado: Boolean,
    error: String?,
    onCancelar: () -> Unit,
    onConectar: (String) -> Unit
) {
    var enlace by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onCancelar,
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text("Conectar calendario") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Pega el enlace .ics de tu calendario. En Canvas está en " +
                        "Calendario, Fuente de calendario. Es privado: no lo compartas."
                )
                OutlinedTextField(
                    value = enlace,
                    onValueChange = { enlace = it },
                    label = { Text("Enlace del calendario") },
                    singleLine = true,
                    enabled = !ocupado,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )
                )
                if (error != null) {
                    Text(error, color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConectar(enlace) }, enabled = enlace.isNotBlank() && !ocupado) {
                Text(if (ocupado) "Conectando..." else "Conectar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) {
                Text("Cancelar", color = MaterialTheme.colorScheme.onSurface)
            }
        }
    )
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
            onAgregar = {},
            onCambiar = {},
            onBorrar = {},
            onConectar = {},
            onActualizar = {},
            onDesconectar = {}
        )
    }
}
