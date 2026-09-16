package com.juanga.terragest

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CrearCuentaPaso1Screen(
    onNavegarAtras: () -> Unit = {},
    onNavegarPaso2: () -> Unit = {},
    onNavegarLogin: () -> Unit = {}
) {
    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F2))
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
            Box(modifier = Modifier.size(32.dp).background(Color(0xFF87BF4E), CircleShape), contentAlignment = Alignment.Center) { Text("1", color = Color.White, fontWeight = FontWeight.Bold) }
            Divider(modifier = Modifier.width(40.dp), color = Color.LightGray, thickness = 2.dp)
            Box(modifier = Modifier.size(32.dp).background(Color(0xFFE0E0E0), CircleShape), contentAlignment = Alignment.Center) { Text("2", color = Color.Gray) }
            Divider(modifier = Modifier.width(40.dp), color = Color.LightGray, thickness = 2.dp)
            Box(modifier = Modifier.size(32.dp).background(Color(0xFFE0E0E0), CircleShape), contentAlignment = Alignment.Center) { Text("3", color = Color.Gray) }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "Nombres", modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = nombres,
            // NUEVO: Validación. Solo actualiza si escribes letras o espacios
            onValueChange = { siEsLetra -> if (siEsLetra.all { it.isLetter() || it.isWhitespace() }) nombres = siEsLetra },
            leadingIcon = { Icon(painterResource(id = R.drawable.gen_personalogo), contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Apellidos", modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = apellidos,
            // NUEVO: Validación para letras
            onValueChange = { siEsLetra -> if (siEsLetra.all { it.isLetter() || it.isWhitespace() }) apellidos = siEsLetra },
            leadingIcon = { Icon(painterResource(id = R.drawable.gen_personalogo), contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Teléfono", modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = telefono,
            // CORREGIDO: Límite a 10 y solo números.
            onValueChange = { siEsNumero ->
                if (siEsNumero.length <= 10 && siEsNumero.all { it.isDigit() }) telefono = siEsNumero
            },
            leadingIcon = { Icon(painterResource(id = R.drawable.gen_collogo), contentDescription = null, tint = Color.Unspecified) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
        )

        Spacer(modifier = Modifier.weight(1f))

        BotonPrincipalVerde(textoDelBoton = "Continuar", alHacerClic = { onNavegarPaso2() })

        Spacer(modifier = Modifier.height(16.dp))
        Text("¿Ya tienes una cuenta? Iniciar sesión", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.clickable { onNavegarLogin() }.padding(8.dp))
    }
}