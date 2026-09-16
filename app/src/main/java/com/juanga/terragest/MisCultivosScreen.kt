package com.juanga.terragest

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Modelo de la base de datos
data class Cultivo(val id: String, val nombre: String, val variedad: String, val fechaSiembra: String, val estado: String, val tieneFoto: Boolean = false)

@Composable
fun MisCultivosScreen(
    onNavegarAtras: () -> Unit = {},
    onNavegarNuevoCultivo: () -> Unit = {}
) {
    var busqueda by remember { mutableStateOf("") }
    // Firebase llenará esta lista. Por ahora inicia vacía.
    var listaCultivos by remember { mutableStateOf(listOf<Cultivo>()) }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF2F2F2)).padding(24.dp)
    ) {
        // Encabezado
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("<", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.clickable { onNavegarAtras() }.padding(end = 16.dp, top = 8.dp, bottom = 8.dp))
            Text("Mis cultivos", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.width(32.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Buscador
        OutlinedTextField(
            value = busqueda, onValueChange = { busqueda = it },
            placeholder = { Text("Buscar cultivo", color = Color.Gray) },
            leadingIcon = { Icon(painterResource(id = R.drawable.paninicio_logolupa), contentDescription = "Buscar", modifier = Modifier.size(20.dp)) },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(25.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Lista de cultivos
        if (listaCultivos.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No tienes cultivos registrados.\n¡Agrega uno nuevo!", color = Color.Gray, textAlign = TextAlign.Center)
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(listaCultivos) { cultivo ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp).clickable { /* Ir a detalles */ },
                        shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            // MARCO DE FOTO / SUBIR FOTO
                            Box(modifier = Modifier.size(70.dp).background(Color(0xFFF2F2F2), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                                if (cultivo.tieneFoto) {
                                    // Aquí iría la carga de imagen web (Coil/Glide)
                                } else {
                                    Image(painter = painterResource(id = R.drawable.gen_subirfoto), contentDescription = "Subir foto", modifier = Modifier.size(30.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text(text = cultivo.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                                    Box(modifier = Modifier.background(Color(0xFFE8F5E9), RoundedCornerShape(12.dp)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                                        Text(text = cultivo.estado, color = Color(0xFF3C733F), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Text(text = "Variedad: ${cultivo.variedad}", fontSize = 12.sp, color = Color.DarkGray)
                                Text(text = "Siembra: ${cultivo.fechaSiembra}", fontSize = 12.sp, color = Color.DarkGray)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        BotonPrincipalVerde(textoDelBoton = "Nuevo cultivo", alHacerClic = onNavegarNuevoCultivo)
    }
}