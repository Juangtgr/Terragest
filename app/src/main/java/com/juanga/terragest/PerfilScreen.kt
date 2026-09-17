package com.juanga.terragest

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PerfilScreen(
    onNavegarCultivos: () -> Unit = {},
    onNavegarInsumos: () -> Unit = {},
    onNavegarGastos: () -> Unit = {},
    onNavegarReportes: () -> Unit = {},
    onNavegarInicio: () -> Unit = {},
    onNavegarAcercaDe: () -> Unit = {},
    onNavegarInfoPersonal: () -> Unit = {},
    onNavegarConfiguracion: () -> Unit = {},
    onNavegarCambiarContrasena: () -> Unit = {},
    onCerrarSesion: () -> Unit = {}
) {
    // Variables de estado vacías, listas para recibir los datos desde Firebase
    var nombreUsuario by remember { mutableStateOf("Cargando nombre...") }
    var correoUsuario by remember { mutableStateOf("Cargando correo...") }
    var telefonoUsuario by remember { mutableStateOf("Cargando teléfono...") }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                // CORREGIDO: Ahora Inicio funciona y Perfil está marcado como seleccionado
                NavigationBarItem(
                    icon = { Image(painterResource(id = R.drawable.paninicio_logohome), contentDescription = "Inicio", modifier = Modifier.size(24.dp)) },
                    label = { Text("Inicio", fontSize = 10.sp) },
                    selected = false,
                    onClick = onNavegarInicio
                )
                NavigationBarItem(
                    icon = { Image(painterResource(id = R.drawable.paninicio_logomanoplantasincolor), contentDescription = "Cultivos", modifier = Modifier.size(24.dp)) },
                    label = { Text("Cultivos", fontSize = 10.sp) },
                    selected = false,
                    onClick = onNavegarCultivos
                )
                NavigationBarItem(
                    icon = { Image(painterResource(id = R.drawable.paninicio_modenalogosincolor), contentDescription = "Gastos", modifier = Modifier.size(24.dp)) },
                    label = { Text("Gastos", fontSize = 10.sp) },
                    selected = false,
                    onClick = onNavegarGastos
                )
                NavigationBarItem(
                    icon = { Image(painterResource(id = R.drawable.paninicio_documento), contentDescription = "Reportes", modifier = Modifier.size(24.dp)) },
                    label = { Text("Reportes", fontSize = 10.sp) },
                    selected = false,
                    onClick = onNavegarReportes
                )
                NavigationBarItem(
                    icon = { Image(painterResource(id = R.drawable.panperf_perfillogopequeno), contentDescription = "Perfil", modifier = Modifier.size(24.dp)) },
                    label = { Text("Perfil", fontSize = 10.sp) },
                    selected = true,
                    onClick = { /* Ya estamos aquí */ },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = Color(0xFFE8F5E9))
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().background(Color(0xFFF2F2F2)).padding(paddingValues).padding(24.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Perfil", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color(0xFF3C733F), thickness = 2.dp)
            Spacer(modifier = Modifier.height(32.dp))

            // Información del usuario (Dinámica)
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                Image(painter = painterResource(id = R.drawable.gen_personaicon), contentDescription = "Foto", modifier = Modifier.size(80.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = nombreUsuario, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                    Text(text = correoUsuario, fontSize = 14.sp, color = Color.DarkGray)
                    Text(text = telefonoUsuario, fontSize = 14.sp, color = Color.DarkGray)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Card(
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    ItemMenuPerfil("Información personal", R.drawable.gen_personaicon) { onNavegarInfoPersonal() }
                    Divider(color = Color.LightGray)
                    ItemMenuPerfil("Cambiar contraseña", R.drawable.panperf_candadologopequeno) { onNavegarCambiarContrasena() }
                    Divider(color = Color.LightGray)
                    ItemMenuPerfil("Configuración", R.drawable.panperf_configlogopequeno) { onNavegarConfiguracion() }
                    Divider(color = Color.LightGray)
                    ItemMenuPerfil("Acerca de", R.drawable.panperf_exclamacionlogo) { onNavegarAcercaDe() }
                    Divider(color = Color.LightGray)
                    Row(modifier = Modifier.fillMaxWidth().clickable { onCerrarSesion() }.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Image(painter = painterResource(id = R.drawable.panperf_exitlogopequeno), contentDescription = null, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = "Cerrar sesión", fontSize = 16.sp, color = Color.Red, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
fun ItemMenuPerfil(texto: String, icono: Int, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Image(painter = painterResource(id = icono), contentDescription = null, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = texto, fontSize = 16.sp, color = Color.Black, fontWeight = FontWeight.Medium)
    }
}