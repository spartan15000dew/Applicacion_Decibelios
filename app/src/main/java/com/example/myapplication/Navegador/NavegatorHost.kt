package com.example.myapplication

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.screens.ConfigLedsScreen
import com.example.myapplication.screens.MenuScreen
import com.example.myapplication.screens.PantallaConfigHorarios
import com.example.myapplication.screens.PantallaMonitor
import com.example.myapplication.screens.PantallaHistorial
import com.example.myapplication.screens.PantallaDetector
import com.example.myapplication.screens.PantallaConfigAlertas
import com.example.myapplication.screens.PantallaZonasAlertas

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

        // Pantalla del Monitor (Monitor original)
        composable("monitor") {
            PantallaMonitor(navController)
        }

        // --- Nuevas Pantallas Agregadas ---

        // Pantalla de Historial de Registros
        composable("historial") {
            PantallaHistorial(navController)
        }

        // Pantalla de Detector en Vivo (Live View)
        composable("detector_live") {
            PantallaDetector(navController)
        }

        // Pantalla de Configuración de Alertas
        composable("config_alertas") {
            PantallaConfigAlertas(navController)
        }

        // Pantalla de Zonas de Alerta
        composable("zonas_alertas") {
            PantallaZonasAlertas(navController)
        }
    }
}