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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NuevoCultivoScreen(
    onNavegarAtras: () -> Unit = {},
    onGuardarCultivo: () -> Unit = {}
) {
    // Variables para Firebase
    var nombre by remember { mutableStateOf("") }
    var variedad by remember { mutableStateOf("") }
    var fechaSiembra by remember { mutableStateOf("") }
    var areaSembrada by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("Activo") }
    var fechaCosecha by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F2))
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("<", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.clickable { onNavegarAtras() }.padding(end = 16.dp, top = 8.dp, bottom = 8.dp))
            Text("Nuevo cultivo", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.width(32.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // MARCO PARA SUBIR FOTO DEL CULTIVO
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .clickable { /* Lógica para abrir galería */ },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(painter = painterResource(id = R.drawable.gen_subirfoto), contentDescription = "Subir foto", modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Añadir foto", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Nombre del cultivo", fontWeight = FontWeight.Bold)
        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), placeholder = { Text("Ej: Papa") })
        Spacer(modifier = Modifier.height(16.dp))

        Text("Variedad", fontWeight = FontWeight.Bold)
        OutlinedTextField(value = variedad, onValueChange = { variedad = it }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), placeholder = { Text("Ej: Diacol Capiro") })
        Spacer(modifier = Modifier.height(16.dp))

        Text("Fecha de siembra", fontWeight = FontWeight.Bold)
        OutlinedTextField(value = fechaSiembra, onValueChange = { fechaSiembra = it }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), placeholder = { Text("DD/MM/AAAA") }, trailingIcon = { Icon(painterResource(id = R.drawable.gen_logocalendariopequeno), contentDescription = null, modifier = Modifier.size(24.dp)) })
        Spacer(modifier = Modifier.height(16.dp))

        Text("Área sembrada", fontWeight = FontWeight.Bold)
        OutlinedTextField(value = areaSembrada, onValueChange = { areaSembrada = it }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), placeholder = { Text("Ej: 1.5 hectáreas") })
        Spacer(modifier = Modifier.height(32.dp))

        BotonPrincipalVerde(textoDelBoton = "Guardar cultivo", alHacerClic = onGuardarCultivo)
        Spacer(modifier = Modifier.height(24.dp))
    }
}