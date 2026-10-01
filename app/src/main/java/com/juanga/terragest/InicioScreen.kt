package com.juanga.terragest

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

data class ActividadReciente(
    val titulo: String,
    val subtitulo: String,
    val valor: String,
    val fechaStr: String,
    val fechaMs: Long,
    val colorValor: Color
)

@Composable
fun InicioScreen(
    onNavegarCultivos: () -> Unit = {},
    onNavegarInsumos: () -> Unit = {},
    onNavegarGastos: () -> Unit = {},
    onNavegarReportes: () -> Unit = {},
    onNavegarVentas: () -> Unit = {},
    onNavegarPerfil: () -> Unit = {}
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val idAgricultor = auth.currentUser?.uid ?: ""
    var nombreUsuario by remember { mutableStateOf(auth.currentUser?.email?.substringBefore("@")?.replaceFirstChar { it.uppercase() } ?: "Agricultor") }

    var cultivosActivos by remember { mutableStateOf("0") }
    var gastosMes by remember { mutableStateOf("$ 0") }
    var ingresosMes by remember { mutableStateOf("$ 0") }
    var rutaFotoPerfil by remember { mutableStateOf("") }

    // Listas separadas para combinar después
    var listaGastos by remember { mutableStateOf(emptyList<ActividadReciente>()) }
    var listaVentas by remember { mutableStateOf(emptyList<ActividadReciente>()) }
    var listaCultivos by remember { mutableStateOf(emptyList<ActividadReciente>()) }

    val formatoMoneda = NumberFormat.getNumberInstance(Locale("es", "CO"))

    // Función para convertir la fecha String a Milisegundos para poder ordenarlas
    fun parseDate(fecha: String?): Long {
        if (fecha.isNullOrEmpty()) return 0L
        return try {
            val formato = if (fecha.contains("/")) "dd/MM/yyyy" else "ddMMyyyy"
            SimpleDateFormat(formato, Locale.getDefault()).parse(fecha)?.time ?: 0L
        } catch (e: Exception) { 0L }
    }

    LaunchedEffect(Unit) {
        if (idAgricultor.isNotEmpty()) {
            db.collection("usuarios").document(idAgricultor).addSnapshotListener { snapshot, _ ->
                rutaFotoPerfil = snapshot?.getString("rutaFoto") ?: ""
                val nombreBD = snapshot?.getString("nombres")
                if (!nombreBD.isNullOrEmpty()) nombreUsuario = nombreBD
            }

            db.collection("cultivos").whereEqualTo("idAgricultor", idAgricultor).addSnapshotListener { snapshot, _ ->
                cultivosActivos = snapshot?.size()?.toString() ?: "0"
                listaCultivos = snapshot?.documents?.mapNotNull { doc ->
                    val fecha = doc.getString("fechaSiembra") ?: ""
                    ActividadReciente("Nuevo Cultivo", "Variedad: ${doc.getString("variedad")}", "Agregado", fecha, parseDate(fecha), Color.DarkGray)
                } ?: emptyList()
            }

            db.collection("gastos").whereEqualTo("idAgricultor", idAgricultor).addSnapshotListener { snapshot, _ ->
                val suma = snapshot?.documents?.sumOf { it.getDouble("valor") ?: 0.0 } ?: 0.0
                gastosMes = "$ ${formatoMoneda.format(suma)}"
                listaGastos = snapshot?.documents?.mapNotNull { doc ->
                    val fecha = doc.getString("fecha") ?: ""
                    val valor = doc.getDouble("valor") ?: 0.0
                    ActividadReciente("Gasto en ${doc.getString("cultivo")}", doc.getString("descripcion") ?: "", "-$ ${formatoMoneda.format(valor)}", fecha, parseDate(fecha), Color.Red)
                } ?: emptyList()
            }

            db.collection("ventas").whereEqualTo("idAgricultor", idAgricultor).addSnapshotListener { snapshot, _ ->
                val suma = snapshot?.documents?.sumOf { it.getDouble("valorTotal") ?: 0.0 } ?: 0.0
                ingresosMes = "$ ${formatoMoneda.format(suma)}"
                listaVentas = snapshot?.documents?.mapNotNull { doc ->
                    val fecha = doc.getString("fecha") ?: ""
                    val valor = doc.getDouble("valorTotal") ?: 0.0
                    ActividadReciente("Venta de ${doc.getString("cultivo")}", "Comprador: ${doc.getString("comprador")}", "+$ ${formatoMoneda.format(valor)}", fecha, parseDate(fecha), Color(0xFF3C733F))
                } ?: emptyList()
            }
        }
    }

    // Combinar, ordenar de más reciente a más antiguo, y tomar los 5 primeros
    val listaActividad = (listaGastos + listaVentas + listaCultivos)
        .sortedByDescending { it.fechaMs }
        .take(5)

    Scaffold(
        bottomBar = {
            BarraNavegacionInferiorRediseñada(
                onNavegarInicio = {},
                onNavegarVentas = onNavegarVentas,
                onNavegarPerfil = onNavegarPerfil
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().background(Color(0xFFF2F2F2)).padding(paddingValues).verticalScroll(rememberScrollState()).padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                if (rutaFotoPerfil.isNotEmpty()) {
                    val bitmap = BitmapFactory.decodeFile(rutaFotoPerfil)
                    if (bitmap != null) {
                        Image(bitmap = bitmap.asImageBitmap(), contentDescription = "Foto", contentScale = ContentScale.Crop, modifier = Modifier.size(50.dp).clip(CircleShape))
                    } else {
                        Image(painter = painterResource(id = R.drawable.gen_personaicon), contentDescription = "Foto", modifier = Modifier.size(50.dp))
                    }
                } else {
                    Image(painter = painterResource(id = R.drawable.gen_personaicon), contentDescription = "Foto", modifier = Modifier.size(50.dp))
                }

                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = "¡Hola, $nombreUsuario!", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(text = "Bienvenid@ a TERRAGEST", fontSize = 14.sp, color = Color.DarkGray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Resumen financiero", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Box(modifier = Modifier.background(Color(0xFFF2F2F2), RoundedCornerShape(12.dp)).padding(horizontal = 12.dp, vertical = 4.dp)) {
                            Text(text = "Global", fontSize = 12.sp, color = Color.DarkGray)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color(0xFFF2F2F2), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        ItemResumen("Ingresos (Ventas)", ingresosMes, R.drawable.paninicio_logodineropequeno, Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(8.dp))
                        ItemResumen("Gastos Totales", gastosMes, R.drawable.paninicio_modenalogosincolor, Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        ItemResumen("Cultivos activos", cultivosActivos, R.drawable.paninicio_logoplantapequeno, Modifier.weight(1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Registros", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                ItemAccesoRapido("Mis cultivos", R.drawable.paninicio_logoplantamano, onNavegarCultivos)
                ItemAccesoRapido("Insumos", R.drawable.paninicio_logocarpeta, onNavegarInsumos)
                ItemAccesoRapido("Gastos", R.drawable.paninicio_logomoneda, onNavegarGastos)
                ItemAccesoRapido("Reportes", R.drawable.paninicio_documento, onNavegarReportes)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Últimos movimientos", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
            Spacer(modifier = Modifier.height(12.dp))

            if (listaActividad.isEmpty()) {
                Text("Aún no hay registros recientes.", color = Color.Gray, fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(16.dp))
            } else {
                listaActividad.forEach { actividad ->
                    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = actividad.titulo, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                Text(text = actividad.subtitulo, fontSize = 12.sp, color = Color.Gray)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = actividad.valor, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = actividad.colorValor)
                                Text(text = actividad.fechaStr, fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

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
        Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), modifier = Modifier.size(60.dp)) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Image(painter = painterResource(id = icono), contentDescription = texto, modifier = Modifier.size(40.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = texto, fontSize = 12.sp, color = Color.Black)
    }
}

@Composable
fun BarraNavegacionInferiorRediseñada(onNavegarInicio: () -> Unit, onNavegarVentas: () -> Unit, onNavegarPerfil: () -> Unit) {
    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        NavigationBarItem(icon = { Image(painterResource(id = R.drawable.paninicio_logohome), contentDescription = "Inicio", modifier = Modifier.size(24.dp)) }, label = { Text("Inicio", fontSize = 10.sp) }, selected = true, onClick = onNavegarInicio, colors = NavigationBarItemDefaults.colors(indicatorColor = Color(0xFFE8F5E9)))
        NavigationBarItem(icon = { Image(painterResource(id = R.drawable.paninicio_logodineropequeno), contentDescription = "Ventas", modifier = Modifier.size(24.dp)) }, label = { Text("Ventas", fontSize = 10.sp) }, selected = false, onClick = onNavegarVentas)
        NavigationBarItem(icon = { Image(painterResource(id = R.drawable.panperf_perfillogopequeno), contentDescription = "Perfil", modifier = Modifier.size(24.dp)) }, label = { Text("Perfil", fontSize = 10.sp) }, selected = false, onClick = onNavegarPerfil)
    }
}