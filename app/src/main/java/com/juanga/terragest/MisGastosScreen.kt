package com.juanga.terragest

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Locale

data class Gasto(
    val idGasto: String = "",
    val cultivo: String = "",
    val fecha: String = "",
    val categoria: String = "",
    val descripcion: String = "",
    val valor: Double = 0.0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisGastosScreen(
    onNavegarAtras: () -> Unit = {},
    onNavegarRegistrarGasto: () -> Unit = {}
) {
    val db = FirebaseFirestore.getInstance()
    var busqueda by remember { mutableStateOf("") }

    var listaGastos by remember { mutableStateOf(listOf<Gasto>()) }
    var cargando by remember { mutableStateOf(true) }

    var filtroCultivo by remember { mutableStateOf("Todos los cultivos") }
    var expandedFiltro by remember { mutableStateOf(false) }

    var gastoAEliminar by remember { mutableStateOf<Gasto?>(null) }
    var gastoAEditar by remember { mutableStateOf<Gasto?>(null) }

    LaunchedEffect(Unit) {
        db.collection("gastos").addSnapshotListener { snapshot, e ->
            if (e != null) {
                cargando = false
                return@addSnapshotListener
            }
            if (snapshot != null) {
                listaGastos = snapshot.documents.mapNotNull { it.toObject(Gasto::class.java) }
                cargando = false
            }
        }
    }

    val listaFiltrada = listaGastos.filter { gasto ->
        val coincideBusqueda = gasto.descripcion.contains(busqueda, ignoreCase = true) ||
                gasto.cultivo.contains(busqueda, ignoreCase = true)
        val coincideFiltro = if (filtroCultivo == "Todos los cultivos") true else gasto.cultivo == filtroCultivo
        coincideBusqueda && coincideFiltro
    }

    val opcionesCultivos = listOf("Todos los cultivos") + listaGastos.map { it.cultivo }.distinct()
    val totalGastos = listaFiltrada.sumOf { it.valor }
    val formatoTotal = NumberFormat.getNumberInstance(Locale("es", "CO")).format(totalGastos)

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF2F2F2)).padding(24.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("<", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.clickable { onNavegarAtras() }.padding(end = 16.dp, top = 8.dp, bottom = 8.dp))
            Text("Mis Gastos", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.width(32.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = busqueda, onValueChange = { busqueda = it },
            placeholder = { Text("Buscar gastos", color = Color.Gray) },
            leadingIcon = { Icon(painterResource(id = R.drawable.paninicio_logolupa), contentDescription = "Buscar", modifier = Modifier.size(20.dp), tint = Color.Unspecified) },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(25.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent)
        )

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(expanded = expandedFiltro, onExpandedChange = { expandedFiltro = !expandedFiltro }) {
            OutlinedTextField(
                value = filtroCultivo, onValueChange = {}, readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFiltro) },
                modifier = Modifier.fillMaxWidth().menuAnchor(), shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedBorderColor = Color.LightGray, unfocusedBorderColor = Color.LightGray)
            )
            ExposedDropdownMenu(expanded = expandedFiltro, onDismissRequest = { expandedFiltro = false }) {
                opcionesCultivos.forEach { seleccion ->
                    DropdownMenuItem(text = { Text(seleccion) }, onClick = { filtroCultivo = seleccion; expandedFiltro = false })
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))) {
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Total Gastos ($filtroCultivo)", fontSize = 14.sp, color = Color(0xFF3C733F))
                Text(text = "$ $formatoTotal", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3C733F))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (cargando) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color(0xFF3C733F)) }
        } else if (listaFiltrada.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) { Text("No hay gastos registrados.", color = Color.Gray, textAlign = TextAlign.Center) }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(listaFiltrada) { gasto ->
                    val formatoMoneda = NumberFormat.getNumberInstance(Locale("es", "CO")).format(gasto.valor)

                    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(painter = painterResource(id = R.drawable.gen_circulopequeverde), contentDescription = null, modifier = Modifier.size(40.dp))
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "${gasto.categoria} - ${gasto.cultivo}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                                    Text(text = "${gasto.fecha} | ${gasto.descripcion}", fontSize = 12.sp, color = Color.Gray)
                                }
                                Text(text = "$ $formatoMoneda", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Divider(color = Color(0xFFF2F2F2))
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                IconButton(onClick = { gastoAEditar = gasto }) {
                                    Icon(painter = painterResource(id = R.drawable.gen_editar), contentDescription = "Editar", modifier = Modifier.size(24.dp), tint = Color.Unspecified)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(onClick = { gastoAEliminar = gasto }) {
                                    Icon(painter = painterResource(id = R.drawable.gen_eliminar), contentDescription = "Eliminar", modifier = Modifier.size(24.dp), tint = Color.Unspecified)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        BotonPrincipalVerde(textoDelBoton = "+ Registrar gasto", alHacerClic = onNavegarRegistrarGasto)
    }

    if (gastoAEliminar != null) {
        AlertDialog(
            onDismissRequest = { gastoAEliminar = null },
            title = { Text("Eliminar Gasto", fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro de que deseas eliminar este gasto?") },
            confirmButton = {
                TextButton(onClick = {
                    db.collection("gastos").document(gastoAEliminar!!.idGasto).delete()
                    gastoAEliminar = null
                }) { Text("Eliminar", color = Color.Red, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { gastoAEliminar = null }) { Text("Cancelar", color = Color.Black) }
            },
            containerColor = Color.White
        )
    }

    if (gastoAEditar != null) {
        var editCultivo by remember { mutableStateOf(gastoAEditar!!.cultivo) }
        var editFecha by remember { mutableStateOf(gastoAEditar!!.fecha) }
        var editCategoria by remember { mutableStateOf(gastoAEditar!!.categoria) }
        var editDescripcion by remember { mutableStateOf(gastoAEditar!!.descripcion) }
        var editValor by remember { mutableStateOf(gastoAEditar!!.valor.toInt().toString()) }

        AlertDialog(
            onDismissRequest = { gastoAEditar = null },
            title = { Text("Actualizar Gasto", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    OutlinedTextField(value = editCultivo, onValueChange = { editCultivo = it }, label = { Text("Cultivo") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editFecha, onValueChange = { editFecha = it }, label = { Text("Fecha") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editCategoria, onValueChange = { editCategoria = it }, label = { Text("Categoría") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editDescripcion, onValueChange = { editDescripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editValor, onValueChange = { if (it.all { char -> char.isDigit() }) editValor = it }, label = { Text("Valor ($)") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val actualizaciones = mapOf(
                        "cultivo" to editCultivo,
                        "fecha" to editFecha,
                        "categoria" to editCategoria,
                        "descripcion" to editDescripcion,
                        "valor" to (editValor.toDoubleOrNull() ?: 0.0)
                    )
                    db.collection("gastos").document(gastoAEditar!!.idGasto).update(actualizaciones)
                    gastoAEditar = null
                }) { Text("Actualizar Info", color = Color(0xFF3C733F), fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { gastoAEditar = null }) { Text("Cancelar", color = Color.Black) }
            },
            containerColor = Color.White
        )
    }
}