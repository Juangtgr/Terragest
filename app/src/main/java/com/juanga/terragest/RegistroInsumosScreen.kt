package com.juanga.terragest

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RegistroInsumosScreen(
    onNavegarAtras: () -> Unit = {},
    onGuardarInsumo: () -> Unit = {}
) {
    var cultivo by remember { mutableStateOf("") }
    var insumo by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var unidad by remember { mutableStateOf("") }
    var proveedor by remember { mutableStateOf("") }
    var valorUnitario by remember { mutableStateOf("") }
    var fechaCompra by remember { mutableStateOf("") }

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
            Text("Registro de insumos", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.width(32.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier.size(100.dp).clip(RoundedCornerShape(12.dp)).background(Color.White).clickable { /* Abrir galería */ },
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

        Text("Cultivo", fontWeight = FontWeight.Bold, color = Color.Black)
        OutlinedTextField(
            value = cultivo, onValueChange = {}, readOnly = true, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp), placeholder = { Text("Ej: Papa", color = Color.Gray) },
            trailingIcon = { Icon(painterResource(id = R.drawable.gen_flechaabajo), contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Unspecified) },
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text("Insumos", fontWeight = FontWeight.Bold, color = Color.Black)
        OutlinedTextField(
            value = insumo, onValueChange = {}, readOnly = true, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp), placeholder = { Text("Ej: Fertilizante triple 15", color = Color.Gray) },
            trailingIcon = { Icon(painterResource(id = R.drawable.gen_flechaabajo), contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Unspecified) },
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Fila para Cantidad y Unidad
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Cantidad", fontWeight = FontWeight.Bold, color = Color.Black)
                OutlinedTextField(
                    value = cantidad, onValueChange = { cantidad = it }, modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp), placeholder = { Text("50", color = Color.Gray) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Unidad", fontWeight = FontWeight.Bold, color = Color.Black)
                OutlinedTextField(
                    value = unidad, onValueChange = {}, readOnly = true, modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp), placeholder = { Text("Kg", color = Color.Gray) },
                    trailingIcon = { Icon(painterResource(id = R.drawable.gen_flechaabajo), contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Unspecified) },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text("Proveedor", fontWeight = FontWeight.Bold, color = Color.Black)
        OutlinedTextField(
            value = proveedor, onValueChange = {}, readOnly = true, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp), placeholder = { Text("Agroinsumos del Sur", color = Color.Gray) },
            trailingIcon = { Icon(painterResource(id = R.drawable.gen_flechaabajo), contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Unspecified) },
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text("Valor unitario", fontWeight = FontWeight.Bold, color = Color.Black)
        OutlinedTextField(
            value = valorUnitario, onValueChange = { valorUnitario = it }, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp), placeholder = { Text("$ 2.400", color = Color.Gray) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text("Fecha de compra", fontWeight = FontWeight.Bold, color = Color.Black)
        OutlinedTextField(
            value = fechaCompra,
            onValueChange = { if (it.length <= 8 && it.all { char -> char.isDigit() }) { fechaCompra = it } },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), placeholder = { Text("DD/MM/AAAA", color = Color.Gray) },
            trailingIcon = { Icon(painterResource(id = R.drawable.gen_logocalendariopequeno), contentDescription = null, modifier = Modifier.size(24.dp), tint = Color.Unspecified) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
        )
        Spacer(modifier = Modifier.height(32.dp))

        BotonPrincipalVerde(textoDelBoton = "Guardar insumo", alHacerClic = onGuardarInsumo)
        Spacer(modifier = Modifier.height(24.dp))
    }
}