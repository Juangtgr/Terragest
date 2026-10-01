package com.juanga.terragest

import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarGastosScreen(onNavegarAtras: () -> Unit = {}, onGuardarGasto: () -> Unit = {}) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val idAgricultor = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    var fecha by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var valor by remember { mutableStateOf("") }
    var rutaFoto by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }

    var cultivosList by remember { mutableStateOf(listOf<String>()) }
    var cultivo by remember { mutableStateOf("") }
    var expandedCultivo by remember { mutableStateOf(false) }

    val categoriasList = listOf("Insumos", "Mano de obra", "Transporte", "Riego", "Empaque", "Otros")
    var categoria by remember { mutableStateOf("") }
    var expandedCategoria by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (idAgricultor.isNotEmpty()) {
            db.collection("cultivos").whereEqualTo("idAgricultor", idAgricultor).get().addOnSuccessListener { result ->
                cultivosList = result.documents.mapNotNull { it.getString("nombre") }
            }
        }
    }

    val selectorDeImagen = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val rutaGuardada = guardarImagenLocalmente(context, uri)
            if (rutaGuardada != null) rutaFoto = rutaGuardada else Toast.makeText(context, "Error al guardar la imagen", Toast.LENGTH_SHORT).show()
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF2F2F2)).imePadding().verticalScroll(rememberScrollState()).padding(24.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("<", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.clickable { onNavegarAtras() }.padding(end = 16.dp, top = 8.dp, bottom = 8.dp))
            Text("Registrar gastos", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.width(32.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            if (rutaFoto.isNotEmpty()) {
                val bitmap = BitmapFactory.decodeFile(rutaFoto)
                if (bitmap != null) {
                    Image(bitmap = bitmap.asImageBitmap(), contentDescription = "Foto", contentScale = ContentScale.Crop, modifier = Modifier.size(100.dp).clip(RoundedCornerShape(12.dp)).clickable { selectorDeImagen.launch("image/*") })
                }
            } else {
                Box(modifier = Modifier.size(100.dp).clip(RoundedCornerShape(12.dp)).background(Color.White).clickable { selectorDeImagen.launch("image/*") }, contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(painter = painterResource(id = R.drawable.gen_subirfoto), contentDescription = "Subir foto", modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Añadir foto", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Cultivo", fontWeight = FontWeight.Bold, color = Color.Black)
        ExposedDropdownMenuBox(expanded = expandedCultivo, onExpandedChange = { expandedCultivo = !expandedCultivo }) {
            OutlinedTextField(
                value = cultivo, onValueChange = {}, readOnly = true, modifier = Modifier.fillMaxWidth().menuAnchor(),
                shape = RoundedCornerShape(12.dp), placeholder = { Text("Selecciona un cultivo", color = Color.Gray) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCultivo) }, textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
            )
            ExposedDropdownMenu(expanded = expandedCultivo, onDismissRequest = { expandedCultivo = false }, modifier = Modifier.background(Color.White)) {
                cultivosList.forEach { seleccion -> DropdownMenuItem(text = { Text(seleccion, color = Color.Black) }, onClick = { cultivo = seleccion; expandedCultivo = false }) }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text("Fecha", fontWeight = FontWeight.Bold, color = Color.Black)
        OutlinedTextField(
            value = fecha, onValueChange = { input -> val num = input.filter { it.isDigit() }; if (num.length <= 8) fecha = num },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), placeholder = { Text("DD/MM/AAAA", color = Color.Gray) },
            trailingIcon = { Icon(painterResource(id = R.drawable.gen_logocalendariopequeno), contentDescription = null, modifier = Modifier.size(24.dp), tint = Color.Unspecified) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), visualTransformation = DateTransformation(), textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text("Categoría", fontWeight = FontWeight.Bold, color = Color.Black)
        ExposedDropdownMenuBox(expanded = expandedCategoria, onExpandedChange = { expandedCategoria = !expandedCategoria }) {
            OutlinedTextField(
                value = categoria, onValueChange = {}, readOnly = true, modifier = Modifier.fillMaxWidth().menuAnchor(),
                shape = RoundedCornerShape(12.dp), placeholder = { Text("Selecciona una categoría", color = Color.Gray) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoria) }, textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
            )
            ExposedDropdownMenu(expanded = expandedCategoria, onDismissRequest = { expandedCategoria = false }, modifier = Modifier.background(Color.White)) {
                categoriasList.forEach { seleccion -> DropdownMenuItem(text = { Text(seleccion, color = Color.Black) }, onClick = { categoria = seleccion; expandedCategoria = false }) }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text("Descripción", fontWeight = FontWeight.Bold, color = Color.Black)
        OutlinedTextField(
            value = descripcion, onValueChange = { descripcion = it }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
            placeholder = { Text("Ej: Fertilización triple 15", color = Color.Gray) }, textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text("Valor", fontWeight = FontWeight.Bold, color = Color.Black)
        OutlinedTextField(
            value = valor, onValueChange = { if (it.all { char -> char.isDigit() }) valor = it }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
            placeholder = { Text("$ 0", color = Color.Gray) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
        )
        Spacer(modifier = Modifier.height(32.dp))

        BotonPrincipalVerde(
            textoDelBoton = if (cargando) "Guardando..." else "Guardar gasto",
            alHacerClic = {
                if (cultivo.isEmpty() || fecha.length != 8 || categoria.isEmpty() || descripcion.isEmpty() || valor.isEmpty()) {
                    Toast.makeText(context, "Llena todos los campos correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    cargando = true
                    val idGasto = UUID.randomUUID().toString()
                    val fechaFormateada = "${fecha.substring(0, 2)}/${fecha.substring(2, 4)}/${fecha.substring(4)}"
                    val nuevoGasto = hashMapOf<String, Any>("idGasto" to idGasto, "idAgricultor" to idAgricultor, "cultivo" to cultivo, "fecha" to fechaFormateada, "categoria" to categoria, "descripcion" to descripcion, "valor" to (valor.toDoubleOrNull() ?: 0.0), "rutaFoto" to rutaFoto)
                    db.collection("gastos").document(idGasto).set(nuevoGasto)
                        .addOnSuccessListener { cargando = false; onGuardarGasto() }
                        .addOnFailureListener { cargando = false; Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show() }
                }
            }
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}