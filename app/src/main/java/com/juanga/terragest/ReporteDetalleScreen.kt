package com.juanga.terragest

import androidx.compose.foundation.BorderStroke
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

@Composable
fun ReporteDetalleScreen(
    tipoReporte: String,
    onNavegarAtras: () -> Unit = {}
) {
    var cultivo by remember { mutableStateOf("") }
    var periodo by remember { mutableStateOf("") }

    // Configuración dinámica según el tipo de reporte
    val titulo = when (tipoReporte) {
        "gastos" -> "Reporte de gastos"
        "produccion" -> "Reporte de producción"
        "rentabilidad" -> "Reporte de rentabilidad"
        "insumos" -> "Reporte de insumos"
        else -> "Reporte"
    }

    val resultados = when (tipoReporte) {
        "gastos" -> listOf("Gastos Total" to "$ 0", "Gastos promedio" to "$ 0", "Total de registros" to "0", "Cultivos" to "0")
        "produccion" -> listOf("Producción total" to "0 Kg", "Rendimiento promedio" to "0 Kg", "Total de registros" to "0", "Cultivos" to "0")
        "rentabilidad" -> listOf("Ingresos Totales" to "$ 0", "Costos totales" to "$ 0", "Utilidad neta" to "$ 0", "Rentabilidad" to "0%")
        "insumos" -> listOf("Total de insumos" to "$ 0", "Costos total" to "$ 0", "Total de registros" to "0", "Cultivos" to "0")
        else -> emptyList()
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF2F2F2)).padding(24.dp).verticalScroll(rememberScrollState())
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("<", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.clickable { onNavegarAtras() }.padding(end = 16.dp, top = 8.dp, bottom = 8.dp))
            Text(titulo, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.width(32.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Cultivo", fontWeight = FontWeight.Bold, color = Color.Black)
        OutlinedTextField(
            value = cultivo, onValueChange = {}, readOnly = true, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp), placeholder = { Text("Todos", color = Color.Gray) },
            trailingIcon = { Icon(painterResource(id = R.drawable.gen_flechaabajo), contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Unspecified) },
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Periodo", fontWeight = FontWeight.Bold, color = Color.Black)
        OutlinedTextField(
            value = periodo, onValueChange = {}, readOnly = true, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp), placeholder = { Text("Este mes", color = Color.Gray) },
            trailingIcon = { Icon(painterResource(id = R.drawable.gen_flechaabajo), contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Unspecified) },
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botones Generar y Exportar
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(
                onClick = { /* Lógica de generar */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF87BF4E)),
                shape = RoundedCornerShape(25.dp),
                modifier = Modifier.weight(1f).height(45.dp)
            ) {
                Text("Generar reporte", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedButton(
                onClick = { /* Lógica de exportar */ },
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFF87BF4E)),
                border = BorderStroke(1.dp, Color(0xFF3C733F)),
                modifier = Modifier.weight(1f).height(45.dp)
            ) {
                Text("Exportar", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Resumen de ${tipoReporte}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(16.dp))

        // Resultados generados dinámicamente
        resultados.forEach { (label, valor) ->
            OutlinedTextField(
                value = valor, onValueChange = {}, readOnly = true,
                label = { Text(label, color = Color.Gray) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedBorderColor = Color.LightGray, unfocusedBorderColor = Color.LightGray)
            )
        }
    }
}