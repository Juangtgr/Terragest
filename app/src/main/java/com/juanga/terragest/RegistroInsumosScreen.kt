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
fun RegistroInsumosScreen(onNavegarAtras: () -> Unit = {}, onGuardarInsumo: () -> Unit = {}) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val idAgricultor = auth.currentUser?.uid ?: ""

    var cultivosList by remember { mutableStateOf(listOf<String>()) }
    var cultivo by remember { mutableStateOf("") }
    var expandedCultivo by remember { mutableStateOf(false) }

    var insumo by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var unidad by remember { mutableStateOf("") }
    var expandedUnidad by remember { mutableStateOf(false) }
    val unidadesList = listOf("Kg", "Litros", "Bultos", "Unidades")

    var proveedor by remember { mutableStateOf("") }
    var valorUnitario by remember { mutableStateOf("") }
    var fechaCompra by remember { mutableStateOf("") }
    var rutaFoto by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (idAgricultor.isNotEmpty()) {
            db.collection("cultivos").whereEqualTo("idAgricultor", idAgricultor).get().addOnSuccessListener { result ->
                cultivosList = result.documents.mapNotNull { it.getString("nombre") }
            }
        }
    }

    val selectorDeImagen = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val rutaGuardada = guardarImagenLocalmente(context, uri)
            if (rutaGuardada != null) {
                rutaFoto = rutaGuardada
            } else {
                Toast.makeText(context, "Error al guardar la imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF2F2F2)).imePadding().verticalScroll(rememberScrollState()).padding(24.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("<", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.clickable { onNavegarAtras() }.padding(end = 16.dp, top = 8.dp, bottom = 8.dp))
            Text("Registro de insumos", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.width(32.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            if (rutaFoto.isNotEmpty()) {
                val bitmap = BitmapFactory.decodeFile(rutaFoto)
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(), contentDescription = "Foto", contentScale = ContentScale.Crop,
                        modifier = Modifier.size(100.dp).clip(RoundedCornerShape(12.dp)).clickable { selectorDeImagen.launch("image/*") }
                    )
                }
            } else {
                Box(
                    modifier = Modifier.size(100.dp).clip(RoundedCornerShape(12.dp)).background(Color.White).clickable { selectorDeImagen.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
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
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCultivo) },
                textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedBorderColor = Color.LightGray, unfocusedBorderColor = Color.LightGray)
            )
            ExposedDropdownMenu(expanded = expandedCultivo, onDismissRequest = { expandedCultivo = false }, modifier = Modifier.background(Color.White)) {
                cultivosList.forEach { seleccion ->
                    DropdownMenuItem(text = { Text(seleccion, color = Color.Black) }, onClick = { cultivo = seleccion; expandedCultivo = false })
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text("Insumo", fontWeight = FontWeight.Bold, color = Color.Black)
        OutlinedTextField(
            value = insumo, onValueChange = { insumo = it }, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp), placeholder = { Text("Ej: Fertilizante triple 15", color = Color.Gray) },
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedBorderColor = Color.LightGray, unfocusedBorderColor = Color.LightGray)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Cantidad", fontWeight = FontWeight.Bold, color = Color.Black)
                OutlinedTextField(
                    value = cantidad, onValueChange = { if (it.all { char -> char.isDigit() }) cantidad = it }, modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp), placeholder = { Text("50", color = Color.Gray) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedBorderColor = Color.LightGray, unfocusedBorderColor = Color.LightGray)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Unidad", fontWeight = FontWeight.Bold, color = Color.Black)
                ExposedDropdownMenuBox(expanded = expandedUnidad, onExpandedChange = { expandedUnidad = !expandedUnidad }) {
                    OutlinedTextField(
                        value = unidad, onValueChange = {}, readOnly = true, modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(12.dp), placeholder = { Text("Kg", color = Color.Gray) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedUnidad) },
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedBorderColor = Color.LightGray, unfocusedBorderColor = Color.LightGray)
                    )
                    ExposedDropdownMenu(expanded = expandedUnidad, onDismissRequest = { expandedUnidad = false }, modifier = Modifier.background(Color.White)) {
                        unidadesList.forEach { sel ->
                            DropdownMenuItem(text = { Text(sel, color = Color.Black) }, onClick = { unidad = sel; expandedUnidad = false })
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text("Proveedor", fontWeight = FontWeight.Bold, color = Color.Black)
        OutlinedTextField(
            value = proveedor, onValueChange = { proveedor = it }, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp), placeholder = { Text("Agroinsumos del Sur", color = Color.Gray) },
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedBorderColor = Color.LightGray, unfocusedBorderColor = Color.LightGray)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text("Valor unitario", fontWeight = FontWeight.Bold, color = Color.Black)
        OutlinedTextField(
            value = valorUnitario, onValueChange = { if (it.all { char -> char.isDigit() }) valorUnitario = it }, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp), placeholder = { Text("$ 2.400", color = Color.Gray) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedBorderColor = Color.LightGray, unfocusedBorderColor = Color.LightGray)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text("Fecha de compra", fontWeight = FontWeight.Bold, color = Color.Black)
        OutlinedTextField(
            value = fechaCompra,
            onValueChange = { input ->
                val soloNumeros = input.filter { it.isDigit() }
                if (soloNumeros.length <= 8) {
                    fechaCompra = soloNumeros
                }
            },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), placeholder = { Text("DD/MM/AAAA", color = Color.Gray) },
            trailingIcon = { Icon(painterResource(id = R.drawable.gen_logocalendariopequeno), contentDescription = null, modifier = Modifier.size(24.dp), tint = Color.Unspecified) },
            visualTransformation = DateTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedBorderColor = Color.LightGray, unfocusedBorderColor = Color.LightGray)
        )
        Spacer(modifier = Modifier.height(32.dp))

        BotonPrincipalVerde(
            textoDelBoton = if (cargando) "Guardando..." else "Guardar insumo",
            alHacerClic = {
                if (cultivo.isEmpty() || insumo.isEmpty() || cantidad.isEmpty() || unidad.isEmpty() || valorUnitario.isEmpty()) {
                    Toast.makeText(context, "Llena todos los campos", Toast.LENGTH_SHORT).show()
                } else if (fechaCompra.length != 8) {
                    Toast.makeText(context, "Fecha incompleta", Toast.LENGTH_SHORT).show()
                } else {
                    cargando = true
                    val idIns = UUID.randomUUID().toString()
                    val fechaFormateada = "${fechaCompra.substring(0, 2)}/${fechaCompra.substring(2, 4)}/${fechaCompra.substring(4)}"

                    val nuevoInsumo = hashMapOf(
                        "id" to idIns,
                        "idAgricultor" to idAgricultor,
                        "cultivo" to cultivo,
                        "nombre" to insumo,
                        "cantidad" to "$cantidad $unidad",
                        "proveedor" to proveedor,
                        "precio" to (valorUnitario.toDoubleOrNull() ?: 0.0),
                        "fechaCompra" to fechaFormateada,
                        "estado" to "Disponible",
                        "rutaFoto" to rutaFoto,
                        "tieneFoto" to rutaFoto.isNotEmpty()
                    )

                    db.collection("insumos").document(idIns).set(nuevoInsumo)
                        .addOnSuccessListener {
                            cargando = false
                            onGuardarInsumo()
                        }
                        .addOnFailureListener {
                            cargando = false
                            Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}