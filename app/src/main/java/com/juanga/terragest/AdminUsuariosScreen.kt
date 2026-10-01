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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore

data class Usuario(val id: String = "", val correo: String = "", val rol: String = "", val activo: Boolean = true)

@Composable
fun AdminUsuariosScreen(onCerrarSesion: () -> Unit = {}) {
    var busqueda by remember { mutableStateOf("") }
    var listaUsuarios by remember { mutableStateOf(listOf<Usuario>()) }
    var cargando by remember { mutableStateOf(true) }
    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(Unit) {
        db.collection("usuarios").addSnapshotListener { snapshot, e ->
            if (e != null || snapshot == null) {
                cargando = false
                return@addSnapshotListener
            }
            listaUsuarios = snapshot.documents.mapNotNull { doc ->
                Usuario(
                    id = doc.id,
                    correo = doc.getString("correo") ?: "Sin correo",
                    rol = doc.getString("rol") ?: "Agricultor",
                    activo = doc.getBoolean("activo") ?: true
                )
            }
            cargando = false
        }
    }

    val listaFiltrada = listaUsuarios.filter { it.correo.contains(busqueda, ignoreCase = true) }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF2F2F2)).padding(24.dp)) {
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

        OutlinedTextField(
            value = busqueda, onValueChange = { busqueda = it },
            placeholder = { Text("Buscar usuario por correo", color = Color.Gray) },
            leadingIcon = { Icon(painterResource(id = R.drawable.paninicio_logolupa), contentDescription = null, modifier = Modifier.size(20.dp)) },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(25.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent)
        )

        Spacer(modifier = Modifier.height(24.dp))
        Text("Usuarios registrados", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(16.dp))

        if (cargando) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color(0xFF3C733F)) }
        } else if (listaFiltrada.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) { Text("No hay usuarios registrados.", color = Color.Gray) }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(listaFiltrada) { usuario ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Image(painter = painterResource(id = R.drawable.gen_personaicon), contentDescription = null, modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = usuario.correo, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                                Text(text = "Rol: ${usuario.rol}", fontSize = 12.sp, color = Color(0xFF3C733F), fontWeight = FontWeight.Bold)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                val colorEstado = if (usuario.activo) Color(0xFF87BF4E) else Color.Red
                                val textoEstado = if (usuario.activo) "Activo" else "Bloqueado"
                                Box(modifier = Modifier.background(colorEstado.copy(alpha = 0.2f), RoundedCornerShape(12.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                                    Text(text = textoEstado, color = colorEstado, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if(usuario.activo) "Bloquear" else "Activar",
                                    fontSize = 12.sp, color = Color.Blue,
                                    modifier = Modifier.clickable {
                                        db.collection("usuarios").document(usuario.id).update("activo", !usuario.activo)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}