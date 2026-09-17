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
fun InformacionPersonalScreen(
    onNavegarAtras: () -> Unit = {}
) {
    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF2F2F2)).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("<", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.clickable { onNavegarAtras() }.padding(end = 16.dp, top = 8.dp, bottom = 8.dp))
            Text("Información Personal", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.width(32.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))
        Image(painter = painterResource(id = R.drawable.gen_personaicon), contentDescription = "Foto de perfil", modifier = Modifier.size(100.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text("Cambiar foto", color = Color(0xFF3C733F), fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.clickable { /* Lógica futura */ })

        Spacer(modifier = Modifier.height(32.dp))

        Text("Nombres", modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold, color = Color.Black)
        OutlinedTextField(
            value = nombres, onValueChange = { nombres = it }, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp), placeholder = { Text("Ej: Maria", color = Color.Gray) },
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Apellidos", modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold, color = Color.Black)
        OutlinedTextField(
            value = apellidos, onValueChange = { apellidos = it }, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp), placeholder = { Text("Ej: Rodriguez", color = Color.Gray) },
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Teléfono", modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold, color = Color.Black)
        OutlinedTextField(
            value = telefono, onValueChange = { telefono = it }, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp), placeholder = { Text("Ej: 3100000000", color = Color.Gray) },
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
        )

        Spacer(modifier = Modifier.weight(1f))

        BotonPrincipalVerde(textoDelBoton = "Guardar cambios", alHacerClic = onNavegarAtras)
        Spacer(modifier = Modifier.height(16.dp))
    }
}