package com.juanga.terragest

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
       fun BienvenidaScreen(
           onNavegarLogin: () -> Unit = {},
           onNavegarRegistro: () -> Unit = {}
           ) {

        // 1. Capa del Fondo (Imagen de naturaleza)
        Image(
            painter = painterResource(id = R.drawable.in_pres_fondo),
            contentDescription = "Fondo de naturaleza",
            contentScale = ContentScale.Crop, // Esto estira la imagen para llenar la pantalla
            modifier = Modifier.fillMaxSize()
        )

        // 2. Capa del Contenido (Logo, textos y botones apilados verticalmente)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            // Logo
            Image(
                painter = painterResource(id = R.drawable.in_pres_logo),
                contentDescription = "Logo Terragest",
                modifier = Modifier.size(180.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Título
            Text(
                text = "¡Bienvenido agricultor!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtítulo
            Text(
                text = "Organiza, controla y potencia tus cultivos desde tu celular",
                fontSize = 14.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Este Spacer empuja los botones hacia la parte de abajo de la pantalla
            Spacer(modifier = Modifier.weight(1f))

            // Botón: Inicia Sesión
            OutlinedButton(
                onClick  =  onNavegarLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent, // Fondo transparente
                    contentColor = Color.Black // Texto negro
                ),
                border = BorderStroke(2.dp, Color.White) // Borde blanco
            ) {
                Text("Inicia sesión", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón: Crear Cuenta
            OutlinedButton(
                onClick = onNavegarRegistro,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Black
                ),
                border = BorderStroke(2.dp, Color.White)
            ) {
                Text("Crear cuenta", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }


// Visualización de la pantalla
@Preview(showBackground = true)
@Composable
fun VistaPreviaBienvenida() {
    BienvenidaScreen()
}