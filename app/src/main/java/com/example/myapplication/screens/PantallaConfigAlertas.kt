package com.example.myapplication.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapplication.models.AlertThreshold
import com.example.myapplication.models.AlertsConfig
import com.example.myapplication.utils.DeviceSession // IMPORTANTE: Importar tu utilitario de sesión
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

@Composable
fun PantallaConfigAlertas(navController: NavHostController) {
    val context = LocalContext.current

    // MODIFICACIÓN: Ruta dinámica basada en el ID del dispositivo
    val deviceId = DeviceSession.currentDeviceId
    val dbPath = "devices_data/$deviceId/configuracion_alertas"

    var alertsConfig by remember { mutableStateOf(AlertsConfig()) }
    var isLoading by remember { mutableStateOf(true) }

    DisposableEffect(Unit) {
        val database = Firebase.database
        val myRef = database.getReference(dbPath)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.getValue(AlertsConfig::class.java)
                if (value != null) {
                    alertsConfig = value
                }
                isLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error al cargar", Toast.LENGTH_SHORT).show()
                isLoading = false
            }
        }
        myRef.addValueEventListener(listener)
        onDispose { myRef.removeEventListener(listener) }
    }

    Scaffold(
        containerColor = Color(0xFF12141C)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = "Alertas",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    // Opcional: Mostrar en qué dispositivo estamos
                    Text(
                        text = "Dispositivo: $deviceId",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                TextButton(onClick = {
                    val database = Firebase.database
                    val myRef = database.getReference(dbPath)
                    myRef.setValue(alertsConfig)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Guardado", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show()
                        }
                }) {
                    Text("Guardar", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                Text("Configura los niveles de activación.", color = Color.Gray, modifier = Modifier.padding(bottom = 20.dp))

                AlertCard(
                    title = "Advertencia (Warning)",
                    subtitle = "Ruido potencialmente dañino",
                    color = Color(0xFFFFC107),
                    state = alertsConfig.warning,
                    onStateChange = { alertsConfig = alertsConfig.copy(warning = it) }
                )

                AlertCard(
                    title = "Crítico (Critical)",
                    subtitle = "Niveles peligrosos",
                    color = Color(0xFFEF4444),
                    state = alertsConfig.critical,
                    onStateChange = { alertsConfig = alertsConfig.copy(critical = it) }
                )

                AlertCard(
                    title = "Aviso (Notice)",
                    subtitle = "Informativo / Bajo riesgo",
                    color = Color(0xFF10B981),
                    state = alertsConfig.notice,
                    onStateChange = { alertsConfig = alertsConfig.copy(notice = it) }
                )
            }
        }
    }
}

// El componente AlertCard se mantiene igual, no necesita cambios.
@Composable
fun AlertCard(
    title: String,
    subtitle: String,
    color: Color,
    state: AlertThreshold,
    onStateChange: (AlertThreshold) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2129)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(16.dp).background(color, RoundedCornerShape(50)))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(title, color = Color.White, fontWeight = FontWeight.Bold)
                        Text(subtitle, color = Color.Gray, fontSize = 12.sp)
                    }
                }
                Switch(
                    checked = state.isEnabled,
                    onCheckedChange = { onStateChange(state.copy(isEnabled = it)) },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = color)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Nivel de Activación", color = Color.White)
                Text("${state.level.toInt()} dB", color = color, fontWeight = FontWeight.Bold)
            }
            Slider(
                value = state.level,
                onValueChange = { onStateChange(state.copy(level = it)) },
                valueRange = 0f..140f,
                colors = SliderDefaults.colors(thumbColor = color, activeTrackColor = color)
            )
        }
    }
}