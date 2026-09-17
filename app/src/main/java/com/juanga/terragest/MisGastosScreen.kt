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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Modelo temporal para Firebase
data class Gasto(val id: String, val descripcion: String, val fecha: String, val valor: String, val icono: Int)

@Composable
fun MisGastosScreen(
    onNavegarAtras: () -> Unit = {},
    onNavegarRegistrarGasto: () -> Unit = {}
) {
    var busqueda by remember { mutableStateOf("") }

    // Lista simulada preparada para Firebase (inicia vacía, pero puedes agregar datos de prueba si deseas)
    var listaGastos by remember { mutableStateOf(listOf<Gasto>()) }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF2F2F2)).padding(24.dp)
    ) {
        // Encabezado
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("<", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.clickable { onNavegarAtras() }.padding(end = 16.dp, top = 8.dp, bottom = 8.dp))
            Text("Mis Gastos", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.width(32.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Buscador
        OutlinedTextField(
            value = busqueda, onValueChange = { busqueda = it },
            placeholder = { Text("Buscar gastos", color = Color.Gray) },
            leadingIcon = { Icon(painterResource(id = R.drawable.paninicio_logolupa), contentDescription = "Buscar", modifier = Modifier.size(20.dp), tint = Color.Unspecified) },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(25.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black, unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White,
                focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Filtro Desplegable (Simulado)
        OutlinedTextField(
            value = "Todos los cultivos", onValueChange = {}, readOnly = true,
            trailingIcon = { Icon(painterResource(id = R.drawable.gen_flechaabajo), contentDescription = "Desplegar", modifier = Modifier.size(16.dp), tint = Color.Unspecified) },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black, unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White,
                focusedBorderColor = Color.LightGray, unfocusedBorderColor = Color.LightGray
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Lista de Gastos
        if (listaGastos.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No hay gastos registrados.", color = Color.Gray, textAlign = TextAlign.Center)
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(listaGastos) { gasto ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Image(painter = painterResource(id = gasto.icono), contentDescription = null, modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = gasto.descripcion, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                                Text(text = gasto.fecha, fontSize = 12.sp, color = Color.Gray)
                            }
                            Text(text = gasto.valor, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        BotonPrincipalVerde(textoDelBoton = "+ Registrar gasto", alHacerClic = onNavegarRegistrarGasto)
    }
}
