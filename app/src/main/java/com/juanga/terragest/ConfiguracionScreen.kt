package com.juanga.terragest

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ConfiguracionScreen(
    onNavegarAtras: () -> Unit = {}
) {
    val context = LocalContext.current
    var usoDatos by remember { mutableStateOf(false) }
    var modoAhorroEnergia by remember { mutableStateOf(false) }
    var mantenerSesion by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF2F2F2)).padding(24.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("<", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.clickable { onNavegarAtras() }.padding(end = 16.dp, top = 8.dp, bottom = 8.dp))
            Text("Configuración", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.width(32.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Solo descargar imágenes con Wi-Fi", fontSize = 14.sp, color = Color.Black)
                    Switch(
                        checked = usoDatos,
                        onCheckedChange = { usoDatos = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF3C733F))
                    )
                }
                Divider(color = Color(0xFFF2F2F2), modifier = Modifier.padding(vertical = 8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Modo Ahorro de Energía (sin animaciones)", fontSize = 14.sp, color = Color.Black)
                    Switch(
                        checked = modoAhorroEnergia,
                        onCheckedChange = { modoAhorroEnergia = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF3C733F))
                    )
                }
                Divider(color = Color(0xFFF2F2F2), modifier = Modifier.padding(vertical = 8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Mantener sesión iniciada", fontSize = 14.sp, color = Color.Black)
                    Switch(
                        checked = mantenerSesion,
                        onCheckedChange = {
                            mantenerSesion = it
                            Toast.makeText(context, "Configuración actualizada", Toast.LENGTH_SHORT).show()
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF3C733F))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Nota: Estos ajustes se guardan localmente en tu dispositivo.", color = Color.Gray, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
    }
}