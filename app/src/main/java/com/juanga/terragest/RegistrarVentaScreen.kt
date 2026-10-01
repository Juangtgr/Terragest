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
fun RegistrarVentaScreen(onNavegarAtras: () -> Unit = {}, onGuardarVenta: () -> Unit = {}) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val idAgricultor = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    var cultivosList by remember { mutableStateOf(listOf<String>()) }
    var cultivo by remember { mutableStateOf("") }
    var expandedCultivo by remember { mutableStateOf(false) }

    var comprador by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var valorTotal by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
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

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF2F2F2)).imePadding().verticalScroll(rememberScrollState()).padding(24.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("<", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.clickable { onNavegarAtras() }.padding(end = 16.dp, top = 8.dp, bottom = 8.dp))
            Text("Registrar Venta", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
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
                Box(modifier = Modifier.size(100.dp).clip(RoundedCornerShape(12.dp)).background(Color.White).clickable { selectorDeImagen.launch("image/*") }, contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(painter = painterResource(id = R.drawable.gen_subirfoto), contentDescription = "Subir recibo", modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Subir recibo", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Cultivo", fontWeight = FontWeight.Bold, color = Color.Black)
        ExposedDropdownMenuBox(expanded = expandedCultivo, onExpandedChange = { expandedCultivo = !expandedCultivo }) {
            OutlinedTextField(
                value = cultivo, onValueChange = {}, readOnly = true, modifier = Modifier.fillMaxWidth().menuAnchor(),
                shape = RoundedCornerShape(12.dp), placeholder = { Text("Selecciona el cultivo", color = Color.Gray) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCultivo) },
                textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedBorderColor = Color.LightGray, unfocusedBorderColor = Color.LightGray)
            )
            ExposedDropdownMenu(expanded = expandedCultivo, onDismissRequest = { expandedCultivo = false }, modifier = Modifier.background(Color.White)) {
                cultivosList.forEach { sel -> DropdownMenuItem(text = { Text(sel, color = Color.Black) }, onClick = { cultivo = sel; expandedCultivo = false }) }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text("Comprador", fontWeight = FontWeight.Bold, color = Color.Black)
        OutlinedTextField(
            value = comprador, onValueChange = { comprador = it }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), placeholder = { Text("Ej: Mercado Local", color = Color.Gray) },
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedBorderColor = Color.LightGray, unfocusedBorderColor = Color.LightGray)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text("Cantidad vendida", fontWeight = FontWeight.Bold, color = Color.Black)
        OutlinedTextField(
            value = cantidad, onValueChange = { cantidad = it }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), placeholder = { Text("Ej: 15 Bultos", color = Color.Gray) },
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedBorderColor = Color.LightGray, unfocusedBorderColor = Color.LightGray)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text("Valor Total Recibido", fontWeight = FontWeight.Bold, color = Color.Black)
        OutlinedTextField(
            value = valorTotal, onValueChange = { if (it.all { char -> char.isDigit() }) valorTotal = it }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), placeholder = { Text("$ 0", color = Color.Gray) },
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedBorderColor = Color.LightGray, unfocusedBorderColor = Color.LightGray)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text("Fecha de Venta", fontWeight = FontWeight.Bold, color = Color.Black)
        OutlinedTextField(
            value = fecha,
            onValueChange = { input ->
                val soloNumeros = input.filter { it.isDigit() }
                if (soloNumeros.length <= 8) {
                    fecha = soloNumeros
                }
            },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
            placeholder = { Text("DD/MM/AAAA", color = Color.Gray) },
            visualTransformation = DateTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedBorderColor = Color.LightGray, unfocusedBorderColor = Color.LightGray)
        )
        Spacer(modifier = Modifier.height(32.dp))

        BotonPrincipalVerde(
            textoDelBoton = if (cargando) "Guardando..." else "Registrar venta",
            alHacerClic = {
                if (cultivo.isEmpty() || comprador.isEmpty() || cantidad.isEmpty() || valorTotal.isEmpty() || fecha.length != 8) {
                    Toast.makeText(context, "Llena todos los campos correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    cargando = true
                    val idVenta = UUID.randomUUID().toString()
                    val fechaFormateada = "${fecha.substring(0, 2)}/${fecha.substring(2, 4)}/${fecha.substring(4)}"
                    val nuevaVenta = hashMapOf("id" to idVenta, "idAgricultor" to idAgricultor, "cultivo" to cultivo, "comprador" to comprador, "cantidad" to cantidad, "valorTotal" to (valorTotal.toDoubleOrNull() ?: 0.0), "fecha" to fechaFormateada, "rutaFoto" to rutaFoto)

                    db.collection("ventas").document(idVenta).set(nuevaVenta)
                        .addOnSuccessListener { onGuardarVenta() }
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