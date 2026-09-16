package com.juanga.terragest

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Modelo de datos temporal para la base de datos
data class Actividad(val titulo: String, val subtitulo: String, val valor: String, val fecha: String)

@Composable
fun InicioScreen(
    onNavegarCultivos: () -> Unit = {},
    onNavegarGastos: () -> Unit = {},
    onNavegarReportes: () -> Unit = {},
    onNavegarPerfil: () -> Unit = {}
) {
    // VARIABLES DE ESTADO (Listas para recibir datos de Firebase)
    var nombreUsuario by remember { mutableStateOf("...") }
    var cultivosActivos by remember { mutableStateOf("0") }
    var gastosMes by remember { mutableStateOf("$ 0") }
    var produccion by remember { mutableStateOf("0 kg") }
    var rentabilidad by remember { mutableStateOf("0%") }

    // Lista vacía simulando que Firebase aún no trae datos
    var actividades by remember { mutableStateOf(listOf<Actividad>()) }

    Scaffold(
        bottomBar = {
            BarraNavegacionInferior(
                onNavegarCultivos = onNavegarCultivos,
                onNavegarGastos = onNavegarGastos,
                onNavegarReportes = onNavegarReportes,
                onNavegarPerfil = onNavegarPerfil
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF2F2F2))
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // --- ENCABEZADO ---
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(id = R.drawable.gen_personaicon),
                    contentDescription = "Foto",
                    modifier = Modifier.size(50.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = "¡Hola, $nombreUsuario!", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(text = "Bienvenida a TERRAGEST", fontSize = 14.sp, color = Color.DarkGray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- RESUMEN GENERAL ---
            Card(
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Resumen general", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Box(modifier = Modifier.background(Color(0xFFF2F2F2), RoundedCornerShape(12.dp)).padding(horizontal = 12.dp, vertical = 4.dp)) {
                            Text(text = "Mes actual", fontSize = 12.sp, color = Color.DarkGray)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color(0xFFF2F2F2), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        ItemResumen("Cultivos activos", cultivosActivos, R.drawable.paninicio_logoplantapequeno, Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(8.dp))
                        ItemResumen("Gastos del mes", gastosMes, R.drawable.paninicio_logodineropequeno, Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        ItemResumen("Producción", produccion, R.drawable.paninicio_logotierrapequeno, Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(8.dp))
                        ItemResumen("Rentabilidad", rentabilidad, R.drawable.paninicio_logograficapequeno, Modifier.weight(1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- ACCESOS RÁPIDOS ---
            Text(text = "Accesos rápidos", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                ItemAccesoRapido("Mis cultivos", R.drawable.paninicio_logoplantamano, onNavegarCultivos)
                ItemAccesoRapido("Insumos", R.drawable.paninicio_logocarpeta, {})
                ItemAccesoRapido("Gastos", R.drawable.paninicio_logomoneda, onNavegarGastos)
                ItemAccesoRapido("Reportes", R.drawable.paninicio_documento, onNavegarReportes)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- ACTIVIDAD RECIENTE ---
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Actividad reciente", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(text = "Ver todo", fontSize = 14.sp, color = Color(0xFF3C733F), fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))

            if (actividades.isEmpty()) {
                Text(
                    text = "Aún no hay registros recientes.",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                )
            } else {
                actividades.forEach { actividad ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(text = actividad.titulo, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                Text(text = actividad.subtitulo, fontSize = 12.sp, color = Color.Gray)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = actividad.valor, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                Text(text = actividad.fecha, fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// LAS FUNCIONES QUE FALTABAN ESTÁN AQUÍ ABAJO (NO LAS BORRES)
// =========================================================================

@Composable
fun ItemResumen(titulo: String, valor: String, icono: Int, modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        Image(painter = painterResource(id = icono), contentDescription = null, modifier = Modifier.size(32.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = titulo, fontSize = 10.sp, color = Color.Gray)
            Text(text = valor, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}

@Composable
fun ItemAccesoRapido(texto: String, icono: Int, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.size(60.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Image(painter = painterResource(id = icono), contentDescription = texto, modifier = Modifier.size(40.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = texto, fontSize = 12.sp, color = Color.Black)
    }
}

@Composable
fun BarraNavegacionInferior(
    onNavegarCultivos: () -> Unit,
    onNavegarGastos: () -> Unit,
    onNavegarReportes: () -> Unit,
    onNavegarPerfil: () -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Image(painterResource(id = R.drawable.paninicio_logohome), contentDescription = "Inicio", modifier = Modifier.size(24.dp)) },
            label = { Text("Inicio", fontSize = 10.sp) },
            selected = true,
            onClick = { /* Ya estamos aquí */ },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color(0xFFE8F5E9))
        )
        NavigationBarItem(
            icon = { Image(painterResource(id = R.drawable.paninicio_logomanoplantasincolor), contentDescription = "Cultivos", modifier = Modifier.size(24.dp)) },
            label = { Text("Cultivos", fontSize = 10.sp) },
            selected = false,
            onClick = onNavegarCultivos
        )
        NavigationBarItem(
            icon = { Image(painterResource(id = R.drawable.paninicio_modenalogosincolor), contentDescription = "Gastos", modifier = Modifier.size(24.dp)) },
            label = { Text("Gastos", fontSize = 10.sp) },
            selected = false,
            onClick = onNavegarGastos
        )
        NavigationBarItem(
            icon = { Image(painterResource(id = R.drawable.paninicio_documento), contentDescription = "Reportes", modifier = Modifier.size(24.dp)) },
            label = { Text("Reportes", fontSize = 10.sp) },
            selected = false,
            onClick = onNavegarReportes
        )
        NavigationBarItem(
            icon = { Image(painterResource(id = R.drawable.panperf_perfillogopequeno), contentDescription = "Perfil", modifier = Modifier.size(24.dp)) },
            label = { Text("Perfil", fontSize = 10.sp) },
            selected = false,
            onClick = onNavegarPerfil
        )
    }
}