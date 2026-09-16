package com.juanga.terragest

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "bienvenida") {

        composable("bienvenida") {
            BienvenidaScreen(
                onNavegarLogin = { navController.navigate("login") },
                onNavegarRegistro = { navController.navigate("registro_paso1") }
            )
        }

        composable("login") {
            InicioSesionScreen(
                onNavegarRecuperar = { navController.navigate("recuperar_inicio") },
                onNavegarAtras = { navController.popBackStack() },
                // NUEVO: Agregamos la ruta para ir al inicio al hacer clic en Iniciar Sesión
                onNavegarInicio = {
                    navController.navigate("inicio") {
                        // Esto borra el login del historial para que si le dan atrás, no vuelvan a Iniciar Sesión
                        popUpTo("bienvenida") { inclusive = false }
                    }
                }
            )
        }

        composable("registro_paso1") {
            CrearCuentaPaso1Screen(
                onNavegarAtras = { navController.popBackStack() },
                onNavegarPaso2 = { navController.navigate("registro_paso2") },
                onNavegarLogin = { navController.navigate("login") }
            )
        }

        composable("registro_paso2") {
            CrearCuentaPaso2Screen(
                onNavegarAtras = { navController.popBackStack() },
                onNavegarExito = { navController.navigate("exito_registro") },
                onNavegarLogin = { navController.navigate("login") }
            )
        }

        composable("recuperar_inicio") {
            RestablecerInicioScreen(
                onNavegarAtras = { navController.popBackStack() },
                onNavegarPaso1 = { navController.navigate("recuperar_paso1") }
            )
        }

        composable("recuperar_paso1") {
            RestablecerPaso1Screen(
                onNavegarAtras = { navController.popBackStack() },
                onNavegarPaso2 = { metodo -> navController.navigate("recuperar_paso2/$metodo") }
            )
        }

        composable(
            route = "recuperar_paso2/{metodo}",
            arguments = listOf(navArgument("metodo") { type = NavType.StringType })
        ) { backStackEntry ->
            val metodo = backStackEntry.arguments?.getString("metodo") ?: "correo"
            RestablecerPaso2Screen(
                metodo = metodo,
                onNavegarAtras = { navController.popBackStack() },
                onNavegarPaso3 = { navController.navigate("recuperar_paso3") }
            )
        }

        composable("recuperar_paso3") {
            RestablecerPaso3Screen(
                onNavegarAtras = { navController.popBackStack() },
                onNavegarExito = { navController.navigate("exito_recuperacion") }
            )
        }

        composable("exito_registro") {
            ExitoScreen(
                mensajePrincipal = "Cuenta creada\ncorrectamente!",
                textoDelBoton = "Iniciar sesión",
                onNavegarLogin = {
                    navController.navigate("login") {
                        popUpTo("bienvenida") { inclusive = false }
                    }
                },
                onNavegarInicio = { navController.popBackStack("bienvenida", inclusive = false) }
            )
        }

        composable("exito_recuperacion") {
            ExitoScreen(
                mensajePrincipal = "¡Contraseña actualizada\ncon éxito!",
                textoDelBoton = "Iniciar sesión",
                onNavegarLogin = {
                    navController.navigate("login") {
                        popUpTo("bienvenida") { inclusive = false }
                    }
                },
                onNavegarInicio = { navController.popBackStack("bienvenida", inclusive = false) }
            )
        }

        // --- NUEVA PANTALLA: DASHBOARD (INICIO) ---
        composable("inicio") {
            InicioScreen(
                onNavegarCultivos = { /* Lo conectaremos en el siguiente paso */ },
                onNavegarGastos = { /* Futuro módulo */ },
                onNavegarReportes = { /* Futuro módulo */ },
                onNavegarPerfil = { /* Futuro módulo */ }
            )
        }

        composable("mis_cultivos") {
            MisCultivosScreen(
                onNavegarAtras = { navController.popBackStack() },
                onNavegarNuevoCultivo = { navController.navigate("nuevo_cultivo") }
            )
        }

        composable("nuevo_cultivo") {
            NuevoCultivoScreen(
                onNavegarAtras = { navController.popBackStack() },
                // Temporalmente lo enviamos atrás simulando que guardó con éxito
                onGuardarCultivo = { navController.popBackStack() }
            )
        }
    }
}