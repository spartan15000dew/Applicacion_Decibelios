package com.example.myapplication.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun MenuScreen(navController: NavHostController) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Panel de Control", fontSize = 28.sp, color = MaterialTheme.colorScheme.primary)

            Spacer(modifier = Modifier.height(40.dp))

            // Botón ir al Monitor
            Button(
                onClick = { navController.navigate("monitor") },
                modifier = Modifier.fillMaxWidth(0.7f).height(50.dp)
            ) {
                Text("Ver Monitor")
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botón Configurar LEDs
            Button(
                onClick = { navController.navigate("config_leds") },
                modifier = Modifier.fillMaxWidth(0.7f).height(50.dp)
            ) {
                Text("Configurar LEDs")
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botón Configurar Horarios
            Button(
                onClick = { navController.navigate("config_horarios") },
                modifier = Modifier.fillMaxWidth(0.7f).height(50.dp)
            ) {
                Text("Configurar Horarios")
            }
        }
    }
}