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

data class Cultivo(val id: String = "", val idAgricultor: String = "", val nombre: String = "", val variedad: String = "", val fechaSiembra: String = "", val areaSembrada: String = "", val estado: String = "Activo", val rutaFoto: String = "", val tieneFoto: Boolean = false)

@Composable
fun MisCultivosScreen(
    onNavegarAtras: () -> Unit = {},
    onNavegarNuevoCultivo: () -> Unit = {},
    onEditarCultivo: (String) -> Unit = {}
) {
    var busqueda by remember { mutableStateOf("") }
    var listaCultivos by remember { mutableStateOf(listOf<Cultivo>()) }
    var cargando by remember { mutableStateOf(true) }

    var mostrarDialogoEliminar by remember { mutableStateOf(false) }
    var cultivoAEliminar by remember { mutableStateOf<String?>(null) }
    var cultivoAEditar by remember { mutableStateOf<Cultivo?>(null) }

    val context = LocalContext.current
    var rutaFotoEdicion by remember { mutableStateOf("") }

    val selectorEdicion = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val ruta = guardarImagenLocalmente(context, uri)
            if (ruta != null) rutaFotoEdicion = ruta
        }
    }

    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        val idAgricultor = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        if (idAgricultor.isNotEmpty()) {
            db.collection("cultivos").whereEqualTo("idAgricultor", idAgricultor).addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) {
                    cargando = false
                    return@addSnapshotListener
                }
                listaCultivos = snapshot.documents.mapNotNull { it.toObject(Cultivo::class.java) }
                cargando = false
            }
        } else { cargando = false }
    }

    if (mostrarDialogoEliminar && cultivoAEliminar != null) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = false },
            title = { Text("Eliminar cultivo", fontWeight = FontWeight.Bold, color = Color.Black) },
            text = { Text("¿Estás seguro de que deseas eliminar este cultivo? Esta acción no se puede deshacer.", color = Color.DarkGray) },
            confirmButton = {
                TextButton(onClick = {
                    FirebaseFirestore.getInstance().collection("cultivos").document(cultivoAEliminar!!).delete()
                    mostrarDialogoEliminar = false
                    cultivoAEliminar = null
                }) { Text("Eliminar", color = Color.Red, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEliminar = false }) { Text("Cancelar", color = Color.Black) }
            },
            containerColor = Color.White
        )
    }

    if (cultivoAEditar != null) {
        var editNombre by remember { mutableStateOf(cultivoAEditar!!.nombre) }
        var editVariedad by remember { mutableStateOf(cultivoAEditar!!.variedad) }
        var editFecha by remember { mutableStateOf(cultivoAEditar!!.fechaSiembra.replace("/", "")) }
        var editArea by remember { mutableStateOf(cultivoAEditar!!.areaSembrada) }

        AlertDialog(
            onDismissRequest = { cultivoAEditar = null },
            title = { Text("Actualizar Cultivo", fontWeight = FontWeight.Bold, color = Color.Black) },
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
                    OutlinedTextField(
                        value = editNombre, onValueChange = { editNombre = it }, label = { Text("Nombre", color = Color.Gray) }, modifier = Modifier.fillMaxWidth(),
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black), colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editVariedad, onValueChange = { editVariedad = it }, label = { Text("Variedad", color = Color.Gray) }, modifier = Modifier.fillMaxWidth(),
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black), colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editFecha,
                        onValueChange = { input -> val num = input.filter { it.isDigit() }; if (num.length <= 8) editFecha = num },
                        label = { Text("Fecha Siembra (DD/MM/AAAA)", color = Color.Gray) }, modifier = Modifier.fillMaxWidth(),
                        visualTransformation = DateTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black), colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editArea, onValueChange = { editArea = it }, label = { Text("Área sembrada", color = Color.Gray) }, modifier = Modifier.fillMaxWidth(),
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black), colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val fechaGuardar = if (editFecha.length == 8) "${editFecha.substring(0, 2)}/${editFecha.substring(2, 4)}/${editFecha.substring(4)}" else editFecha
                    val actualizaciones = mapOf("nombre" to editNombre, "variedad" to editVariedad, "fechaSiembra" to fechaGuardar, "areaSembrada" to editArea, "rutaFoto" to rutaFotoEdicion, "tieneFoto" to rutaFotoEdicion.isNotEmpty())
                    FirebaseFirestore.getInstance().collection("cultivos").document(cultivoAEditar!!.id).update(actualizaciones)
                    cultivoAEditar = null
                }) { Text("Actualizar Info", color = Color(0xFF3C733F), fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { cultivoAEditar = null }) { Text("Cancelar", color = Color.Black) } },
            containerColor = Color.White
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF2F2F2)).padding(24.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("<", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.clickable { onNavegarAtras() }.padding(end = 16.dp, top = 8.dp, bottom = 8.dp))
            Text("Mis cultivos", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.width(32.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = busqueda, onValueChange = { busqueda = it }, placeholder = { Text("Buscar cultivo", color = Color.Gray) },
            leadingIcon = { Icon(painterResource(id = R.drawable.paninicio_logolupa), contentDescription = "Buscar", modifier = Modifier.size(20.dp)) },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(25.dp),
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent)
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
                    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(70.dp).background(Color(0xFFF2F2F2), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                                if (cultivo.rutaFoto.isNotEmpty()) {
                                    val bitmap = BitmapFactory.decodeFile(cultivo.rutaFoto)
                                    if (bitmap != null) {
                                        Image(bitmap = bitmap.asImageBitmap(), contentDescription = "Foto", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)))
                                    } else {
                                        Image(painter = painterResource(id = R.drawable.gen_subirfoto), contentDescription = "Foto", modifier = Modifier.size(30.dp))
                                    }
                                } else {
                                    Image(painter = painterResource(id = R.drawable.gen_subirfoto), contentDescription = "Foto", modifier = Modifier.size(30.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))

                            // AQUI ESTÁ LA CORRECCIÓN DEL TEXTO APLASTADO
                            Column(modifier = Modifier.weight(1f)) {
                                Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = cultivo.nombre,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color.Black,
                                        modifier = Modifier.weight(1f).padding(end = 8.dp),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Box(modifier = Modifier.background(Color(0xFFE8F5E9), RoundedCornerShape(12.dp)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                                        Text(text = cultivo.estado, color = Color(0xFF3C733F), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Text(text = "Variedad: ${cultivo.variedad}", fontSize = 12.sp, color = Color.DarkGray)
                                Text(text = "Siembra: ${cultivo.fechaSiembra}", fontSize = 12.sp, color = Color.DarkGray)
                            }

                            // AQUI ESTÁ LA CORRECCIÓN DE LOS BOTONES RECORTADOS
                            Column(horizontalAlignment = Alignment.End) {
                                IconButton(onClick = { cultivoAEditar = cultivo; rutaFotoEdicion = cultivo.rutaFoto }) {
                                    Image(painter = painterResource(id = R.drawable.gen_editar), contentDescription = "Editar", modifier = Modifier.size(24.dp))
                                }
                                IconButton(onClick = { cultivoAEliminar = cultivo.id; mostrarDialogoEliminar = true }) {
                                    Image(painter = painterResource(id = R.drawable.gen_eliminar), contentDescription = "Eliminar", modifier = Modifier.size(24.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        BotonPrincipalVerde(textoDelBoton = "+ Nuevo cultivo", alHacerClic = onNavegarNuevoCultivo)
    }
}