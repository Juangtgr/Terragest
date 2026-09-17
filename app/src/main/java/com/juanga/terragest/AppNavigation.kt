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

        // --- MÓDULO DE AUTENTICACIÓN ---
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
                onNavegarInicio = {
                    navController.navigate("inicio") { popUpTo("bienvenida") { inclusive = false } }
                },
                // NUEVA RUTA PARA EL ADMIN
                onNavegarAdmin = {
                    navController.navigate("admin_usuarios") { popUpTo("bienvenida") { inclusive = false } }
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
                onNavegarLogin = { navController.navigate("login") { popUpTo("bienvenida") { inclusive = false } } },
                onNavegarInicio = { navController.popBackStack("bienvenida", inclusive = false) }
            )
        }

        composable("exito_recuperacion") {
            ExitoScreen(
                mensajePrincipal = "¡Contraseña actualizada\ncon éxito!",
                textoDelBoton = "Iniciar sesión",
                onNavegarLogin = { navController.navigate("login") { popUpTo("bienvenida") { inclusive = false } } },
                onNavegarInicio = { navController.popBackStack("bienvenida", inclusive = false) }
            )
        }

        // --- MÓDULO DASHBOARD (INICIO) ---
        composable("inicio") {
            InicioScreen(
                onNavegarCultivos = { navController.navigate("mis_cultivos") },
                onNavegarInsumos = { navController.navigate("mis_insumos") },
                onNavegarGastos = { navController.navigate("mis_gastos") },
                onNavegarReportes = { navController.navigate("reportes") },
                onNavegarPerfil = { navController.navigate("perfil") }
            )
        }

        // --- MÓDULO DE CULTIVOS ---
        composable("mis_cultivos") {
            MisCultivosScreen(
                onNavegarAtras = { navController.popBackStack() },
                onNavegarNuevoCultivo = { navController.navigate("nuevo_cultivo") }
            )
        }

        composable("nuevo_cultivo") {
            NuevoCultivoScreen(
                onNavegarAtras = { navController.popBackStack() },
                onGuardarCultivo = { navController.navigate("confirmacion_cultivo") }
            )
        }

        composable("detalle_cultivo") {
            DetalleCultivoScreen(onNavegarAtras = { navController.popBackStack() })
        }

        composable("confirmacion_cultivo") {
            ConfirmacionCultivoScreen(onNavegarListo = { navController.popBackStack("mis_cultivos", inclusive = false) })
        }

        // --- MÓDULO DE GASTOS ---
        composable("mis_gastos") {
            MisGastosScreen(
                onNavegarAtras = { navController.popBackStack() },
                onNavegarRegistrarGasto = { navController.navigate("registrar_gasto") }
            )
        }

        composable("registrar_gasto") {
            RegistrarGastosScreen(
                onNavegarAtras = { navController.popBackStack() },
                onGuardarGasto = { navController.navigate("confirmacion_gasto") }
            )
        }

        composable("confirmacion_gasto") {
            ConfirmacionGastoScreen(onNavegarListo = { navController.popBackStack("mis_gastos", inclusive = false) })
        }

        // --- MÓDULO DE INSUMOS ---
        composable("mis_insumos") {
            MisInsumosScreen(
                onNavegarAtras = { navController.popBackStack() },
                onNavegarRegistrarInsumo = { navController.navigate("registro_insumos") }
            )
        }

        composable("registro_insumos") {
            RegistroInsumosScreen(
                onNavegarAtras = { navController.popBackStack() },
                onGuardarInsumo = { navController.navigate("confirmacion_insumo") }
            )
        }

        composable("confirmacion_insumo") {
            ConfirmacionInsumoScreen(onNavegarListo = { navController.popBackStack("mis_insumos", inclusive = false) })
        }

        // --- MÓDULO DE REPORTES ---
        composable("reportes") {
            ReportesScreen(
                onNavegarAtras = { navController.popBackStack() },
                onNavegarDetalle = { tipo -> navController.navigate("reporte_detalle/$tipo") }
            )
        }

        composable(
            route = "reporte_detalle/{tipo}",
            arguments = listOf(navArgument("tipo") { type = NavType.StringType })
        ) { backStackEntry ->
            val tipo = backStackEntry.arguments?.getString("tipo") ?: "gastos"
            ReporteDetalleScreen(
                tipoReporte = tipo,
                onNavegarAtras = { navController.popBackStack() }
            )
        }

        // --- MÓDULO DE PERFIL ---
        composable("perfil") {
            PerfilScreen(
                onNavegarCultivos = { navController.navigate("mis_cultivos") },
                onNavegarInsumos = { navController.navigate("mis_insumos") },
                onNavegarGastos = { navController.navigate("mis_gastos") },
                onNavegarReportes = { navController.navigate("reportes") },
                onNavegarInicio = {
                    navController.navigate("inicio") {
                        popUpTo("inicio") { inclusive = true }
                    }
                },
                onNavegarInfoPersonal = { navController.navigate("info_personal") },
                onNavegarConfiguracion = { navController.navigate("configuracion") },
                // Te lleva a la pantalla de recuperación que ya habíamos hecho
                onNavegarCambiarContrasena = { navController.navigate("recuperar_inicio") },
                onNavegarAcercaDe = { navController.navigate("acerca_de") },
                onCerrarSesion = {
                    navController.navigate("bienvenida") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("info_personal") {
            InformacionPersonalScreen(onNavegarAtras = { navController.popBackStack() })
        }

        composable("configuracion") {
            ConfiguracionScreen(onNavegarAtras = { navController.popBackStack() })
        }

        composable("acerca_de") {
            AcercaDeScreen(onNavegarAtras = { navController.popBackStack() })
        }
        // --- MÓDULO DE ADMINISTRADOR ---
        composable("admin_usuarios") {
            AdminUsuariosScreen(
                onCerrarSesion = {
                    navController.navigate("bienvenida") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}