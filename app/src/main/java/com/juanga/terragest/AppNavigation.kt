package com.juanga.terragest

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    val usuarioActual = auth.currentUser
    val rutaInicial = if (usuarioActual != null) "inicio" else "bienvenida"

    NavHost(navController = navController, startDestination = rutaInicial) {

        composable("bienvenida") { BienvenidaScreen(onNavegarLogin = { navController.navigate("login") }, onNavegarRegistro = { navController.navigate("registro_paso1") }) }

        composable("login") {
            InicioSesionScreen(
                onNavegarRecuperar = { navController.navigate("recuperar_inicio") },
                onNavegarAtras = { navController.popBackStack() },
                onNavegarInicio = { navController.navigate("inicio") { popUpTo("bienvenida") { inclusive = false } } },
                onNavegarAdmin = { navController.navigate("admin_usuarios") { popUpTo("bienvenida") { inclusive = false } } }
            )
        }

        composable("registro_paso1") { CrearCuentaPaso1Screen(onNavegarAtras = { navController.popBackStack() }, onNavegarPaso2 = { navController.navigate("registro_paso2") }, onNavegarLogin = { navController.navigate("login") }) }
        composable("registro_paso2") { CrearCuentaPaso2Screen(onNavegarAtras = { navController.popBackStack() }, onNavegarExito = { navController.navigate("exito_registro") }, onNavegarLogin = { navController.navigate("login") }) }

        composable("recuperar_inicio") { RestablecerInicioScreen(onNavegarAtras = { navController.popBackStack() }, onNavegarLogin = { navController.navigate("login") }) }
        composable("recuperar_paso1") { RestablecerPaso1Screen(onNavegarAtras = { navController.popBackStack() }, onNavegarPaso2 = { metodo -> navController.navigate("recuperar_paso2/$metodo") }) }
        composable("recuperar_paso2/{metodo}", arguments = listOf(navArgument("metodo") { type = NavType.StringType })) { backStackEntry ->
            RestablecerPaso2Screen(metodo = backStackEntry.arguments?.getString("metodo") ?: "correo", onNavegarAtras = { navController.popBackStack() }, onNavegarPaso3 = { navController.navigate("recuperar_paso3") })
        }
        composable("recuperar_paso3") { RestablecerPaso3Screen(onNavegarAtras = { navController.popBackStack() }, onNavegarExito = { navController.navigate("exito_recuperacion") }) }

        composable("exito_registro") { ExitoScreen(mensajePrincipal = "Cuenta creada\ncorrectamente!", textoDelBoton = "Iniciar sesión", onNavegarLogin = { navController.navigate("login") { popUpTo("bienvenida") { inclusive = false } } }, onNavegarInicio = { navController.popBackStack("bienvenida", inclusive = false) }) }
        composable("exito_recuperacion") { ExitoScreen(mensajePrincipal = "¡Contraseña actualizada\ncon éxito!", textoDelBoton = "Iniciar sesión", onNavegarLogin = { navController.navigate("login") { popUpTo("bienvenida") { inclusive = false } } }, onNavegarInicio = { navController.popBackStack("bienvenida", inclusive = false) }) }

        // --- DASHBOARD (INICIO) ---
        composable("inicio") {
            InicioScreen(
                onNavegarCultivos = { navController.navigate("mis_cultivos") },
                onNavegarInsumos = { navController.navigate("mis_insumos") },
                onNavegarGastos = { navController.navigate("mis_gastos") },
                onNavegarReportes = { navController.navigate("reportes") },
                onNavegarVentas = { navController.navigate("mis_ventas") },
                onNavegarPerfil = { navController.navigate("perfil") }
            )
        }

        // --- MÓDULO DE VENTAS (NUEVO) ---
        composable("mis_ventas") {
            MisVentasScreen(
                onNavegarInicio = { navController.navigate("inicio") { popUpTo("inicio") { inclusive = true } } },
                onNavegarPerfil = { navController.navigate("perfil") },
                onNavegarRegistrarVenta = { navController.navigate("registrar_venta") }
            )
        }
        composable("registrar_venta") {
            RegistrarVentaScreen(
                onNavegarAtras = { navController.popBackStack() },
                onGuardarVenta = { navController.popBackStack() }
            )
        }

        // --- CULTIVOS ---
        composable("mis_cultivos") {
            MisCultivosScreen(
                onNavegarAtras = { navController.popBackStack() },
                onNavegarNuevoCultivo = { navController.navigate("nuevo_cultivo") }
                // Se eliminó onEditarCultivo porque ahora todo se hace mediante el cuadro de diálogo interno
            )
        }
        composable("nuevo_cultivo") {
            NuevoCultivoScreen(
                cultivoId = null, // La creación es siempre nula, la edición es en diálogo
                onNavegarAtras = { navController.popBackStack() },
                onGuardarCultivo = { navController.popBackStack() }
            )
        }

        // --- GASTOS ---
        composable("mis_gastos") { MisGastosScreen(onNavegarAtras = { navController.popBackStack() }, onNavegarRegistrarGasto = { navController.navigate("registrar_gasto") }) }
        composable("registrar_gasto") { RegistrarGastosScreen(onNavegarAtras = { navController.popBackStack() }, onGuardarGasto = { navController.navigate("confirmacion_gasto") }) }
        composable("confirmacion_gasto") { ConfirmacionGastoScreen(onNavegarListo = { navController.popBackStack("mis_gastos", inclusive = false) }) }

        // --- INSUMOS ---
        composable("mis_insumos") { MisInsumosScreen(onNavegarAtras = { navController.popBackStack() }, onNavegarRegistrarInsumo = { navController.navigate("registro_insumos") }) }
        composable("registro_insumos") { RegistroInsumosScreen(onNavegarAtras = { navController.popBackStack() }, onGuardarInsumo = { navController.navigate("confirmacion_insumo") }) }
        composable("confirmacion_insumo") { ConfirmacionInsumoScreen(onNavegarListo = { navController.popBackStack("mis_insumos", inclusive = false) }) }

        // --- REPORTES ---
        composable("reportes") { ReportesScreen(onNavegarAtras = { navController.popBackStack() }, onNavegarDetalle = { tipo -> navController.navigate("reporte_detalle/$tipo") }) }

        composable("reporte_detalle/{tipo}", arguments = listOf(navArgument("tipo") { type = NavType.StringType })) { backStackEntry ->
            ReporteDetalleScreen(
                tipoReporte = backStackEntry.arguments?.getString("tipo") ?: "gastos",
                onNavegarAtras = { navController.popBackStack() },
                onNavegarGenerado = { tipo, cultivo, periodo ->
                    navController.navigate("reporte_generado/$tipo/$cultivo/$periodo")
                }
            )
        }

        composable(
            route = "reporte_generado/{tipo}/{cultivo}/{periodo}",
            arguments = listOf(
                navArgument("tipo") { type = NavType.StringType },
                navArgument("cultivo") { type = NavType.StringType },
                navArgument("periodo") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val tipo = backStackEntry.arguments?.getString("tipo") ?: "gastos"
            val cultivo = java.net.URLDecoder.decode(backStackEntry.arguments?.getString("cultivo") ?: "Todos los cultivos", "UTF-8")
            val periodo = java.net.URLDecoder.decode(backStackEntry.arguments?.getString("periodo") ?: "Todo el historial", "UTF-8")

            ReporteGeneradoScreen(
                tipoReporte = tipo,
                cultivo = cultivo,
                periodo = periodo,
                onNavegarAtras = { navController.popBackStack() }
            )
        }

        // --- PERFIL ---
        composable("perfil") {
            PerfilScreen(
                onNavegarInicio = { navController.navigate("inicio") { popUpTo("inicio") { inclusive = true } } },
                onNavegarVentas = { navController.navigate("mis_ventas") },
                onNavegarInfoPersonal = { navController.navigate("info_personal") },
                onNavegarConfiguracion = { navController.navigate("configuracion") },
                onNavegarCambiarContrasena = { navController.navigate("recuperar_inicio") },
                onNavegarAcercaDe = { navController.navigate("acerca_de") },
                onCerrarSesion = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("bienvenida") { popUpTo(0) { inclusive = true } }
                }
            )
        }
        composable("info_personal") { InformacionPersonalScreen(onNavegarAtras = { navController.popBackStack() }) }
        composable("configuracion") { ConfiguracionScreen(onNavegarAtras = { navController.popBackStack() }) }
        composable("acerca_de") { AcercaDeScreen(onNavegarAtras = { navController.popBackStack() }) }
        composable("admin_usuarios") { AdminUsuariosScreen(onCerrarSesion = { FirebaseAuth.getInstance().signOut(); navController.navigate("bienvenida") { popUpTo(0) { inclusive = true } } }) }
    }
}