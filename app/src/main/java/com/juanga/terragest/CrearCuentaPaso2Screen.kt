package com.juanga.terragest

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F2))
            // LA MAGIA: imePadding hace que el teclado empuje el contenido y verticalScroll permite deslizar
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

        Text(text = "Correo electrónico", modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = correo, onValueChange = { correo = it },
            leadingIcon = { Icon(painterResource(id = R.drawable.gen_maillogo), contentDescription = null) },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Contraseña", modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = contrasena, onValueChange = { contrasena = it },
            visualTransformation = if (verContrasena) VisualTransformation.None else PasswordVisualTransformation(),
            leadingIcon = { Icon(painterResource(id = R.drawable.gen_candadologo), contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { verContrasena = !verContrasena }) {
                    // AQUÍ CAMBIA EL ICONO
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

        Text(text = "Confirmar contraseña", modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = confirmar, onValueChange = { confirmar = it },
            visualTransformation = if (verConfirmar) VisualTransformation.None else PasswordVisualTransformation(),
            leadingIcon = { Icon(painterResource(id = R.drawable.gen_candadologo), contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { verConfirmar = !verConfirmar }) {
                    // AQUÍ CAMBIA EL ICONO
                    val icono = if (verConfirmar) R.drawable.gen_ojoabierto else R.drawable.gen_ojocerrado
                    Icon(painterResource(id = icono), contentDescription = "Ver contraseña")
                }
            },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
        )

        Spacer(modifier = Modifier.height(32.dp))

        BotonPrincipalVerde(
            textoDelBoton = "Crear cuenta",
            alHacerClic = { onNavegarExito() }
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