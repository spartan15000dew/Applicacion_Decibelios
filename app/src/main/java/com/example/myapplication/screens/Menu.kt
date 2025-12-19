package com.example.myapplication.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun MenuScreen(navController: NavHostController) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()), // Permite scroll si hay muchos botones
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            Text("Panel de Control", fontSize = 28.sp, color = MaterialTheme.colorScheme.primary)

            Spacer(modifier = Modifier.height(30.dp))

            // --- VISTAS DE MONITORIZACIÓN ---

            // Botón ir al Monitor Principal
            Button(
                onClick = { navController.navigate("monitor") },
                modifier = Modifier.fillMaxWidth(0.7f).height(50.dp)
            ) {
                Text("Monitor General")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Detector Live View
            Button(
                onClick = { navController.navigate("detector_live") },
                modifier = Modifier.fillMaxWidth(0.7f).height(50.dp)
            ) {
                Text("Detector en Vivo")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Historial
            Button(
                onClick = { navController.navigate("historial") },
                modifier = Modifier.fillMaxWidth(0.7f).height(50.dp)
            ) {
                Text("Ver Historial")
            }

            Spacer(modifier = Modifier.height(30.dp))
            Divider(modifier = Modifier.fillMaxWidth(0.8f))
            Spacer(modifier = Modifier.height(30.dp))

            // --- CONFIGURACIONES ---

            // Botón Configurar LEDs
            Button(
                onClick = { navController.navigate("config_leds") },
                modifier = Modifier.fillMaxWidth(0.7f).height(50.dp)
            ) {
                Text("Configurar LEDs")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Configurar Horarios
            Button(
                onClick = { navController.navigate("config_horarios") },
                modifier = Modifier.fillMaxWidth(0.7f).height(50.dp)
            ) {
                Text("Configurar Horarios")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Configurar Alertas
            Button(
                onClick = { navController.navigate("config_alertas") },
                modifier = Modifier.fillMaxWidth(0.7f).height(50.dp)
            ) {
                Text("Configurar Alertas")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Zonas de Alerta
            Button(
                onClick = { navController.navigate("zonas_alertas") },
                modifier = Modifier.fillMaxWidth(0.7f).height(50.dp)
            ) {
                Text("Zonas de Alerta")
            }

            // --- SECCIÓN DE GESTIÓN DE DISPOSITIVO ---
            Spacer(modifier = Modifier.height(30.dp))
            Divider(modifier = Modifier.fillMaxWidth(0.8f))
            Spacer(modifier = Modifier.height(30.dp))

            Text("Gestión", fontSize = 20.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))

            // Botón Cambiar Dispositivo (NUEVO)
            Button(
                onClick = { navController.navigate("device_select") },
                modifier = Modifier.fillMaxWidth(0.7f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7)) // Un color morado para diferenciarlo
            ) {
                Text("Cambiar Dispositivo")
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}