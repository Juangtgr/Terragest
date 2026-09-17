package com.juanga.terragest

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

// Modelo de datos para Usuarios
data class Usuario(val nombre: String, val correo: String, val rol: String, val activo: Boolean)

@Composable
fun AdminUsuariosScreen(
    onCerrarSesion: () -> Unit = {}
) {
    var busqueda by remember { mutableStateOf("") }

    // Lista simulada de usuarios para mostrar en el prototipo
    val listaUsuarios = listOf(
        Usuario("Maria Rodriguez", "maria@gmail.com", "Agricultor", true),
        Usuario("Juan Lopez", "juan@terragest.com", "Administrador", true),
        Usuario("Carlos Ruiz", "carlos@gmail.com", "Agricultor", false)
    )

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF2F2F2)).padding(24.dp)
    ) {
        // Encabezado
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Panel de Administración", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(id = R.drawable.panperf_exitlogopequeno),
                contentDescription = "Cerrar sesión",
                modifier = Modifier.size(28.dp).clickable { onCerrarSesion() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider(color = Color(0xFF3C733F), thickness = 2.dp)
        Spacer(modifier = Modifier.height(24.dp))

        // Buscador de usuarios
        OutlinedTextField(
            value = busqueda, onValueChange = { busqueda = it },
            placeholder = { Text("Buscar usuario por nombre o correo", color = Color.Gray) },
            leadingIcon = { Icon(painterResource(id = R.drawable.paninicio_logolupa), contentDescription = null, modifier = Modifier.size(20.dp)) },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(25.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent)
        )

        Spacer(modifier = Modifier.height(24.dp))
        Text("Usuarios registrados", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(16.dp))

        // Lista de Usuarios
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(listaUsuarios) { usuario ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Image(painter = painterResource(id = R.drawable.gen_personaicon), contentDescription = null, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = usuario.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                            Text(text = usuario.correo, fontSize = 12.sp, color = Color.Gray)
                            Text(text = "Rol: ${usuario.rol}", fontSize = 12.sp, color = Color(0xFF3C733F), fontWeight = FontWeight.Bold)
                        }

                        // Estado y acciones
                        Column(horizontalAlignment = Alignment.End) {
                            val colorEstado = if (usuario.activo) Color(0xFF87BF4E) else Color.Red
                            val textoEstado = if (usuario.activo) "Activo" else "Bloqueado"
                            Box(modifier = Modifier.background(colorEstado.copy(alpha = 0.2f), RoundedCornerShape(12.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                                Text(text = textoEstado, color = colorEstado, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Editar rol", fontSize = 12.sp, color = Color.Blue, modifier = Modifier.clickable { /* Futura lógica */ })
                        }
                    }
                }
            }
        }
    }
}