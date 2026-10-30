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
fun LoginScreen(onEntrar: () -> Unit, modifier: Modifier = Modifier) {

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

            // Los dos weight reparten el espacio libre arriba y abajo de Lumi
            Spacer(Modifier.weight(1f))

            // Si la tocan, Lumi se sorprende y saluda
            LumiAnimada(
                cara = caraLumi,
                saludando = lumiSaluda,
                // Se tapa los ojos mientras escriben la contraseña
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

            // Con el teclado abierto se oculta el texto para que Lumi siga a la vista
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

            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                ),
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
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { claveEnFoco = it.isFocused }
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = onEntrar,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Entrar", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    MiSemestreTheme {
        LoginScreen(onEntrar = {})
    }
}
