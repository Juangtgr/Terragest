package com.juanga.terragest
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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
fun RestablecerPaso1Screen(
    correoUsuario: String = "correo@ejemplo.com",
    telefonoUsuario: String = "+57 0000000000",
    onNavegarAtras: () -> Unit = {},
    onNavegarPaso2: (String) -> Unit = {} // Modificado para recibir un texto
) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF2F2F2)).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("<", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.clickable { onNavegarAtras() }.padding(end = 16.dp, top = 8.dp, bottom = 8.dp))
            Text("Cambiar contraseña", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.width(32.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))
        // Stepper
        Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) { Box(modifier = Modifier.size(32.dp).background(Color(0xFF87BF4E), CircleShape), contentAlignment = Alignment.Center) { Text("1", color = Color.White, fontWeight = FontWeight.Bold) }
                Text("Verificación", fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp)) }
            Divider(modifier = Modifier.width(30.dp).padding(top = 16.dp), color = Color.LightGray, thickness = 2.dp)
            Column(horizontalAlignment = Alignment.CenterHorizontally) { Box(modifier = Modifier.size(32.dp).background(Color(0xFFE0E0E0), CircleShape), contentAlignment = Alignment.Center) { Text("2", color = Color.Gray, fontWeight = FontWeight.Bold) }
                Text("Código", fontSize = 10.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp)) }
            Divider(modifier = Modifier.width(30.dp).padding(top = 16.dp), color = Color.LightGray, thickness = 2.dp)
            Column(horizontalAlignment = Alignment.CenterHorizontally) { Box(modifier = Modifier.size(32.dp).background(Color(0xFFE0E0E0), CircleShape), contentAlignment = Alignment.Center) { Text("3", color = Color.Gray, fontWeight = FontWeight.Bold) }
                Text("Nueva\ncontraseña", fontSize = 10.sp, color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 4.dp)) }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Image(painter = painterResource(id = R.drawable.reg_candadoverde), contentDescription = "Candado", modifier = Modifier.size(80.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text("Vamos a cambiar\ntu contraseña", fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Elije un método para recibir el código de\nverificación.", fontSize = 14.sp, color = Color.DarkGray, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = { onNavegarPaso2("correo") }, // ENVÍA CORREO
            modifier = Modifier.fillMaxWidth().height(70.dp),
            shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White), border = BorderStroke(1.dp, Color.LightGray)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Image(painter = painterResource(id = R.drawable.reg_mailverdelogo), contentDescription = "Correo", modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column { Text("Enviar código por correo", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp)
                    Text(correoUsuario, color = Color.Gray, fontSize = 12.sp) }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = { onNavegarPaso2("sms") }, // ENVÍA SMS
            modifier = Modifier.fillMaxWidth().height(70.dp),
            shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White), border = BorderStroke(1.dp, Color.LightGray)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Image(painter = painterResource(id = R.drawable.reg_telverdelogo), contentDescription = "SMS", modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column { Text("Enviar código por SMS", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp)
                    Text(telefonoUsuario, color = Color.Gray, fontSize = 12.sp) }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Box(modifier = Modifier.fillMaxWidth().background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp)).padding(16.dp)) {
            Text("Por tu seguridad, usamos estos métodos para verificar que eres tú.", fontSize = 12.sp, color = Color(0xFF3C733F), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
    }
}