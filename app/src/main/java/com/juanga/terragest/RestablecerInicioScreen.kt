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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RestablecerInicioScreen(
    onNavegarAtras: () -> Unit = {},
    onNavegarPaso1: () -> Unit = {}
) {
    var correo by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF2F2F2)).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "<", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black,
                modifier = Modifier.clickable { onNavegarAtras() }.padding(end = 16.dp, top = 8.dp, bottom = 8.dp)
            )
            Text("Recuperar contraseña", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.width(32.dp))
        }

        Spacer(modifier = Modifier.height(48.dp))

        Image(painter = painterResource(id = R.drawable.reg_candadoverde), contentDescription = null, modifier = Modifier.size(80.dp))

        Spacer(modifier = Modifier.height(24.dp))

        Text("¿Olvidaste tu contraseña?", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Ingresa el correo electrónico asociado a tu cuenta para enviarte un código de verificación.",
            fontSize = 14.sp, color = Color.DarkGray, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "Correo electrónico", modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = correo, onValueChange = { correo = it },
            leadingIcon = { Icon(painterResource(id = R.drawable.gen_maillogo), contentDescription = null) },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
            // NUEVO: Letras negras
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
        )

        Spacer(modifier = Modifier.weight(1f))

        // NUEVO: Funcionalidad de continuar
        BotonPrincipalVerde(
            textoDelBoton = "Continuar",
            alHacerClic = { onNavegarPaso1() }
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}