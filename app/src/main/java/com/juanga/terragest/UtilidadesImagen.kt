package com.juanga.terragest

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

fun guardarImagenLocalmente(context: Context, uri: Uri): String? {
    return try {
        // Abrimos la imagen original que el usuario seleccionó
        val inputStream = context.contentResolver.openInputStream(uri)

        // Creamos un nombre único basado en la hora actual
        val nombreArchivo = "terragest_img_${System.currentTimeMillis()}.jpg"

        // Creamos el nuevo archivo en la carpeta privada de la app
        val archivoLocal = File(context.filesDir, nombreArchivo)
        val outputStream = FileOutputStream(archivoLocal)

        // Copiamos los datos
        inputStream?.copyTo(outputStream)

        // Cerramos los canales para no consumir memoria
        inputStream?.close()
        outputStream.close()

        // Retornamos la ruta final donde quedó guardada la foto
        archivoLocal.absolutePath
    } catch (e: Exception) {
        null
    }
}