package com.example.myapplication

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.screens.ConfigLedsScreen
import com.example.myapplication.screens.MenuScreen
import com.example.myapplication.screens.PantallaConfigHorarios
import com.example.myapplication.screens.PantallaMonitor

@Composable
fun NavegacionApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "menu") {

        // Pantalla de Menú Principal
        composable("menu") {
            MenuScreen(navController)
        }

        // Pantalla de Configuración de LEDs
        composable("config_leds") {
            ConfigLedsScreen(navController)
        }

        // Pantalla de Configuración de Horarios
        composable("config_horarios") {
            PantallaConfigHorarios(navController)
        }

        // Pantalla del Monitor
        composable("monitor") {
            PantallaMonitor(navController)
        }
    }
}