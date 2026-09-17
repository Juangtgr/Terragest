package com.juanga.terragest

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Importamos Firebase
import com.google.firebase.auth.FirebaseAuth

@Composable
fun InicioSesionScreen(
    onNavegarRecuperar: () -> Unit = {},
    onNavegarAtras: () -> Unit = {},
    onNavegarInicio: () -> Unit = {},
    onNavegarAdmin: () -> Unit = {}
) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var verContrasena by remember { mutableStateOf(false) }

    // Variables para Firebase
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    var cargando by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF2F2F2)).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("<", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.clickable { onNavegarAtras() }.padding(end = 16.dp, top = 8.dp, bottom = 8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
        Image(painter = painterResource(id = R.drawable.in_pres_logo), contentDescription = null, modifier = Modifier.size(150.dp))
        Spacer(modifier = Modifier.height(40.dp))

        OutlinedTextField(
            value = correo, onValueChange = { correo = it }, label = { Text("Correo electrónico", color = Color.Gray) },
            leadingIcon = { Icon(painterResource(id = R.drawable.gen_maillogo), contentDescription = null) },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = contrasena, onValueChange = { contrasena = it }, label = { Text("Contraseña", color = Color.Gray) },
            visualTransformation = if (verContrasena) VisualTransformation.None else PasswordVisualTransformation(),
            leadingIcon = { Icon(painterResource(id = R.drawable.gen_candadologo), contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { verContrasena = !verContrasena }) {
                    val icono = if (verContrasena) R.drawable.gen_ojoabierto else R.drawable.gen_ojocerrado
                    Icon(painterResource(id = icono), contentDescription = "Ver")
                }
            },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text("¿Olvidaste tu contraseña?", color = Color(0xFF73563D), fontSize = 14.sp, modifier = Modifier.align(Alignment.End).clickable { onNavegarRecuperar() }.padding(vertical = 8.dp))
        Spacer(modifier = Modifier.height(32.dp))

        // BOTÓN CON LÓGICA DE FIREBASE Y EL TRUCO DEL ADMIN
        BotonPrincipalVerde(
            textoDelBoton = if (cargando) "Iniciando..." else "Inicia sesión",
            alHacerClic = {
                val correoLimpio = correo.lowercase().trim()

                // 1. Truco del Administrador
                if (correoLimpio == "admin@terragest.com") {
                    onNavegarAdmin()
                    return@BotonPrincipalVerde
                }

                // 2. Validación básica
                if (correo.isEmpty() || contrasena.isEmpty()) {
                    Toast.makeText(context, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show()
                    return@BotonPrincipalVerde
                }

                // 3. Iniciar sesión real con Firebase
                cargando = true
                auth.signInWithEmailAndPassword(correoLimpio, contrasena)
                    .addOnCompleteListener { task ->
                        cargando = false
                        if (task.isSuccessful) {
                            onNavegarInicio() // Éxito: entra a la app
                        } else {
                            Toast.makeText(context, "Correo o contraseña incorrectos", Toast.LENGTH_LONG).show()
                        }
                    }
            }
        )
    }
}