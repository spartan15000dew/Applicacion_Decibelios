import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.awaitCancellation

@Composable
fun MenuPrincipalScreen(navController: NavHostController) {
    // Lectura interna (Data Class SimulacionDataMenu abajo)
    val (simulacion, _, _) = LeerFirebaseMenu("simulacion", SimulacionDataMenu::class.java)
    val dbValue = simulacion?.decibelios ?: 0

    Scaffold(bottomBar = { BottomBar(navController) }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().background(Color(0xFF111827)).padding(padding).padding(16.dp)
        ) {
            Text("Menú Principal", fontSize = 28.sp, color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(20.dp))

            // Tarjeta Estado
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937)),
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Row(modifier = Modifier.padding(20.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("Estado Actual", color = Color.Gray)
                        Text(if (dbValue < 60) "Seguro" else "Alto", color = if (dbValue < 60) Color.Green else Color.Red, fontWeight = FontWeight.Bold)
                    }
                    Text("$dbValue dB", fontSize = 24.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            MenuItem(Icons.Default.ShowChart, "Monitor", "Ver gráfica") { navController.navigate("monitor") }
            MenuItem(Icons.Default.Settings, "Configurar LEDs", "Ajustar alertas") { navController.navigate("config_leds") }
            MenuItem(Icons.Default.DateRange, "Horarios", "Programar") { navController.navigate("config_horarios") }
        }
    }
}

@Composable
fun MenuItem(icon: ImageVector, title: String, desc: String, onClick: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937)), modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { onClick() }) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(desc, color = Color.Gray, fontSize = 14.sp)
            }
        }
    }
}

// MODELO Y FIREBASE INTERNO (SOLO PARA MENU)
data class SimulacionDataMenu(val decibelios: Int = 0, val actividad: String = "") { constructor() : this(0, "") }

@Composable
fun <T> LeerFirebaseMenu(field: String, valueType: Class<T>): Triple<T?, Boolean, String?> {
    var currentValue by rememberSaveable { mutableStateOf<T?>(null) }
    var isLoading by rememberSaveable { mutableStateOf(true) }
    val database = Firebase.database
    val myRef = database.getReference(field)

    LaunchedEffect(field) {
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) { currentValue = snapshot.getValue(valueType); isLoading = false }
            override fun onCancelled(error: DatabaseError) { isLoading = false }
        })
        awaitCancellation()
    }
    return Triple(currentValue, isLoading, null)
}