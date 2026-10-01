package com.juanga.terragest

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Locale

data class Venta(val id: String = "", val cultivo: String = "", val comprador: String = "", val cantidad: String = "", val valorTotal: Double = 0.0, val fecha: String = "", val rutaFoto: String = "")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisVentasScreen(onNavegarInicio: () -> Unit = {}, onNavegarPerfil: () -> Unit = {}, onNavegarRegistrarVenta: () -> Unit = {}) {
    var listaVentas by remember { mutableStateOf(listOf<Venta>()) }
    var cargando by remember { mutableStateOf(true) }
    var ventaAEliminar by remember { mutableStateOf<Venta?>(null) }
    var ventaAEditar by remember { mutableStateOf<Venta?>(null) }

    val db = FirebaseFirestore.getInstance()
    val idAgricultor = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val context = LocalContext.current
    var rutaFotoEdicion by remember { mutableStateOf("") }

    val selectorEdicion = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val ruta = guardarImagenLocalmente(context, uri)
            if (ruta != null) rutaFotoEdicion = ruta
        }
    }

    LaunchedEffect(Unit) {
        if (idAgricultor.isNotEmpty()) {
            db.collection("ventas").whereEqualTo("idAgricultor", idAgricultor).addSnapshotListener { snapshot, _ ->
                listaVentas = snapshot?.documents?.mapNotNull { it.toObject(Venta::class.java) } ?: emptyList()
                cargando = false
            }
        } else { cargando = false }
    }

    if (ventaAEliminar != null) {
        AlertDialog(
            onDismissRequest = { ventaAEliminar = null },
            title = { Text("Eliminar Venta", fontWeight = FontWeight.Bold, color = Color.Black) },
            text = { Text("¿Estás seguro de eliminar esta venta?", color = Color.DarkGray) },
            confirmButton = { TextButton(onClick = { db.collection("ventas").document(ventaAEliminar!!.id).delete(); ventaAEliminar = null }) { Text("Eliminar", color = Color.Red, fontWeight = FontWeight.Bold) } },
            dismissButton = { TextButton(onClick = { ventaAEliminar = null }) { Text("Cancelar", color = Color.Black) } },
            containerColor = Color.White
        )
    }

    if (ventaAEditar != null) {
        var editCultivo by remember { mutableStateOf(ventaAEditar!!.cultivo) }
        var editComprador by remember { mutableStateOf(ventaAEditar!!.comprador) }
        var editCantidad by remember { mutableStateOf(ventaAEditar!!.cantidad) }
        var editValor by remember { mutableStateOf(ventaAEditar!!.valorTotal.toInt().toString()) }
        var editFecha by remember { mutableStateOf(ventaAEditar!!.fecha.replace("/", "")) }

        AlertDialog(
            onDismissRequest = { ventaAEditar = null },
            title = { Text("Actualizar Venta", fontWeight = FontWeight.Bold, color = Color.Black) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Box(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), contentAlignment = Alignment.Center) {
                        if (rutaFotoEdicion.isNotEmpty()) {
                            val bitmap = BitmapFactory.decodeFile(rutaFotoEdicion)
                            if (bitmap != null) {
                                Image(bitmap = bitmap.asImageBitmap(), contentDescription = "Foto", contentScale = ContentScale.Crop, modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)).clickable { selectorEdicion.launch("image/*") })
                            }
                        } else {
                            Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFF2F2F2)).clickable { selectorEdicion.launch("image/*") }, contentAlignment = Alignment.Center) {
                                Image(painter = painterResource(id = R.drawable.gen_subirfoto), contentDescription = "Subir foto", modifier = Modifier.size(30.dp))
                            }
                        }
                    }
                    OutlinedTextField(value = editCultivo, onValueChange = { editCultivo = it }, label = { Text("Cultivo", color = Color.Gray) }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black), colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editComprador, onValueChange = { editComprador = it }, label = { Text("Comprador", color = Color.Gray) }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black), colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editCantidad, onValueChange = { editCantidad = it }, label = { Text("Cantidad", color = Color.Gray) }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black), colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editValor, onValueChange = { if (it.all { char -> char.isDigit() }) editValor = it }, label = { Text("Valor Total ($)", color = Color.Gray) }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black), colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editFecha, onValueChange = { input -> val num = input.filter { it.isDigit() }; if (num.length <= 8) editFecha = num }, label = { Text("Fecha (DD/MM/AAAA)", color = Color.Gray) }, modifier = Modifier.fillMaxWidth(), visualTransformation = DateTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black), colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White))
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val fechaGuardar = if (editFecha.length == 8) "${editFecha.substring(0, 2)}/${editFecha.substring(2, 4)}/${editFecha.substring(4)}" else editFecha
                    val actualizaciones = mapOf("cultivo" to editCultivo, "comprador" to editComprador, "cantidad" to editCantidad, "valorTotal" to (editValor.toDoubleOrNull() ?: 0.0), "fecha" to fechaGuardar, "rutaFoto" to rutaFotoEdicion)
                    db.collection("ventas").document(ventaAEditar!!.id).update(actualizaciones)
                    ventaAEditar = null
                }) { Text("Actualizar Info", color = Color(0xFF3C733F), fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { ventaAEditar = null }) { Text("Cancelar", color = Color.Black) } },
            containerColor = Color.White
        )
    }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                NavigationBarItem(icon = { Image(painterResource(id = R.drawable.paninicio_logohome), contentDescription = "Inicio", modifier = Modifier.size(24.dp)) }, label = { Text("Inicio", fontSize = 10.sp) }, selected = false, onClick = onNavegarInicio)
                NavigationBarItem(icon = { Image(painterResource(id = R.drawable.paninicio_logodineropequeno), contentDescription = "Ventas", modifier = Modifier.size(24.dp)) }, label = { Text("Ventas", fontSize = 10.sp) }, selected = true, onClick = {}, colors = NavigationBarItemDefaults.colors(indicatorColor = Color(0xFFE8F5E9)))
                NavigationBarItem(icon = { Image(painterResource(id = R.drawable.panperf_perfillogopequeno), contentDescription = "Perfil", modifier = Modifier.size(24.dp)) }, label = { Text("Perfil", fontSize = 10.sp) }, selected = false, onClick = onNavegarPerfil)
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF2F2F2)).padding(paddingValues).padding(24.dp)) {
            Text("Historial de Ventas", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(24.dp))

            if (cargando) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color(0xFF3C733F)) }
            } else if (listaVentas.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) { Text("No hay ventas registradas aún.", color = Color.Gray) }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(listaVentas) { venta ->
                        val formatoMoneda = NumberFormat.getNumberInstance(Locale("es", "CO")).format(venta.valorTotal)
                        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(50.dp).background(Color(0xFFF2F2F2), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                                    if (venta.rutaFoto.isNotEmpty()) {
                                        val bitmap = BitmapFactory.decodeFile(venta.rutaFoto)
                                        if (bitmap != null) {
                                            Image(bitmap = bitmap.asImageBitmap(), contentDescription = "Foto", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)))
                                        } else {
                                            Image(painter = painterResource(id = R.drawable.gen_subirfoto), contentDescription = "Foto", modifier = Modifier.size(24.dp))
                                        }
                                    } else {
                                        Image(painter = painterResource(id = R.drawable.gen_subirfoto), contentDescription = "Foto", modifier = Modifier.size(24.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(16.dp))

                                // CORRECCIÓN DEL TEXTO PARA QUE NO APLASTE
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Cultivo: ${venta.cultivo}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color.Black,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(text = "${venta.fecha} | ${venta.comprador}", fontSize = 12.sp, color = Color.Gray)
                                    Text(text = "Cantidad: ${venta.cantidad}", fontSize = 12.sp, color = Color.DarkGray)
                                }

                                // CORRECCIÓN DE LOS BOTONES RECORTADOS
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = "+$ $formatoMoneda", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF3C733F))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row {
                                        IconButton(onClick = { ventaAEditar = venta; rutaFotoEdicion = venta.rutaFoto }) {
                                            Image(painter = painterResource(id = R.drawable.gen_editar), contentDescription = "Editar", modifier = Modifier.size(24.dp))
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        IconButton(onClick = { ventaAEliminar = venta }) {
                                            Image(painter = painterResource(id = R.drawable.gen_eliminar), contentDescription = "Eliminar", modifier = Modifier.size(24.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            BotonPrincipalVerde(textoDelBoton = "+ Registrar Venta", alHacerClic = onNavegarRegistrarVenta)
        }
    }
}