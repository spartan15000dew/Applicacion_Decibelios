package com.example.myapplication.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapplication.models.LedConfig
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

@Composable
fun ConfigLedsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val dbPath = "configuracion_leds"

    var localConfig by remember { mutableStateOf(LedConfig()) }
    var isLoading by remember { mutableStateOf(true) }

    DisposableEffect(Unit) {
        val database = Firebase.database
        val myRef = database.getReference(dbPath)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.getValue(LedConfig::class.java)
                if (value != null) localConfig = value
                isLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error leyendo datos", error.toException())
                isLoading = false
            }
        }
        myRef.addValueEventListener(listener)
        onDispose { myRef.removeEventListener(listener) }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    label = { Text("Menu") },
                    selected = false,
                    onClick = { navController.navigate("menu") }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Configuración LEDs", fontSize = 24.sp, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn {
                    item {
                        SectionTitle("Frecuencia de Parpadeo (Hz)")
                        SliderControl("Verde", localConfig.freqVerde, 1f, 10f, Color.Green) {
                            localConfig = localConfig.copy(freqVerde = it)
                        }
                        SliderControl("Amarillo", localConfig.freqAmarillo, 1f, 10f, Color.Yellow) {
                            localConfig = localConfig.copy(freqAmarillo = it)
                        }
                        SliderControl("Rojo", localConfig.freqRojo, 1f, 10f, Color.Red) {
                            localConfig = localConfig.copy(freqRojo = it)
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        SectionTitle("Umbrales de Decibelios (dB)")
                        SliderControl("Inicio Riesgo Bajo", localConfig.umbralVerde, 0f, 120f, Color.Green) {
                            localConfig = localConfig.copy(umbralVerde = it)
                        }
                        SliderControl("Inicio Riesgo Medio", localConfig.umbralAmarillo, 0f, 120f, Color.Yellow) {
                            localConfig = localConfig.copy(umbralAmarillo = it)
                        }
                        SliderControl("Inicio Riesgo Alto", localConfig.umbralRojo, 0f, 120f, Color.Red) {
                            localConfig = localConfig.copy(umbralRojo = it)
                        }

                        Spacer(modifier = Modifier.height(30.dp))

                        Button(
                            onClick = {
                                val database = Firebase.database
                                val myRef = database.getReference(dbPath)
                                myRef.setValue(localConfig)
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Guardado exitoso", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show()
                                    }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Guardar Cambios")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp),
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun SliderControl(
    label: String,
    value: Float,
    min: Float,
    max: Float,
    color: Color,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
            Text(text = String.format("%.1f", value), style = MaterialTheme.typography.bodyMedium)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = min..max,
            colors = SliderDefaults.colors(thumbColor = color, activeTrackColor = color)
        )
    }
}