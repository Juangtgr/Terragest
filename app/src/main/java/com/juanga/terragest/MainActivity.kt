package com.juanga.terragest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.juanga.terragest.ui.theme.TerragestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // ¡Corregido! Ahora llamamos directamente al tema
            TerragestTheme {
                AppNavigation()
            }
        }
    }
}