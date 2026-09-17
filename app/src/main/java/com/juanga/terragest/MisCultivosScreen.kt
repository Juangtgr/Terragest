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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class Cultivo(val id: String, val nombre: String, val variedad: String, val fechaSiembra: String, val estado: String, val tieneFoto: Boolean = false)

@Composable
fun MisCultivosScreen(
    onNavegarAtras: () -> Unit = {},
    onNavegarNuevoCultivo: () -> Unit = {},
    onEditarCultivo: (String) -> Unit = {} // NUEVO: Función para editar
) {
    var busqueda by remember { mutableStateOf("") }
    var listaCultivos by remember { mutableStateOf(listOf<Cultivo>()) }
    var cargando by remember { mutableStateOf(true) }

    // Variables para el cuadro de diálogo de eliminación
    var mostrarDialogo by remember { mutableStateOf(false) }
    var cultivoAEliminar by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val idAgricultor = auth.currentUser?.uid ?: ""

        if (idAgricultor.isNotEmpty()) {
            db.collection("cultivos").whereEqualTo("idAgricultor", idAgricultor).get()
                .addOnSuccessListener { documentos ->
                    val cultivosDescargados = mutableListOf<Cultivo>()
                    for (documento in documentos) {
                        cultivosDescargados.add(
                            Cultivo(
                                id = documento.getString("id") ?: "",
                                nombre = documento.getString("nombre") ?: "Sin nombre",
                                variedad = documento.getString("variedad") ?: "Sin variedad",
                                fechaSiembra = documento.getString("fechaSiembra") ?: "--/--/----",
                                estado = documento.getString("estado") ?: "Activo",
                                tieneFoto = documento.getBoolean("tieneFoto") ?: false
                            )
                        )
                    }
                    listaCultivos = cultivosDescargados
                    cargando = false
                }
        } else {
            cargando = false
        }
    }

    // DIÁLOGO DE CONFIRMACIÓN PARA ELIMINAR
    if (mostrarDialogo && cultivoAEliminar != null) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Eliminar cultivo", fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro de que deseas eliminar este cultivo? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    val db = FirebaseFirestore.getInstance()
                    db.collection("cultivos").document(cultivoAEliminar!!).delete()
                        .addOnSuccessListener {
                            // Actualizamos la lista local para que desaparezca al instante
                            listaCultivos = listaCultivos.filter { it.id != cultivoAEliminar }
                            mostrarDialogo = false
                            cultivoAEliminar = null
                        }
                }) {
                    Text("Eliminar", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) {
                    Text("Cancelar", color = Color.Black)
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF2F2F2)).padding(24.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("<", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.clickable { onNavegarAtras() }.padding(end = 16.dp, top = 8.dp, bottom = 8.dp))
            Text("Mis cultivos", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.width(32.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = busqueda, onValueChange = { busqueda = it }, placeholder = { Text("Buscar cultivo", color = Color.Gray) },
            leadingIcon = { Icon(painterResource(id = R.drawable.paninicio_logolupa), contentDescription = "Buscar", modifier = Modifier.size(20.dp)) },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(25.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent)
        )
        Spacer(modifier = Modifier.height(24.dp))

        if (cargando) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color(0xFF3C733F)) }
        } else if (listaCultivos.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) { Text("No tienes cultivos registrados.\n¡Agrega uno nuevo!", color = Color.Gray, textAlign = TextAlign.Center) }
        } else {
            val listaFiltrada = listaCultivos.filter { it.nombre.contains(busqueda, ignoreCase = true) || it.variedad.contains(busqueda, ignoreCase = true) }

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(listaFiltrada) { cultivo ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(70.dp).background(Color(0xFFF2F2F2), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                                Image(painter = painterResource(id = R.drawable.gen_subirfoto), contentDescription = "Foto", modifier = Modifier.size(30.dp))
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

                            // ICONOS DE EDITAR Y ELIMINAR (Usando tus propios PNG)
                            Column(horizontalAlignment = Alignment.End) {
                                IconButton(
                                    onClick = { onEditarCultivo(cultivo.id) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.gen_editar),
                                        contentDescription = "Editar",
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        cultivoAEliminar = cultivo.id
                                        mostrarDialogo = true
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.gen_eliminar),
                                        contentDescription = "Eliminar",
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
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