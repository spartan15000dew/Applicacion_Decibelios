package screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun MenuPrincipalScreen(navController: NavHostController) {
    // Leemos la simulación para mostrar el estado rápido
    val (simulacion, _, _) = LeerFirebase("simulacion", SimulacionData::class.java)
    val dbValue = simulacion?.decibelios ?: 0

    Scaffold(bottomBar = { BottomBar(navController) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Menú Principal", fontSize = 28.sp, color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(20.dp))

            // Tarjeta de resumen rápido
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Estado Actual", color = Color.Gray)
                        Text(
                            text = if (dbValue < 60) "Seguro" else "Alto",
                            color = if (dbValue < 60) Color.Green else Color.Red,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text("$dbValue dB", fontSize = 24.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            // Botones del menú usando el componente reutilizable MenuItem
            MenuItem(
                icon = Icons.Default.ShowChart,
                title = "Monitor",
                desc = "Ver gráfica y niveles"
            ) { navController.navigate("monitor") }

            MenuItem(
                icon = Icons.Default.Settings,
                title = "Configurar LEDs",
                desc = "Ajustar alertas visuales"
            ) { navController.navigate("config_leds") }

            MenuItem(
                icon = Icons.Default.DateRange,
                title = "Horarios",
                desc = "Programar monitoreo"
            ) { navController.navigate("config_horarios") }
        }
    }
}