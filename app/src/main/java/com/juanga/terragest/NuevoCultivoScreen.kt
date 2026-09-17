package com.juanga.terragest

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

@Composable
fun NuevoCultivoScreen(
    cultivoId: String? = null, // NUEVO PARÁMETRO
    onNavegarAtras: () -> Unit = {},
    onGuardarCultivo: () -> Unit = {}
) {
    var nombre by remember { mutableStateOf("") }
    var variedad by remember { mutableStateOf("") }
    var fechaSiembra by remember { mutableStateOf("") }
    var areaSembrada by remember { mutableStateOf("") }

    val context = LocalContext.current
    var cargando by remember { mutableStateOf(false) }
    var descargandoDatos by remember { mutableStateOf(cultivoId != null) }

    // SI HAY UN ID, DESCARGAMOS LOS DATOS PARA EDITAR
    LaunchedEffect(cultivoId) {
        if (cultivoId != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("cultivos").document(cultivoId).get()
                .addOnSuccessListener { doc ->
                    nombre = doc.getString("nombre") ?: ""
                    variedad = doc.getString("variedad") ?: ""
                    areaSembrada = doc.getString("areaSembrada") ?: ""
                    // Le quitamos las barritas a la fecha para que el VisualTransformation funcione bien
                    val fechaFirebase = doc.getString("fechaSiembra") ?: ""
                    fechaSiembra = fechaFirebase.replace("/", "")
                    descargandoDatos = false
                }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF2F2F2)).imePadding().verticalScroll(rememberScrollState()).padding(24.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("<", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.clickable { onNavegarAtras() }.padding(end = 16.dp, top = 8.dp, bottom = 8.dp))
            // EL TÍTULO CAMBIA SI ESTAMOS EDITANDO
            Text(if (cultivoId == null) "Nuevo cultivo" else "Editar cultivo", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.width(32.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (descargandoDatos) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF3C733F))
            }
        } else {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.size(100.dp).clip(RoundedCornerShape(12.dp)).background(Color.White).clickable { }, contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(painter = painterResource(id = R.drawable.gen_subirfoto), contentDescription = "Subir foto", modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Añadir foto", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Nombre del cultivo", fontWeight = FontWeight.Bold, color = Color.Black)
            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), placeholder = { Text("Ej: Papa") }, colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black))
            Spacer(modifier = Modifier.height(16.dp))

            Text("Variedad", fontWeight = FontWeight.Bold, color = Color.Black)
            OutlinedTextField(value = variedad, onValueChange = { variedad = it }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), placeholder = { Text("Ej: Diacol Capiro") }, colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black))
            Spacer(modifier = Modifier.height(16.dp))

            Text("Fecha de siembra", fontWeight = FontWeight.Bold, color = Color.Black)
            OutlinedTextField(
                value = fechaSiembra,
                onValueChange = { if (it.length <= 8 && it.all { char -> char.isDigit() }) { fechaSiembra = it } },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), placeholder = { Text("DD/MM/AAAA") },
                trailingIcon = { Icon(painterResource(id = R.drawable.gen_logocalendariopequeno), contentDescription = null, modifier = Modifier.size(24.dp), tint = Color.Unspecified) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = DateTransformation(),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("Área sembrada", fontWeight = FontWeight.Bold, color = Color.Black)
            OutlinedTextField(value = areaSembrada, onValueChange = { areaSembrada = it }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), placeholder = { Text("Ej: 1.5 hectáreas") }, colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black))
            Spacer(modifier = Modifier.height(32.dp))

            BotonPrincipalVerde(
                textoDelBoton = if (cargando) "Guardando..." else if (cultivoId == null) "Guardar cultivo" else "Actualizar cultivo",
                alHacerClic = {
                    if (nombre.isEmpty() || variedad.isEmpty() || fechaSiembra.isEmpty() || areaSembrada.isEmpty()) {
                        Toast.makeText(context, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show()
                        return@BotonPrincipalVerde
                    }

                    if (fechaSiembra.length != 8) {
                        Toast.makeText(context, "La fecha debe estar completa (DDMMAAAA)", Toast.LENGTH_SHORT).show()
                        return@BotonPrincipalVerde
                    }

                    try {
                        val sdf = java.text.SimpleDateFormat("ddMMyyyy")
                        sdf.isLenient = false
                        val fechaIngresada = sdf.parse(fechaSiembra)
                        val fechaActual = java.util.Date()
                        if (fechaIngresada == null || fechaIngresada.after(fechaActual)) {
                            Toast.makeText(context, "Fecha inválida o en el futuro", Toast.LENGTH_SHORT).show()
                            return@BotonPrincipalVerde
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Fecha inválida", Toast.LENGTH_SHORT).show()
                        return@BotonPrincipalVerde
                    }

                    cargando = true
                    val db = FirebaseFirestore.getInstance()
                    val auth = FirebaseAuth.getInstance()
                    val idAgricultor = auth.currentUser?.uid ?: "desconocido"

                    // SI ESTAMOS EDITANDO, USAMOS EL ID EXISTENTE. SI NO, CREAMOS UNO NUEVO.
                    val idFinal = cultivoId ?: UUID.randomUUID().toString()
                    val fechaFormateada = "${fechaSiembra.substring(0, 2)}/${fechaSiembra.substring(2, 4)}/${fechaSiembra.substring(4)}"

                    val datosGuardar = hashMapOf(
                        "id" to idFinal,
                        "idAgricultor" to idAgricultor,
                        "nombre" to nombre,
                        "variedad" to variedad,
                        "fechaSiembra" to fechaFormateada,
                        "areaSembrada" to areaSembrada,
                        "estado" to "Activo",
                        "tieneFoto" to false
                    )

                    db.collection("cultivos").document(idFinal)
                        .set(datosGuardar)
                        .addOnSuccessListener {
                            cargando = false
                            onGuardarCultivo()
                        }
                        .addOnFailureListener {
                            cargando = false
                            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                        }
                }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

class DateTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 8) text.text.substring(0..7) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i == 1 || i == 3) out += "/"
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 1) return offset; if (offset <= 3) return offset + 1; if (offset <= 8) return offset + 2; return 10
            }
            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 2) return offset; if (offset <= 5) return offset - 1; if (offset <= 10) return offset - 2; return 8
            }
        }
        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}