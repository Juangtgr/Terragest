package com.juanga.terragest

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InicioSesionScreen(
    onNavegarRecuperar: () -> Unit = {},
    onNavegarAtras: () -> Unit = {},
    onNavegarInicio: () -> Unit = {},
    onNavegarAdmin: () -> Unit = {} // NUEVO: Para el admin
) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var verContrasena by remember { mutableStateOf(false) }

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

        // EL TRUCO DEL ADMIN
        BotonPrincipalVerde(
            textoDelBoton = "Inicia sesión",
            alHacerClic = {
                if (correo.lowercase().trim() == "admin@terragest.com") {
                    onNavegarAdmin()
                } else {
                    onNavegarInicio()
                }
            }
        )
    }
}