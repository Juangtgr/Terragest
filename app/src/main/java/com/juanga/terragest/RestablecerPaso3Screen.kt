package com.juanga.terragest

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RestablecerPaso3Screen(
    onNavegarAtras: () -> Unit = {},
    onNavegarExito: () -> Unit = {}
) {
    var contrasena by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }
    var verContrasena by remember { mutableStateOf(false) }
    var verConfirmar by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var cargando by remember { mutableStateOf(false) }

    // Evaluaciones reactivas en tiempo real
    val tieneMinimo = contrasena.length >= 8
    val tieneMayuscula = contrasena.any { it.isUpperCase() }
    val tieneNumero = contrasena.any { it.isDigit() }
    val tieneEspecial = contrasena.any { "!@#$%^&*()_+-=[]{}|;':\",./<>?".contains(it) }
    val contrasenasCoinciden = contrasena.isNotEmpty() && contrasena == confirmar

    val cumpleTodo = tieneMinimo && tieneMayuscula && tieneNumero && tieneEspecial && contrasenasCoinciden

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

        // Requisitos dinámicos reutilizando la función que creamos en el paso 1
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
            CheckDeValidacionDinamico("Mínimo 8 caracteres", tieneMinimo)
            CheckDeValidacionDinamico("Una letra mayúscula", tieneMayuscula)
            CheckDeValidacionDinamico("Un número", tieneNumero)
            CheckDeValidacionDinamico("Un carácter especial (!@#$%)", tieneEspecial)
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
            isError = contrasena.isNotEmpty() && confirmar.isNotEmpty() && !contrasenasCoinciden,
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
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
                // Lógica futura de actualización de Firebase
                onNavegarExito()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (cumpleTodo) Color(0xFF3C733F) else Color.LightGray
            ),
            shape = RoundedCornerShape(25.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = !cargando
        ) {
            Text(
                text = if (cargando) "Restableciendo..." else "Restablecer contraseña",
                color = if (cumpleTodo) Color.White else Color.DarkGray,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// Se asume que CheckDeValidacionDinamico ya existe en el paquete gracias a CrearCuentaPaso2Screen.kt.
// Si Android Studio marca un error, asegúrate de que ambos archivos estén en el mismo paquete.