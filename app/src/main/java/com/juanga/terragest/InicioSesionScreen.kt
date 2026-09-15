package com.juanga.terragest

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// IMPORTANTE: Asegúrate de importar el paquete R de tu proyecto, por ejemplo:
// import com.tu.paquete.R

@Composable
fun InicioSesionScreen() {
    // 1. Estados (Variables que guardan lo que el usuario escribe)
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    // 2. Contenedor principal (Apila los elementos verticalmente)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F2)) // El color Gris Claro de tu documento
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // 3. Logo (Usa el prefijo in_ que definiste)
        Image(
            painter = painterResource(id = R.drawable.in_pres_logo), // Cambia el nombre si es distinto
            contentDescription = "Logo Terragest",
            modifier = Modifier.size(150.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // 4. Campo de Correo
        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo electrónico") },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.gen_maillogo),
                    contentDescription = "Icono Correo"
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 5. Campo de Contraseña
        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(), // Oculta el texto con puntitos
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.gen_candadologo),
                    contentDescription = "Icono Candado"
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 6. Texto de Olvidaste contraseña
        Text(
            text = "¿Olvidaste tu contraseña?",
            color = Color(0xFF73563D), // El color Marrón Tierra de tu documento
            fontSize = 14.sp,
            modifier = Modifier.align(Alignment.End)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 7. Tu Botón Verde (Llamamos a la función que creamos en los mensajes anteriores)
        BotonPrincipalVerde(
            textoDelBoton = "Inicia sesión",
            alHacerClic = {
                // Aquí pondremos la lógica de validación más adelante
                println("Correo ingresado: $correo")
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun VistaPreviaInicioSesion() {
    InicioSesionScreen()
}