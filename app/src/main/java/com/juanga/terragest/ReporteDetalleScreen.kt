package com.juanga.terragest

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReporteDetalleScreen(
    tipoReporte: String,
    onNavegarAtras: () -> Unit = {},
    onNavegarGenerado: (String, String, String) -> Unit = { _, _, _ -> }
) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val idAgricultor = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val formatoMoneda = NumberFormat.getNumberInstance(Locale("es", "CO"))

    var cultivoSeleccionado by remember { mutableStateOf("Todos los cultivos") }
    var expandedCultivo by remember { mutableStateOf(false) }
    var cultivosList by remember { mutableStateOf(listOf("Todos los cultivos")) }

    var periodoSeleccionado by remember { mutableStateOf("Todo el historial") }
    var expandedPeriodo by remember { mutableStateOf(false) }
    val periodosList = listOf("Todo el historial", "Últimos 30 días", "Últimos 90 días", "Este año")

    var totalGastos by remember { mutableStateOf(0.0) }
    var totalInsumos by remember { mutableStateOf(0.0) }
    var totalIngresos by remember { mutableStateOf(0.0) }
    var cantidadRegistros by remember { mutableStateOf(0) }
    var cantidadCultivos by remember { mutableStateOf(0) }

    fun estaDentroDelPeriodo(fechaStr: String?): Boolean {
        if (fechaStr.isNullOrEmpty() || periodoSeleccionado == "Todo el historial") return true
        return try {
            val formato = if (fechaStr.contains("/")) "dd/MM/yyyy" else "ddMMyyyy"
            val sdf = SimpleDateFormat(formato, Locale.getDefault())
            val date = sdf.parse(fechaStr) ?: return true
            val diffDays = (System.currentTimeMillis() - date.time) / (1000 * 60 * 60 * 24)
            when (periodoSeleccionado) {
                "Últimos 30 días" -> diffDays <= 30
                "Últimos 90 días" -> diffDays <= 90
                "Este año" -> {
                    val calActual = Calendar.getInstance()
                    val calDoc = Calendar.getInstance().apply { time = date }
                    calActual.get(Calendar.YEAR) == calDoc.get(Calendar.YEAR)
                }
                else -> true
            }
        } catch (e: Exception) { true }
    }

    LaunchedEffect(Unit) {
        if (idAgricultor.isNotEmpty()) {
            db.collection("cultivos").whereEqualTo("idAgricultor", idAgricultor).get()
                .addOnSuccessListener { res ->
                    val nombres = res.documents.mapNotNull { it.getString("nombre") }
                    cultivosList = listOf("Todos los cultivos") + nombres
                    cantidadCultivos = res.size()
                }
        }
    }

    LaunchedEffect(cultivoSeleccionado, periodoSeleccionado) {
        if (idAgricultor.isEmpty()) return@LaunchedEffect

        db.collection("gastos").whereEqualTo("idAgricultor", idAgricultor).get().addOnSuccessListener { docs ->
            var sumaGastos = 0.0
            var countGastos = 0
            for (doc in docs) {
                val cult = doc.getString("cultivo") ?: ""
                val fecha = doc.getString("fecha") ?: ""
                if ((cultivoSeleccionado == "Todos los cultivos" || cult == cultivoSeleccionado) && estaDentroDelPeriodo(fecha)) {
                    sumaGastos += doc.getDouble("valor") ?: 0.0
                    countGastos++
                }
            }
            totalGastos = sumaGastos
            if (tipoReporte == "gastos") cantidadRegistros = countGastos
        }

        db.collection("insumos").whereEqualTo("idAgricultor", idAgricultor).get().addOnSuccessListener { docs ->
            var sumaInsumos = 0.0
            var countInsumos = 0
            for (doc in docs) {
                val cult = doc.getString("cultivo") ?: ""
                val fecha = doc.getString("fechaCompra") ?: ""
                if ((cultivoSeleccionado == "Todos los cultivos" || cult == cultivoSeleccionado) && estaDentroDelPeriodo(fecha)) {
                    sumaInsumos += doc.getDouble("precio") ?: 0.0
                    countInsumos++
                }
            }
            totalInsumos = sumaInsumos
            if (tipoReporte == "insumos") cantidadRegistros = countInsumos
        }

        db.collection("ventas").whereEqualTo("idAgricultor", idAgricultor).get().addOnSuccessListener { docs ->
            var sumaVentas = 0.0
            var countVentas = 0
            for (doc in docs) {
                val cult = doc.getString("cultivo") ?: ""
                val fecha = doc.getString("fecha") ?: ""
                if ((cultivoSeleccionado == "Todos los cultivos" || cult == cultivoSeleccionado) && estaDentroDelPeriodo(fecha)) {
                    sumaVentas += doc.getDouble("valorTotal") ?: 0.0
                    countVentas++
                }
            }
            totalIngresos = sumaVentas
            if (tipoReporte == "produccion" || tipoReporte == "rentabilidad") cantidadRegistros = countVentas
        }
    }

    val titulo = when (tipoReporte) {
        "gastos" -> "Reporte de gastos"
        "produccion" -> "Reporte de producción"
        "rentabilidad" -> "Reporte de rentabilidad"
        "insumos" -> "Reporte de insumos"
        else -> "Reporte"
    }

    val egresosTotales = totalGastos + totalInsumos
    val utilidadNeta = totalIngresos - egresosTotales
    val porcentajeRentabilidad = if (egresosTotales > 0) (utilidadNeta / egresosTotales) * 100 else if (totalIngresos > 0) 100.0 else 0.0

    val resultados = when (tipoReporte) {
        "gastos" -> listOf("Gastos Totales" to "$ ${formatoMoneda.format(totalGastos)}", "Total de registros" to "$cantidadRegistros", "Cultivos analizados" to if (cultivoSeleccionado == "Todos los cultivos") "$cantidadCultivos" else "1")
        "produccion" -> listOf("Ingresos por Producción" to "$ ${formatoMoneda.format(totalIngresos)}", "Total de ventas registradas" to "$cantidadRegistros", "Cultivos analizados" to if (cultivoSeleccionado == "Todos los cultivos") "$cantidadCultivos" else "1")
        "rentabilidad" -> listOf("Ingresos Totales (Ventas)" to "$ ${formatoMoneda.format(totalIngresos)}", "Egresos (Gastos + Insumos)" to "$ ${formatoMoneda.format(egresosTotales)}", "Utilidad neta" to "$ ${formatoMoneda.format(utilidadNeta)}", "Rentabilidad" to "${String.format("%.1f", porcentajeRentabilidad)} %")
        "insumos" -> listOf("Inversión en Insumos" to "$ ${formatoMoneda.format(totalInsumos)}", "Total de registros" to "$cantidadRegistros", "Cultivos analizados" to if (cultivoSeleccionado == "Todos los cultivos") "$cantidadCultivos" else "1")
        else -> emptyList()
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF2F2F2)).padding(24.dp).verticalScroll(rememberScrollState())) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("<", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.clickable { onNavegarAtras() }.padding(end = 16.dp, top = 8.dp, bottom = 8.dp))
            Text(titulo, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.width(32.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Cultivo", fontWeight = FontWeight.Bold, color = Color.Black)
        ExposedDropdownMenuBox(expanded = expandedCultivo, onExpandedChange = { expandedCultivo = !expandedCultivo }) {
            OutlinedTextField(
                value = cultivoSeleccionado, onValueChange = {}, readOnly = true, modifier = Modifier.fillMaxWidth().menuAnchor(),
                shape = RoundedCornerShape(12.dp), trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCultivo) },
                textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedBorderColor = Color.LightGray, unfocusedBorderColor = Color.LightGray)
            )
            ExposedDropdownMenu(expanded = expandedCultivo, onDismissRequest = { expandedCultivo = false }, modifier = Modifier.background(Color.White)) {
                cultivosList.forEach { sel ->
                    DropdownMenuItem(text = { Text(sel, color = Color.Black) }, onClick = { cultivoSeleccionado = sel; expandedCultivo = false })
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Periodo", fontWeight = FontWeight.Bold, color = Color.Black)
        ExposedDropdownMenuBox(expanded = expandedPeriodo, onExpandedChange = { expandedPeriodo = !expandedPeriodo }) {
            OutlinedTextField(
                value = periodoSeleccionado, onValueChange = {}, readOnly = true, modifier = Modifier.fillMaxWidth().menuAnchor(),
                shape = RoundedCornerShape(12.dp), trailingIcon = { Icon(painterResource(id = R.drawable.gen_flechaabajo), contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp)) },
                textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedBorderColor = Color.LightGray, unfocusedBorderColor = Color.LightGray)
            )
            ExposedDropdownMenu(expanded = expandedPeriodo, onDismissRequest = { expandedPeriodo = false }, modifier = Modifier.background(Color.White)) {
                periodosList.forEach { sel ->
                    DropdownMenuItem(text = { Text(sel, color = Color.Black) }, onClick = { periodoSeleccionado = sel; expandedPeriodo = false })
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // BOTÓN ÚNICO CENTRADO
        Button(
            onClick = {
                val cultivoSafe = java.net.URLEncoder.encode(cultivoSeleccionado, "UTF-8")
                val periodoSafe = java.net.URLEncoder.encode(periodoSeleccionado, "UTF-8")
                onNavegarGenerado(tipoReporte, cultivoSafe, periodoSafe)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF87BF4E)),
            shape = RoundedCornerShape(25.dp), modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Generar reporte", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Resumen en pantalla", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(16.dp))

        resultados.forEach { (label, valor) ->
            OutlinedTextField(
                value = valor, onValueChange = {}, readOnly = true,
                label = { Text(label, color = Color.Gray) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                shape = RoundedCornerShape(12.dp),
                textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black, fontWeight = FontWeight.Medium),
                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedBorderColor = Color.LightGray, unfocusedBorderColor = Color.LightGray)
            )
        }
    }
}