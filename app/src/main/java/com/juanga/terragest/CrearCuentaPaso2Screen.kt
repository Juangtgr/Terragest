package com.juanga.terragest

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun CrearCuentaPaso2Screen(
    onNavegarAtras: () -> Unit = {},
    onNavegarExito: () -> Unit = {},
    onNavegarLogin: () -> Unit = {}
) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }

    var verContrasena by remember { mutableStateOf(false) }
    var verConfirmar by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    var cargando by remember { mutableStateOf(false) }

    val tieneMinimo = contrasena.length >= 8
    val tieneMayuscula = contrasena.any { it.isUpperCase() }
    val tieneNumero = contrasena.any { it.isDigit() }
    val tieneEspecial = contrasena.any { "!@#$%^&*()_+-=[]{}|;':\",./<>?".contains(it) }
    val contrasenasCoinciden = contrasena.isNotEmpty() && contrasena == confirmar

    val cumpleTodo = tieneMinimo && tieneMayuscula && tieneNumero && tieneEspecial && contrasenasCoinciden && correo.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F2))
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("<", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.clickable { onNavegarAtras() }.padding(end = 16.dp, top = 8.dp, bottom = 8.dp))
            Text("Crear cuenta", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.width(32.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(32.dp).background(Color(0xFFE0E0E0), CircleShape), contentAlignment = Alignment.Center) { Text("1", color = Color.Gray, fontWeight = FontWeight.Bold) }
            Divider(modifier = Modifier.width(40.dp), color = Color.LightGray, thickness = 2.dp)
            Box(modifier = Modifier.size(32.dp).background(Color(0xFF87BF4E), CircleShape), contentAlignment = Alignment.Center) { Text("2", color = Color.White, fontWeight = FontWeight.Bold) }
            Divider(modifier = Modifier.width(40.dp), color = Color.LightGray, thickness = 2.dp)
            Box(modifier = Modifier.size(32.dp).background(Color(0xFFE0E0E0), CircleShape), contentAlignment = Alignment.Center) { Text("3", color = Color.Gray) }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "Correo electrónico", modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold, color = Color.Black)
        OutlinedTextField(
            value = correo, onValueChange = { correo = it },
            leadingIcon = { Icon(painterResource(id = R.drawable.gen_maillogo), contentDescription = null) },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black, unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White,
                focusedBorderColor = Color(0xFF3C733F), unfocusedBorderColor = Color.LightGray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Contraseña", modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold, color = Color.Black)
        OutlinedTextField(
            value = contrasena, onValueChange = { contrasena = it },
            visualTransformation = if (verContrasena) VisualTransformation.None else PasswordVisualTransformation(),
            leadingIcon = { Icon(painterResource(id = R.drawable.gen_candadologo), contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { verContrasena = !verContrasena }) {
                    val icono = if (verContrasena) R.drawable.gen_ojoabierto else R.drawable.gen_ojocerrado
                    Icon(painterResource(id = icono), contentDescription = "Ver contraseña")
                }
            },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black, unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White,
                focusedBorderColor = Color(0xFF3C733F), unfocusedBorderColor = Color.LightGray
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
            CheckDeValidacionDinamico("Mínimo 8 caracteres", tieneMinimo)
            CheckDeValidacionDinamico("Una letra mayúscula", tieneMayuscula)
            CheckDeValidacionDinamico("Un número", tieneNumero)
            CheckDeValidacionDinamico("Un carácter especial (!@#$%)", tieneEspecial)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Confirmar contraseña", modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold, color = Color.Black)
        OutlinedTextField(
            value = confirmar, onValueChange = { confirmar = it },
            visualTransformation = if (verConfirmar) VisualTransformation.None else PasswordVisualTransformation(),
            leadingIcon = { Icon(painterResource(id = R.drawable.gen_candadologo), contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { verConfirmar = !verConfirmar }) {
                    val icono = if (verConfirmar) R.drawable.gen_ojoabierto else R.drawable.gen_ojocerrado
                    Icon(painterResource(id = icono), contentDescription = "Ver contraseña")
                }
            },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
            isError = contrasena.isNotEmpty() && confirmar.isNotEmpty() && !contrasenasCoinciden,
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black, unfocusedTextColor = Color.Black,
                errorTextColor = Color.Black, // EVITA LA TRANSPARENCIA AL BORRAR
                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White,
                errorContainerColor = Color.White,
                focusedBorderColor = Color(0xFF3C733F), unfocusedBorderColor = Color.LightGray,
                errorBorderColor = Color.Red, errorLeadingIconColor = Color.Red
            )
        )
        if (contrasena.isNotEmpty() && confirmar.isNotEmpty() && !contrasenasCoinciden) {
            Text("Las contraseñas no coinciden", color = Color.Red, fontSize = 12.sp, modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 4.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (!cumpleTodo) {
                    Toast.makeText(context, "Completa todos los requisitos para continuar", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                cargando = true
                auth.createUserWithEmailAndPassword(correo.trim(), contrasena)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userId = task.result?.user?.uid ?: ""
                            val db = FirebaseFirestore.getInstance()
                            val nuevoUsuario = hashMapOf(
                                "id" to userId,
                                "correo" to correo.trim(),
                                "rol" to "Agricultor",
                                "activo" to true,
                                "fechaRegistro" to System.currentTimeMillis()
                            )
                            db.collection("usuarios").document(userId).set(nuevoUsuario)
                                .addOnSuccessListener {
                                    cargando = false
                                    onNavegarExito()
                                }
                                .addOnFailureListener {
                                    cargando = false
                                    onNavegarExito()
                                }
                        } else {
                            cargando = false
                            Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (cumpleTodo) Color(0xFF3C733F) else Color.LightGray
            ),
            shape = RoundedCornerShape(25.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = !cargando
        ) {
            Text(
                text = if (cargando) "Creando cuenta..." else "Crear cuenta",
                color = if (cumpleTodo) Color.White else Color.DarkGray,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("¿Ya tienes una cuenta? Iniciar sesión", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.clickable { onNavegarLogin() }.padding(8.dp))
    }
}

@Composable
fun CheckDeValidacionDinamico(texto: String, cumple: Boolean) {
    val icono = if (cumple) R.drawable.gen_checkverde else R.drawable.gen_xroja
    val colorTexto = if (cumple) Color(0xFF3C733F) else Color.Gray

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Icon(
            painter = painterResource(id = icono),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = texto, fontSize = 12.sp, color = colorTexto, fontWeight = if (cumple) FontWeight.Bold else FontWeight.Normal)
    }
}