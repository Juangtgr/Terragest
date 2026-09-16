package com.juanga.terragest

import androidx.compose.foundation.Image
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
import com.juanga.terragest.R // <-- Esta es la línea mágica que quita el error rojo

@Composable
fun RestablecerPaso3Screen(
    onNavegarAtras: () -> Unit = {},
    onNavegarExito: () -> Unit = {}
) {
    var contrasena by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }
    var verContrasena by remember { mutableStateOf(false) }
    var verConfirmar by remember { mutableStateOf(false) }

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
            Text("Cambiar contraseña", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.width(32.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Stepper
        Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) { Box(modifier = Modifier.size(32.dp).background(Color(0xFF87BF4E), CircleShape), contentAlignment = Alignment.Center) { Icon(painterResource(id = R.drawable.gen_checkverde), contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp)) }
                Text("Verificación", fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp)) }
            Divider(modifier = Modifier.width(30.dp).padding(top = 16.dp), color = Color(0xFF87BF4E), thickness = 2.dp)
            Column(horizontalAlignment = Alignment.CenterHorizontally) { Box(modifier = Modifier.size(32.dp).background(Color(0xFF87BF4E), CircleShape), contentAlignment = Alignment.Center) { Icon(painterResource(id = R.drawable.gen_checkverde), contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp)) }
                Text("Código", fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp)) }
            Divider(modifier = Modifier.width(30.dp).padding(top = 16.dp), color = Color(0xFF87BF4E), thickness = 2.dp)
            Column(horizontalAlignment = Alignment.CenterHorizontally) { Box(modifier = Modifier.size(32.dp).background(Color(0xFF87BF4E), CircleShape), contentAlignment = Alignment.Center) { Text("3", color = Color.White, fontWeight = FontWeight.Bold) }
                Text("Nueva\ncontraseña", fontSize = 10.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 4.dp)) }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Image(painter = painterResource(id = R.drawable.reg_candadoverde), contentDescription = null, modifier = Modifier.size(60.dp))

        Spacer(modifier = Modifier.height(16.dp))
        Text("Crea tu nueva contraseña", fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(24.dp))

        Text("Nueva contraseña", modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = contrasena, onValueChange = { contrasena = it },
            visualTransformation = if (verContrasena) VisualTransformation.None else PasswordVisualTransformation(),
            leadingIcon = { Icon(painterResource(id = R.drawable.gen_candadologo), contentDescription = null) },
            trailingIcon = { IconButton(onClick = { verContrasena = !verContrasena }) {
                val icono = if (verContrasena) R.drawable.gen_ojoabierto else R.drawable.gen_ojocerrado
                Icon(painterResource(id = icono), contentDescription = null) }
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

        Text("Confirmar contraseña", modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = confirmar, onValueChange = { confirmar = it },
            visualTransformation = if (verConfirmar) VisualTransformation.None else PasswordVisualTransformation(),
            leadingIcon = { Icon(painterResource(id = R.drawable.gen_candadologo), contentDescription = null) },
            trailingIcon = { IconButton(onClick = { verConfirmar = !verConfirmar }) {
                val icono = if (verConfirmar) R.drawable.gen_ojoabierto else R.drawable.gen_ojocerrado
                Icon(painterResource(id = icono), contentDescription = null) }
            },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
        )

        Spacer(modifier = Modifier.height(32.dp))
        BotonPrincipalVerde(textoDelBoton = "Restablecer contraseña", alHacerClic = { onNavegarExito() })
        Spacer(modifier = Modifier.height(16.dp))
    }
}


