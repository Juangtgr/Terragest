package com.juanga.terragest

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
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
fun AcercaDeScreen(
    onNavegarAtras: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF2F2F2)).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("<", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.clickable { onNavegarAtras() }.padding(end = 16.dp, top = 8.dp, bottom = 8.dp))
            Text("Acerca de Terragest", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.width(32.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider(color = Color(0xFF3C733F), thickness = 2.dp)
        Spacer(modifier = Modifier.height(48.dp))

        Image(painter = painterResource(id = R.drawable.in_pres_logo), contentDescription = "Logo", modifier = Modifier.size(200.dp))

        Spacer(modifier = Modifier.height(24.dp))
        Text("Sistema de gestión agrícola", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Aplicación desarrollada para apoyar\na los agricultores en la gestión\neficiente\nde sus cultivos",
            fontSize = 16.sp, color = Color.Black, textAlign = TextAlign.Center, fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.weight(1f))

        Text("Diana Benavides\nSamuel Caicedo - Juan Lopez - Nicolas Escobar\n29/05/2026\nversión 1.1\nContacto: 3186075203",
            fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(24.dp))
    }
}