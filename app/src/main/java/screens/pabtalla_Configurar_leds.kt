package screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun ConfigLedsScreen(navController: NavHostController) {
    val (config, loading, _) = LeerFirebase("configuracion_leds", LedConfig::class.java)
    var localConfig by remember { mutableStateOf(LedConfig()) }

    // Cargar datos cuando lleguen
    LaunchedEffect(config) { if (config != null) localConfig = config }

    Scaffold(bottomBar = { BottomBar(navController) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Configuración LEDs", fontSize = 24.sp, color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))

            if (loading) {
                CircularProgressIndicator(color = Color.White)
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
                            onClick = { escribirFirebase("configuracion_leds", localConfig) },
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