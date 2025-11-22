

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MonitorScreen(navController: NavHostController) {
    val (simulacion, _, _) = LeerFirebase("simulacion", SimulacionData::class.java)
    val (horarioConfig, _, _) = LeerFirebase("horarios/principal", Horario::class.java)

    // Lógica de Horarios
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    val currentTime = sdf.format(Date())

    val dentroDeHorario = try {
        val start = horarioConfig?.inicio ?: "00:00"
        val end = horarioConfig?.fin ?: "23:59"
        currentTime >= start && currentTime <= end
    } catch (e: Exception) { false }

    val decibelios = if (dentroDeHorario) simulacion?.decibelios ?: 0 else 0
    val actividad = if (dentroDeHorario) simulacion?.actividad ?: "Monitor Inactivo" else "Fuera de Horario"

    val barColor = when {
        decibelios < 60 -> Color.Green
        decibelios < 85 -> Color.Yellow
        else -> Color.Red
    }

    Scaffold(bottomBar = { BottomBar(navController) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Monitor de Actividad", fontSize = 24.sp, color = Color.White)
            Text(actividad, fontSize = 18.sp, color = Color(0xFFF59E0B))
            Spacer(modifier = Modifier.height(40.dp))

            Text("$decibelios dB", fontSize = 60.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(20.dp))

            // Barra visual simple
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(300.dp)
                    .background(Color.DarkGray, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.BottomCenter
            ) {
                val heightPercent = (decibelios / 120f).coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(heightPercent)
                        .background(barColor, RoundedCornerShape(10.dp))
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            if (dentroDeHorario) {
                Text("Monitoreo Activo: ${horarioConfig?.inicio} - ${horarioConfig?.fin}", color = Color.Gray)
            } else {
                Text("Monitoreo Pausado (Horario: ${horarioConfig?.inicio} - ${horarioConfig?.fin})", color = Color.Red)
            }
        }
    }
}