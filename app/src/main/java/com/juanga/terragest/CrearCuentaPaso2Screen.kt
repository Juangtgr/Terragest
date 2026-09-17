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
// NUEVO: Importamos Firebase
import com.google.firebase.auth.FirebaseAuth

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

    // NUEVO: Variables para Firebase
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    var cargando by remember { mutableStateOf(false) }

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
            Text(
                text = "<", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black,
                modifier = Modifier.clickable { onNavegarAtras() }.padding(end = 16.dp, top = 8.dp, bottom = 8.dp)
            )
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
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
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
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
            CheckDeValidacion("Mínimo 8 caracteres")
            CheckDeValidacion("Una letra mayúscula")
            CheckDeValidacion("Un número")
            CheckDeValidacion("Un carácter especial (!@#$%)")
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
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // LA MAGIA DE FIREBASE OCURRE AQUÍ
        BotonPrincipalVerde(
            textoDelBoton = if (cargando) "Creando cuenta..." else "Crear cuenta",
            alHacerClic = {
                // 1. Validaciones básicas
                if (correo.isEmpty() || contrasena.isEmpty() || confirmar.isEmpty()) {
                    Toast.makeText(context, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show()
                    return@BotonPrincipalVerde
                }
                if (contrasena != confirmar) {
                    Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                    return@BotonPrincipalVerde
                }
                if (contrasena.length < 6) {
                    Toast.makeText(context, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                    return@BotonPrincipalVerde
                }

                // 2. Intentar crear el usuario en Firebase
                cargando = true
                auth.createUserWithEmailAndPassword(correo.trim(), contrasena)
                    .addOnCompleteListener { task ->
                        cargando = false
                        if (task.isSuccessful) {
                            // Éxito: Navegar a la pantalla verde de confirmación
                            onNavegarExito()
                        } else {
                            // Error: Mostrar el motivo (ej: correo ya existe, mal formato)
                            Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text("¿Ya tienes una cuenta? Iniciar sesión", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.clickable { onNavegarLogin() }.padding(8.dp))
    }
}

@Composable
fun CheckDeValidacion(texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Icon(painter = painterResource(id = R.drawable.gen_checkverde), contentDescription = "Check", tint = Color.Unspecified, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = texto, fontSize = 12.sp, color = Color.DarkGray)
    }
}