package com.juanga.terragest

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class RegistroReporte(val titulo: String, val subtitulo: String, val valor: Double, val fecha: String)

@Composable
fun ReporteGeneradoScreen(
    tipoReporte: String,
    cultivo: String,
    periodo: String,
    onNavegarAtras: () -> Unit = {}
) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val idAgricultor = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var listaRegistros by remember { mutableStateOf(listOf<RegistroReporte>()) }
    var cargando by remember { mutableStateOf(true) }

    val formatoMoneda = NumberFormat.getNumberInstance(Locale("es", "CO"))

    // Selector nativo para crear y guardar el archivo PDF
    val creacionPdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri: Uri? ->
        if (uri != null) {
            generarYGuardarPDF(context, uri, tipoReporte, cultivo, periodo, listaRegistros)
        } else {
            Toast.makeText(context, "Exportación cancelada", Toast.LENGTH_SHORT).show()
        }
    }

    fun estaDentroDelPeriodo(fechaStr: String?): Boolean {
        if (fechaStr.isNullOrEmpty() || periodo == "Todo el historial") return true
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = sdf.parse(fechaStr) ?: return false
            val diffDays = (System.currentTimeMillis() - date.time) / (1000 * 60 * 60 * 24)
            when (periodo) {
                "Últimos 30 días" -> diffDays <= 30
                "Últimos 90 días" -> diffDays <= 90
                "Este año" -> {
                    val calActual = Calendar.getInstance()
                    val calDoc = Calendar.getInstance().apply { time = date }
                    calActual.get(Calendar.YEAR) == calDoc.get(Calendar.YEAR)
                }
                else -> true
            }
        } catch (e: Exception) { false }
    }

    LaunchedEffect(Unit) {
        val registrosTemp = mutableListOf<RegistroReporte>()

        when (tipoReporte) {
            "gastos" -> {
                db.collection("gastos").whereEqualTo("idAgricultor", idAgricultor).get().addOnSuccessListener { docs ->
                    for (doc in docs) {
                        val cult = doc.getString("cultivo") ?: ""
                        val fecha = doc.getString("fecha") ?: ""
                        if ((cultivo == "Todos los cultivos" || cult == cultivo) && estaDentroDelPeriodo(fecha)) {
                            registrosTemp.add(RegistroReporte(doc.getString("descripcion") ?: "Gasto", cult, doc.getDouble("valor") ?: 0.0, fecha))
                        }
                    }
                    listaRegistros = registrosTemp.sortedByDescending { it.fecha }
                    cargando = false
                }
            }
            "insumos" -> {
                db.collection("insumos").whereEqualTo("idAgricultor", idAgricultor).get().addOnSuccessListener { docs ->
                    for (doc in docs) {
                        val cult = doc.getString("cultivo") ?: ""
                        val fecha = doc.getString("fechaCompra") ?: ""
                        if ((cultivo == "Todos los cultivos" || cult == cultivo) && estaDentroDelPeriodo(fecha)) {
                            registrosTemp.add(RegistroReporte(doc.getString("nombre") ?: "Insumo", cult, doc.getDouble("precio") ?: 0.0, fecha))
                        }
                    }
                    listaRegistros = registrosTemp.sortedByDescending { it.fecha }
                    cargando = false
                }
            }
            "produccion", "rentabilidad" -> {
                db.collection("ventas").whereEqualTo("idAgricultor", idAgricultor).get().addOnSuccessListener { docs ->
                    for (doc in docs) {
                        val cult = doc.getString("cultivo") ?: ""
                        val fecha = doc.getString("fecha") ?: ""
                        if ((cultivo == "Todos los cultivos" || cult == cultivo) && estaDentroDelPeriodo(fecha)) {
                            registrosTemp.add(RegistroReporte("Venta a: ${doc.getString("comprador")}", cult, doc.getDouble("valorTotal") ?: 0.0, fecha))
                        }
                    }
                    listaRegistros = registrosTemp.sortedByDescending { it.fecha }
                    cargando = false
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF2F2F2)).padding(24.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("<", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.clickable { onNavegarAtras() }.padding(end = 16.dp, top = 8.dp, bottom = 8.dp))
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Detalle de Reporte", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text("$cultivo - $periodo", fontSize = 12.sp, color = Color.DarkGray)
            }
            Spacer(modifier = Modifier.width(32.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (cargando) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color(0xFF3C733F)) }
        } else if (listaRegistros.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) { Text("No hay registros para este filtro.", color = Color.Gray) }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(listaRegistros) { registro ->
                    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = registro.titulo, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                                Text(text = "${registro.fecha} | Cultivo: ${registro.subtitulo}", fontSize = 12.sp, color = Color.Gray)
                            }
                            Text(text = "$ ${formatoMoneda.format(registro.valor)}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = if(tipoReporte == "produccion" || tipoReporte == "rentabilidad") Color(0xFF3C733F) else Color.Red)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (listaRegistros.isNotEmpty()) {
                    val timestamp = SimpleDateFormat("ddMMyyyy_HHmm", Locale.getDefault()).format(Calendar.getInstance().time)
                    val nombreArchivo = "Terragest_${tipoReporte.uppercase()}_${timestamp}.pdf"
                    creacionPdfLauncher.launch(nombreArchivo)
                } else {
                    Toast.makeText(context, "No hay datos para exportar", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = if(listaRegistros.isNotEmpty()) Color(0xFF3C733F) else Color.LightGray),
            shape = RoundedCornerShape(25.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = listaRegistros.isNotEmpty()
        ) {
            Text("Exportar comprobante a PDF", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// LÓGICA DE DIBUJO DEL PDF ESTILO "COMPROBANTE/RECIBO"
fun generarYGuardarPDF(
    context: Context,
    uri: Uri,
    tipoReporte: String,
    cultivo: String,
    periodo: String,
    registros: List<RegistroReporte>
) {
    try {
        val pdfDocument = PdfDocument()
        val paint = Paint()

        // Calculamos la altura dinámica para que sea un recibo continuo
        val ancho = 400
        val alturaBase = 250
        val alturaPorRegistro = 45
        val alturaTotal = alturaBase + (registros.size * alturaPorRegistro) + 100

        val pageInfo = PdfDocument.PageInfo.Builder(ancho, alturaTotal, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        // Fondo blanco
        paint.color = android.graphics.Color.WHITE
        canvas.drawRect(0f, 0f, ancho.toFloat(), alturaTotal.toFloat(), paint)

        // Textos y dibujo
        paint.color = android.graphics.Color.BLACK

        // --- ENCABEZADO ---
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 24f
        paint.isFakeBoldText = true
        canvas.drawText("TERRAGEST", ancho / 2f, 50f, paint)

        paint.textSize = 14f
        paint.isFakeBoldText = false
        canvas.drawText("Comprobante de Reporte", ancho / 2f, 75f, paint)

        val fechaGeneracion = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Calendar.getInstance().time)
        paint.textSize = 10f
        paint.color = android.graphics.Color.DKGRAY
        canvas.drawText("Generado el: $fechaGeneracion", ancho / 2f, 90f, paint)

        // Línea separadora
        paint.color = android.graphics.Color.BLACK
        canvas.drawLine(20f, 105f, (ancho - 20).toFloat(), 105f, paint)

        // --- FILTROS APLICADOS ---
        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 12f
        var yPosition = 130f
        canvas.drawText("Tipo de Reporte: ${tipoReporte.uppercase()}", 20f, yPosition, paint); yPosition += 20f
        canvas.drawText("Cultivo: $cultivo", 20f, yPosition, paint); yPosition += 20f
        canvas.drawText("Periodo analizado: $periodo", 20f, yPosition, paint); yPosition += 30f

        // Línea separadora
        canvas.drawLine(20f, yPosition - 10, (ancho - 20).toFloat(), yPosition - 10, paint)

        // --- DETALLE DE REGISTROS ---
        paint.isFakeBoldText = true
        paint.textSize = 14f
        canvas.drawText("Detalle de movimientos:", 20f, yPosition, paint); yPosition += 25f

        paint.isFakeBoldText = false
        paint.textSize = 11f
        val formatoMoneda = NumberFormat.getNumberInstance(Locale("es", "CO"))
        var totalSuma = 0.0

        for (reg in registros) {
            canvas.drawText("${reg.fecha} - ${reg.titulo}", 20f, yPosition, paint)

            val signo = if (tipoReporte == "produccion" || tipoReporte == "rentabilidad") "+" else "-"
            val textoValor = "$signo$ ${formatoMoneda.format(reg.valor)}"

            paint.textAlign = Paint.Align.RIGHT
            paint.isFakeBoldText = true
            canvas.drawText(textoValor, (ancho - 20).toFloat(), yPosition, paint)

            paint.textAlign = Paint.Align.LEFT
            paint.isFakeBoldText = false
            paint.color = android.graphics.Color.DKGRAY
            yPosition += 15f
            canvas.drawText("Cultivo: ${reg.subtitulo}", 20f, yPosition, paint)
            paint.color = android.graphics.Color.BLACK
            yPosition += 30f

            totalSuma += reg.valor
        }

        // --- PIE DE PÁGINA (TOTALES) ---
        canvas.drawLine(20f, yPosition, (ancho - 20).toFloat(), yPosition, paint)
        yPosition += 25f

        paint.textSize = 16f
        paint.isFakeBoldText = true
        canvas.drawText("TOTAL:", 20f, yPosition, paint)

        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("$ ${formatoMoneda.format(totalSuma)}", (ancho - 20).toFloat(), yPosition, paint)

        pdfDocument.finishPage(page)

        // Escribir el documento en el URI provisto por el usuario
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            pdfDocument.writeTo(outputStream)
        }
        pdfDocument.close()
        Toast.makeText(context, "PDF guardado correctamente", Toast.LENGTH_LONG).show()

    } catch (e: Exception) {
        Toast.makeText(context, "Error al generar el PDF: ${e.message}", Toast.LENGTH_LONG).show()
    }
}