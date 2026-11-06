package com.evelynvarela.misemestre.ui.entregas

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.evelynvarela.misemestre.data.Entrega
import com.evelynvarela.misemestre.data.textoFecha
import com.evelynvarela.misemestre.ui.theme.Tinta
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DialogoEntrega(
    entrega: Entrega?,
    materias: List<String>,
    onCancelar: () -> Unit,
    onGuardar: (nombre: String, materia: String, fecha: LocalDate) -> Unit
) {
    var nombre by remember { mutableStateOf(entrega?.nombre.orEmpty()) }
    var materia by remember { mutableStateOf(entrega?.materia.orEmpty()) }
    var fecha by remember { mutableStateOf(entrega?.fecha) }
    var eligiendoFecha by remember { mutableStateOf(false) }
    val coloresCampo = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
    )

    AlertDialog(
        onDismissRequest = onCancelar,
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text(if (entrega == null) "Nueva entrega" else "Editar entrega") },
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
            initialSelectedDateMillis = fecha?.takeUnless { it.isBefore(LocalDate.now()) }
                ?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli(),
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
internal fun DialogoCrearMaterias(
    materias: List<String>,
    onAhoraNo: () -> Unit,
    onCrear: (Map<String, Int>) -> Unit
) {
    val creditos = remember { mutableStateMapOf<String, String>() }
    val validas = materias.mapNotNull { nombre ->
        creditos[nombre]?.toIntOrNull()?.takeIf { it > 0 }?.let { nombre to it }
    }.toMap()

    AlertDialog(
        onDismissRequest = onAhoraNo,
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text("Materias de tu calendario") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Pon los créditos de las que quieras agregar.")
                materias.forEach { nombre ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(nombre, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        OutlinedTextField(
                            value = creditos[nombre].orEmpty(),
                            onValueChange = { creditos[nombre] = it.filter(Char::isDigit) },
                            label = { Text("Créditos") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.width(112.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onCrear(validas) }, enabled = validas.isNotEmpty()) {
                Text("Crear")
            }
        },
        dismissButton = {
            TextButton(onClick = onAhoraNo) {
                Text("Ahora no", color = MaterialTheme.colorScheme.onSurface)
            }
        }
    )
}

@Composable
internal fun DialogoConectarCalendario(
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
