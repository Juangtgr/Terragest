package com.juanga.terragest
import androidx.activity.compose.BackHandler // Importante
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ExitoScreen(
    mensajePrincipal: String = "Cuenta creada\ncorrectamente!",
    textoDelBoton: String = "Iniciar sesión",
    onNavegarLogin: () -> Unit = {},
    onNavegarInicio: () -> Unit = {} // NUEVO
) {
    // Interceptamos el botón físico de Android
    BackHandler {
        onNavegarInicio()
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF2F2F2)).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Text(text = mensajePrincipal, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(32.dp))
        Image(painter = painterResource(id = R.drawable.gen_checkverdegrande), contentDescription = "Éxito", modifier = Modifier.size(120.dp))
        Spacer(modifier = Modifier.weight(1f))
        BotonPrincipalVerde(textoDelBoton = textoDelBoton, alHacerClic = { onNavegarLogin() })
        Spacer(modifier = Modifier.height(16.dp))
    }
}