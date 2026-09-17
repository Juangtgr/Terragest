package com.juanga.terragest

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ReportesScreen(
    onNavegarAtras: () -> Unit = {},
    onNavegarDetalle: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF2F2F2)).padding(24.dp).verticalScroll(rememberScrollState())
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("<", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.clickable { onNavegarAtras() }.padding(end = 16.dp, top = 8.dp, bottom = 8.dp))
            Text("Reportes", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.width(32.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Seleccione el tipo de reporte", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(16.dp))

        // Botones de Reportes
        ItemTipoReporte("Reporte de gastos", "Resumen de gastos por\ncultivo y por periodo", R.drawable.panrepor_gastologo) { onNavegarDetalle("gastos") }
        ItemTipoReporte("Reporte de producción", "Producción obtenida por\ncultivo y periodo", R.drawable.panrepor_prodlogo) { onNavegarDetalle("produccion") }
        ItemTipoReporte("Reporte de rentabilidad", "Análisis de rentabilidad\npor cultivo", R.drawable.panrepor_rentlogo) { onNavegarDetalle("rentabilidad") }
        ItemTipoReporte("Reporte de insumos", "Insumos utilizados y su\ncostos por cultivo", R.drawable.panrepor_logoinsum) { onNavegarDetalle("insumos") }
    }
}

@Composable
fun ItemTipoReporte(titulo: String, subtitulo: String, icono: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp).clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(painter = painterResource(id = icono), contentDescription = null, modifier = Modifier.size(50.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = titulo, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                Text(text = subtitulo, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}