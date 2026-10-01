package com.juanga.terragest

import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

@Composable
fun InformacionPersonalScreen(
    onNavegarAtras: () -> Unit = {}
) {
    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var rutaFotoPerfil by remember { mutableStateOf("") }

    var cargando by remember { mutableStateOf(false) }
    var cargandoInicial by remember { mutableStateOf(true) } // NUEVO: Evita el parpadeo

    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    LaunchedEffect(Unit) {
        if (userId.isNotEmpty()) {
            db.collection("usuarios").document(userId).get()
                .addOnSuccessListener { doc ->
                    nombres = doc.getString("nombres") ?: ""
                    apellidos = doc.getString("apellidos") ?: ""
                    telefono = doc.getString("telefono") ?: ""
                    rutaFotoPerfil = doc.getString("rutaFoto") ?: ""
                    cargandoInicial = false
                }
                .addOnFailureListener {
                    cargandoInicial = false
                }
        } else {
            cargandoInicial = false
        }
    }

    val selectorDeImagen = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val rutaGuardada = guardarImagenLocalmente(context, uri)
            if (rutaGuardada != null) {
                rutaFotoPerfil = rutaGuardada
                if (userId.isNotEmpty()) {
                    val actualizacionImagen = mapOf("rutaFoto" to rutaFotoPerfil)
                    db.collection("usuarios").document(userId).set(actualizacionImagen, SetOptions.merge())
                }
            } else {
                Toast.makeText(context, "Error al guardar la imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF2F2F2)).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("<", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.clickable { onNavegarAtras() }.padding(end = 16.dp, top = 8.dp, bottom = 8.dp))
            Text("Información Personal", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.width(32.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Lógica visual para la foto con estado de carga
        if (cargandoInicial) {
            Box(modifier = Modifier.size(100.dp).clip(CircleShape).background(Color.LightGray), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color(0xFF3C733F))
            }
        } else {
            if (rutaFotoPerfil.isNotEmpty()) {
                val bitmap = BitmapFactory.decodeFile(rutaFotoPerfil)
                if (bitmap != null) {
                    Image(bitmap = bitmap.asImageBitmap(), contentDescription = "Foto de perfil", contentScale = ContentScale.Crop, modifier = Modifier.size(100.dp).clip(CircleShape))
                } else {
                    Image(painter = painterResource(id = R.drawable.gen_personaicon), contentDescription = "Foto genérica", modifier = Modifier.size(100.dp))
                }
            } else {
                Image(painter = painterResource(id = R.drawable.gen_personaicon), contentDescription = "Foto de perfil", modifier = Modifier.size(100.dp))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("Cambiar foto", color = Color(0xFF3C733F), fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.clickable { selectorDeImagen.launch("image/*") })

        Spacer(modifier = Modifier.height(32.dp))

        Text("Nombres", modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold, color = Color.Black)
        OutlinedTextField(
            value = nombres, onValueChange = { nombres = it }, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp), placeholder = { Text("Ej: Maria", color = Color.Gray) },
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Apellidos", modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold, color = Color.Black)
        OutlinedTextField(
            value = apellidos, onValueChange = { apellidos = it }, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp), placeholder = { Text("Ej: Rodriguez", color = Color.Gray) },
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Teléfono", modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold, color = Color.Black)
        OutlinedTextField(
            value = telefono, onValueChange = { if (it.all { char -> char.isDigit() }) telefono = it }, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp), placeholder = { Text("Ej: 3100000000", color = Color.Gray) },
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
        )

        Spacer(modifier = Modifier.weight(1f))

        BotonPrincipalVerde(
            textoDelBoton = if (cargando) "Guardando..." else "Guardar cambios",
            alHacerClic = {
                if (userId.isNotEmpty()) {
                    cargando = true
                    val actualizaciones = mapOf("nombres" to nombres, "apellidos" to apellidos, "telefono" to telefono)
                    db.collection("usuarios").document(userId).set(actualizaciones, SetOptions.merge())
                        .addOnCompleteListener { task ->
                            cargando = false
                            if(task.isSuccessful) {
                                Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                                onNavegarAtras()
                            } else {
                                Toast.makeText(context, "Error al guardar", Toast.LENGTH_LONG).show()
                            }
                        }
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}