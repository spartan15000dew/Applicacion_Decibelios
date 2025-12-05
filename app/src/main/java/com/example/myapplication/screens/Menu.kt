package com.example.myapplication

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// Importamos las pantallas desde el paquete 'screens'
import com.example.myapplication.screens.menu
import com.example.myapplication.screens.ConfigLedsScreen  // Nombre actualizado


@Composable
fun NavegacionApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "menu") {

        composable("menu") {
            Menu(navController)
        }

        composable("config_leds") {
            // Usamos el nombre del componente actualizado
            ConfigLedsScreen(navController)
        }

        composable("config_horarios") {
            PantallaConfigHorarios(navController)
        }

        composable("monitor") {
            PantallaMonitor(navController)
        }
    }
}