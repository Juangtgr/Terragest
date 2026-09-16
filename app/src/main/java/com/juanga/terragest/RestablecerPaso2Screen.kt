package com.juanga.terragest

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
fun RestablecerPaso2Screen(
    metodo: String = "correo", // Recibe la decisión
    onNavegarAtras: () -> Unit = {},
    onNavegarPaso3: () -> Unit = {}
) {
    var codigo by remember { mutableStateOf("") }

    // Lógica para cambiar los textos e íconos dinámicamente
    val esCorreo = metodo == "correo"
    val tituloDestino = if (esCorreo) "Enviamos un código de verificación a" else "Enviamos un código SMS a"
    val textoDestino = if (esCorreo) "Maria@gmail.com" else "+57 3000000000"
    val icono = if (esCorreo) R.drawable.reg_mailverdelogo else R.drawable.reg_telverdelogo

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F2))
            .imePadding() // Soluciona el teclado
            .verticalScroll(rememberScrollState()) // Soluciona el teclado
            .padding(24.dp),
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
            Column(horizontalAlignment = Alignment.CenterHorizontally) { Box(modifier = Modifier.size(32.dp).background(Color(0xFF87BF4E), CircleShape), contentAlignment = Alignment.Center) { Icon(painterResource(id = R.drawable.gen_checkverde), contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp)) }
                Text("Verificación", fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp)) }
            Divider(modifier = Modifier.width(30.dp).padding(top = 16.dp), color = Color(0xFF87BF4E), thickness = 2.dp)
            Column(horizontalAlignment = Alignment.CenterHorizontally) { Box(modifier = Modifier.size(32.dp).background(Color(0xFF87BF4E), CircleShape), contentAlignment = Alignment.Center) { Text("2", color = Color.White, fontWeight = FontWeight.Bold) }
                Text("Código", fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp)) }
            Divider(modifier = Modifier.width(30.dp).padding(top = 16.dp), color = Color.LightGray, thickness = 2.dp)
            Column(horizontalAlignment = Alignment.CenterHorizontally) { Box(modifier = Modifier.size(32.dp).background(Color(0xFFE0E0E0), CircleShape), contentAlignment = Alignment.Center) { Text("3", color = Color.Gray, fontWeight = FontWeight.Bold) }
                Text("Nueva\ncontraseña", fontSize = 10.sp, color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 4.dp)) }
        }

        Spacer(modifier = Modifier.height(48.dp))
        Image(painter = painterResource(id = icono), contentDescription = null, modifier = Modifier.size(80.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text("Hemos enviado tu código", fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(8.dp))
        Text(tituloDestino, fontSize = 14.sp, color = Color.DarkGray, textAlign = TextAlign.Center)
        Text(textoDestino, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(32.dp))

        // El componente de los 4 cuadros para escribir
        BasicTextField(
            value = codigo,
            onValueChange = { if (it.length <= 4) codigo = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                repeat(4) { index ->
                    val char = if (index < codigo.length) codigo[index].toString() else ""
                    Box(
                        modifier = Modifier.size(56.dp).background(Color.White, RoundedCornerShape(12.dp)).border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = char, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Validación aplicada aquí:
        BotonPrincipalVerde(
            textoDelBoton = "Verificar código",
            alHacerClic = {
                if (codigo.length == 4) {
                    onNavegarPaso3()
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}