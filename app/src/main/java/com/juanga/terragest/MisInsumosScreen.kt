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

data class Insumo(val id: String = "", val idAgricultor: String = "", val nombre: String = "", val cultivo: String = "", val cantidad: String = "", val precio: Double = 0.0, val estado: String = "Disponible", val rutaFoto: String = "", val tieneFoto: Boolean = false)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisInsumosScreen(onNavegarAtras: () -> Unit = {}, onNavegarRegistrarInsumo: () -> Unit = {}) {
    var busqueda by remember { mutableStateOf("") }
    var listaInsumos by remember { mutableStateOf(listOf<Insumo>()) }
    var cargando by remember { mutableStateOf(true) }
    var filtroCultivo by remember { mutableStateOf("Todos los cultivos") }
    var expandedFiltro by remember { mutableStateOf(false) }
    var insumoAEliminar by remember { mutableStateOf<String?>(null) }
    var insumoAEditar by remember { mutableStateOf<Insumo?>(null) }

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
        if(idAgricultor.isNotEmpty()) {
            db.collection("insumos").whereEqualTo("idAgricultor", idAgricultor).addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) { cargando = false; return@addSnapshotListener }
                listaInsumos = snapshot.documents.mapNotNull { it.toObject(Insumo::class.java) }
                cargando = false
            }
        } else { cargando = false }
    }

    val listaFiltrada = listaInsumos.filter { (it.nombre.contains(busqueda, ignoreCase = true) || it.cultivo.contains(busqueda, ignoreCase = true)) && (filtroCultivo == "Todos los cultivos" || it.cultivo == filtroCultivo) }
    val opcionesCultivos = listOf("Todos los cultivos") + listaInsumos.map { it.cultivo }.distinct()

    if (insumoAEliminar != null) {
        AlertDialog(
            onDismissRequest = { insumoAEliminar = null },
            title = { Text("Eliminar Insumo", fontWeight = FontWeight.Bold, color = Color.Black) },
            text = { Text("¿Estás seguro de eliminar este insumo?", color = Color.DarkGray) },
            confirmButton = { TextButton(onClick = { db.collection("insumos").document(insumoAEliminar!!).delete(); insumoAEliminar = null }) { Text("Eliminar", color = Color.Red, fontWeight = FontWeight.Bold) } },
            dismissButton = { TextButton(onClick = { insumoAEliminar = null }) { Text("Cancelar", color = Color.Black) } },
            containerColor = Color.White
        )
    }

    if (insumoAEditar != null) {
        var editNombre by remember { mutableStateOf(insumoAEditar!!.nombre) }
        var editCantidad by remember { mutableStateOf(insumoAEditar!!.cantidad) }
        var editPrecio by remember { mutableStateOf(insumoAEditar!!.precio.toInt().toString()) }

        AlertDialog(
            onDismissRequest = { insumoAEditar = null },
            title = { Text("Actualizar Insumo", fontWeight = FontWeight.Bold, color = Color.Black) },
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
                    OutlinedTextField(value = editNombre, onValueChange = { editNombre = it }, label = { Text("Nombre", color = Color.Gray) }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black), colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editCantidad, onValueChange = { editCantidad = it }, label = { Text("Cantidad y Unidad", color = Color.Gray) }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black), colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editPrecio, onValueChange = { if (it.all { char -> char.isDigit() }) editPrecio = it }, label = { Text("Precio Total ($)", color = Color.Gray) }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black), colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White))
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val actualizaciones = mapOf("nombre" to editNombre, "cantidad" to editCantidad, "precio" to (editPrecio.toDoubleOrNull() ?: 0.0), "rutaFoto" to rutaFotoEdicion)
                    db.collection("insumos").document(insumoAEditar!!.id).update(actualizaciones)
                    insumoAEditar = null
                }) { Text("Actualizar Info", color = Color(0xFF3C733F), fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { insumoAEditar = null }) { Text("Cancelar", color = Color.Black) } },
            containerColor = Color.White
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF2F2F2)).padding(24.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("<", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.clickable { onNavegarAtras() }.padding(end = 16.dp, top = 8.dp, bottom = 8.dp))
            Text("Mis insumos", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.width(32.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = busqueda, onValueChange = { busqueda = it }, placeholder = { Text("Buscar insumos", color = Color.Gray) },
            leadingIcon = { Icon(painterResource(id = R.drawable.paninicio_logolupa), contentDescription = "Buscar", modifier = Modifier.size(20.dp), tint = Color.Unspecified) },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(25.dp), textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent)
        )

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(expanded = expandedFiltro, onExpandedChange = { expandedFiltro = !expandedFiltro }) {
            OutlinedTextField(
                value = filtroCultivo, onValueChange = {}, readOnly = true, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFiltro) },
                modifier = Modifier.fillMaxWidth().menuAnchor(), shape = RoundedCornerShape(12.dp), textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedBorderColor = Color.LightGray, unfocusedBorderColor = Color.LightGray)
            )
            ExposedDropdownMenu(expanded = expandedFiltro, onDismissRequest = { expandedFiltro = false }, modifier = Modifier.background(Color.White)) {
                opcionesCultivos.forEach { seleccion -> DropdownMenuItem(text = { Text(seleccion, color = Color.Black) }, onClick = { filtroCultivo = seleccion; expandedFiltro = false }) }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (cargando) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color(0xFF3C733F)) }
        } else if (listaFiltrada.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) { Text("No hay insumos registrados.", color = Color.Gray, textAlign = TextAlign.Center) }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(listaFiltrada) { insumo ->
                    val formatoMoneda = NumberFormat.getNumberInstance(Locale("es", "CO")).format(insumo.precio)
                    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(60.dp).background(Color(0xFFF2F2F2), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                                if (insumo.rutaFoto.isNotEmpty()) {
                                    val bitmap = BitmapFactory.decodeFile(insumo.rutaFoto)
                                    if (bitmap != null) {
                                        Image(bitmap = bitmap.asImageBitmap(), contentDescription = "Foto", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)))
                                    } else {
                                        Image(painter = painterResource(id = R.drawable.gen_subirfoto), contentDescription = "Subir foto", modifier = Modifier.size(24.dp))
                                    }
                                } else {
                                    Image(painter = painterResource(id = R.drawable.gen_subirfoto), contentDescription = "Subir foto", modifier = Modifier.size(24.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))

                            // AQUI ESTÁ LA CORRECCIÓN DEL TEXTO APLASTADO
                            Column(modifier = Modifier.weight(1f)) {
                                Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = insumo.nombre,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color.Black,
                                        modifier = Modifier.weight(1f).padding(end = 8.dp),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Box(modifier = Modifier.background(Color(0xFFE8F5E9), RoundedCornerShape(12.dp)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                                        Text(text = insumo.estado, color = Color(0xFF3C733F), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Text(text = "Cultivo: ${insumo.cultivo}", fontSize = 12.sp, color = Color.DarkGray)
                                Text(text = "Cantidad: ${insumo.cantidad}", fontSize = 12.sp, color = Color.DarkGray)
                                Text(text = "Precio: $ $formatoMoneda", fontSize = 12.sp, color = Color.DarkGray)
                            }

                            // AQUI ESTÁ LA CORRECCIÓN DE LOS BOTONES RECORTADOS
                            Column(horizontalAlignment = Alignment.End) {
                                IconButton(onClick = { insumoAEditar = insumo; rutaFotoEdicion = insumo.rutaFoto }) {
                                    Image(painter = painterResource(id = R.drawable.gen_editar), contentDescription = "Editar", modifier = Modifier.size(24.dp))
                                }
                                IconButton(onClick = { insumoAEliminar = insumo.id }) {
                                    Image(painter = painterResource(id = R.drawable.gen_eliminar), contentDescription = "Eliminar", modifier = Modifier.size(24.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        BotonPrincipalVerde(textoDelBoton = "+ Registrar insumo", alHacerClic = onNavegarRegistrarInsumo)
    }
}