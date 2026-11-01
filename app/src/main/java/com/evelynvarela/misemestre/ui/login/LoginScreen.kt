package com.evelynvarela.misemestre.ui.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.evelynvarela.misemestre.ui.componentes.CaraLumi
import com.evelynvarela.misemestre.ui.componentes.LumiAnimada
import com.evelynvarela.misemestre.ui.theme.Cielo
import com.evelynvarela.misemestre.ui.theme.Durazno
import com.evelynvarela.misemestre.ui.theme.Lila
import com.evelynvarela.misemestre.ui.theme.MiSemestreTheme
import com.evelynvarela.misemestre.ui.theme.Rosa
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onEntrar: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel()
) {
    LoginContenido(
        cargando = viewModel.cargando,
        error = viewModel.error,
        onEntrar = { correo, clave -> viewModel.entrar(correo, clave, onEntrar) },
        onRegistrar = { nombre, correo, clave ->
            viewModel.registrar(nombre, correo, clave, onEntrar)
        },
        onCambiarModo = viewModel::limpiarError,
        modifier = modifier
    )
}

@Composable
private fun coloresCampo() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
)

@Composable
private fun LoginContenido(
    cargando: Boolean,
    error: String?,
    onEntrar: (correo: String, clave: String) -> Unit,
    onRegistrar: (nombre: String, correo: String, clave: String) -> Unit,
    onCambiarModo: () -> Unit,
    modifier: Modifier = Modifier
) {

    var creando by remember { mutableStateOf(false) }
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var caraLumi by remember { mutableStateOf(CaraLumi.Normal) }
    var lumiSaluda by remember { mutableStateOf(false) }
    var claveEnFoco by remember { mutableStateOf(false) }
    val tecladoAbierto = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    val scope = rememberCoroutineScope()

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
    ) {

        Box(
            Modifier
                .offset(x = (-60).dp, y = (-70).dp)
                .size(300.dp)
                .background(Lila.copy(alpha = 0.25f), RoundedCornerShape(58, 42, 50, 50))
        )
        Box(
            Modifier
                .align(Alignment.TopEnd)
                .offset(x = 50.dp, y = 120.dp)
                .size(170.dp)
                .background(Durazno.copy(alpha = 0.3f), RoundedCornerShape(40, 60, 45, 55))
        )
        Box(
            Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-70).dp, y = 60.dp)
                .size(34.dp)
                .background(Rosa.copy(alpha = 0.33f), CircleShape)
        )
        Box(
            Modifier
                .offset(x = 30.dp, y = 290.dp)
                .size(48.dp)
                .background(Cielo.copy(alpha = 0.33f), CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .heightIn(min = maxHeight)
                .padding(24.dp)
        ) {

            Spacer(Modifier.weight(1f))

            LumiAnimada(
                cara = caraLumi,
                saludando = lumiSaluda,
                tapandoOjos = claveEnFoco,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(190.dp)
                    .clickable(interactionSource = null, indication = null) {
                        scope.launch {
                            caraLumi = CaraLumi.Sorpresa
                            delay(350)
                            caraLumi = CaraLumi.Feliz
                            lumiSaluda = true
                            delay(1400)
                            lumiSaluda = false
                            caraLumi = CaraLumi.Normal
                        }
                    }
            )

            Spacer(Modifier.weight(1f))

            AnimatedVisibility(visible = !tecladoAbierto) {
                Column {
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = "Mi Semestre",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "Tu semestre,\nsin correr al final.",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Notas, entregas y Lumi, una medusita que te ayuda a decidir qué hacer primero.",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )

                    Spacer(Modifier.height(24.dp))
                }
            }

            AnimatedVisibility(visible = creando) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = coloresCampo(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )
            }

            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(16.dp),
                colors = coloresCampo(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = clave,
                onValueChange = { clave = it },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(16.dp),
                colors = coloresCampo(),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { claveEnFoco = it.isFocused }
            )

            error?.let {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    if (creando) onRegistrar(nombre, correo, clave) else onEntrar(correo, clave)
                },
                enabled = !cargando && correo.isNotBlank() && clave.isNotEmpty() &&
                    (!creando || nombre.isNotBlank()),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text(
                    text = when {
                        cargando -> "Un momento..."
                        creando -> "Crear cuenta"
                        else -> "Entrar"
                    },
                    fontWeight = FontWeight.Bold
                )
            }

            TextButton(
                onClick = {
                    creando = !creando
                    onCambiarModo()
                },
                enabled = !cargando,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = if (creando) "¿Ya tienes cuenta? Entra" else "¿No tienes cuenta? Crea una",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    MiSemestreTheme {
        LoginContenido(
            cargando = false,
            error = null,
            onEntrar = { _, _ -> },
            onRegistrar = { _, _, _ -> },
            onCambiarModo = {}
        )
    }
}
