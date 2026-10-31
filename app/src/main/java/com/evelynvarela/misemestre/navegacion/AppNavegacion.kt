package com.evelynvarela.misemestre.navegacion

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.evelynvarela.misemestre.ui.asistente.AsistenteScreen
import com.evelynvarela.misemestre.ui.bienvenida.BienvenidaScreen
import com.evelynvarela.misemestre.ui.componentes.BarraNavegacion
import com.evelynvarela.misemestre.ui.entregas.EntregasScreen
import com.evelynvarela.misemestre.ui.inicio.InicioScreen
import com.evelynvarela.misemestre.ui.login.LoginScreen
import com.evelynvarela.misemestre.ui.materia.MateriaScreen
import com.evelynvarela.misemestre.ui.materias.MateriasScreen
import com.evelynvarela.misemestre.ui.perfil.PerfilScreen

private const val RUTA_MATERIA = "materia/{id}"
private val rutasConBarra =
    setOf("inicio", "materias", RUTA_MATERIA, "asistente", "entregas", "perfil")

// Cambia de pestaña sin ir acumulando pantallas: Atrás siempre vuelve a Inicio
private fun NavHostController.irAPestana(ruta: String) {
    navigate(ruta) {
        popUpTo("inicio") { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun AppNavegacion(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route
    val tecladoAbierto = WindowInsets.ime.getBottom(LocalDensity.current) > 0

    Column(modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = "bienvenida",
            modifier = Modifier.weight(1f)
        ) {
            composable("bienvenida") {
                BienvenidaScreen(
                    onTerminar = {
                        navController.navigate("login") {
                            popUpTo("bienvenida") { inclusive = true }
                        }
                    }
                )
            }

            composable("login") {
                LoginScreen(
                    onEntrar = {
                        navController.navigate("inicio") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }

            composable("inicio") {
                InicioScreen(
                    onAbrirMateria = { navController.navigate("materia/$it") },
                    onVerMaterias = { navController.irAPestana("materias") },
                    onVerEntregas = { navController.irAPestana("entregas") },
                    onVerAsistente = { navController.irAPestana("asistente") },
                    onVerPerfil = { navController.navigate("perfil") }
                )
            }

            composable("perfil") {
                PerfilScreen(
                    onVolver = { navController.popBackStack() },
                    onCerrarSesion = {
                        // Limpia todo el historial: Atrás ya no vuelve a la app
                        navController.navigate("login") {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                )
            }

            composable("materias") {
                MateriasScreen(onAbrirMateria = { navController.navigate("materia/$it") })
            }

            composable(
                route = RUTA_MATERIA,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { entrada ->
                MateriaScreen(
                    materiaId = entrada.arguments?.getInt("id") ?: 0,
                    onVolver = { navController.popBackStack() }
                )
            }

            composable("asistente") {
                AsistenteScreen()
            }

            composable("entregas") {
                EntregasScreen()
            }
        }

        if (rutaActual in rutasConBarra && !tecladoAbierto) {
            BarraNavegacion(
                rutaActiva = when (rutaActual) {
                    RUTA_MATERIA -> "materias"
                    "perfil" -> null
                    else -> rutaActual
                },
                onIrA = { navController.irAPestana(it) },
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 12.dp)
            )
        }
    }
}
