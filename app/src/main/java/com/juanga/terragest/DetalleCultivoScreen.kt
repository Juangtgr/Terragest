package com.juanga.terragest

import androidx.compose.foundation.BorderStroke
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

@Composable
fun DetalleCultivoScreen(
    onNavegarAtras: () -> Unit = {}
) {
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF2F2F2)).verticalScroll(rememberScrollState()).padding(24.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("<", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.clickable { onNavegarAtras() }.padding(end = 16.dp, top = 8.dp, bottom = 8.dp))
            Text("Detalle del cultivo", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.width(32.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider(color = Color(0xFF3C733F), thickness = 2.dp)
        Spacer(modifier = Modifier.height(24.dp))

        // Encabezado del cultivo
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(80.dp).background(Color.White, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                Image(painter = painterResource(id = R.drawable.gen_subirfoto), contentDescription = "Foto", modifier = Modifier.size(40.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = "Papa", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
                Text(text = "Variedad: Diacol Capiro", fontSize = 14.sp, color = Color.DarkGray)
                Text(text = "Siembra: 12/03/2026", fontSize = 14.sp, color = Color.DarkGray)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Text(text = "Estado: ", fontSize = 14.sp, color = Color.DarkGray)
                    Box(modifier = Modifier.background(Color(0xFFE8F5E9), RoundedCornerShape(12.dp)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                        Text(text = "Activo", color = Color(0xFF3C733F), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("Información general", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                FilaDetalle("Área sembrada", "1.5 ha")
                FilaDetalle("Fecha estimada de cosecha", "12/07/2026")
                FilaDetalle("Días trascurridos", "45 días")
                FilaDetalle("Ubicación", "Vereda Catambuco")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Resumen financiero", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                FilaDetalle("Gastos totales", "$ 850.000")
                FilaDetalle("Producción estimada", "1.200 kg")
                FilaDetalle("Rentabilidad", "25 %")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // NUEVO: BOTONES DE EDITAR Y ELIMINAR PARA CUMPLIR EL MÓDULO 2
        BotonPrincipalVerde(textoDelBoton = "Editar cultivo", alHacerClic = { /* Lógica de edición futura */ })
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { mostrarDialogoEliminar = true },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
            border = BorderStroke(2.dp, Color.Red)
        ) {
            Text("Eliminar cultivo", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }

    // DIÁLOGO DE CONFIRMACIÓN PARA ELIMINAR
    if (mostrarDialogoEliminar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = false },
            title = { Text("Eliminar cultivo", fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro de que deseas eliminar este cultivo? Todos los gastos asociados se perderán.") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarDialogoEliminar = false
                    onNavegarAtras() // Simula la eliminación volviendo a la lista
                }) {
                    Text("Eliminar", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEliminar = false }) {
                    Text("Cancelar", color = Color.Black)
                }
            },
            containerColor = Color.White
        )
    }
}

@Composable
fun FilaDetalle(titulo: String, valor: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = titulo, fontSize = 14.sp, color = Color.DarkGray)
        Text(text = valor, fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Medium)
    }
}