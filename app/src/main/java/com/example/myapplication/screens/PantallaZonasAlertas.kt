// PantallaZonasAlertas.kt
package com.example.myapplication.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapplication.models.SimulacionData
import com.example.myapplication.utils.DeviceSession
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

data class DeviceStatus(val id: String, val name: String, val db: Int)

@Composable
fun PantallaZonasAlertas(navController: NavHostController) {
    val currentId = DeviceSession.currentDeviceId

    // Lista de estados de OTROS dispositivos
    var otherDevicesStatus by remember { mutableStateOf(listOf<DeviceStatus>()) }

    DisposableEffect(Unit) {
        val db = Firebase.database

        // Escuchar cambios en TODOS los dispositivos para llenar la lista
        val ref = db.getReference("devices_data") // Nueva raíz para datos dinámicos

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<DeviceStatus>()

                // Recorremos todos los IDs de dispositivos
                for (deviceSnapshot in snapshot.children) {
                    val devId = deviceSnapshot.key ?: continue

                    // Saltamos nuestro propio dispositivo
                    if (devId == currentId) continue

                    // Obtenemos nombre (suponiendo que guardamos el nombre dentro de data también o cruzamos datos)
                    // Para simplificar, obtenemos los decibelios directos de la simulación
                    val dbVal = deviceSnapshot.child("simulacion/decibelios").getValue(Int::class.java) ?: 0
                    // Nombre dummy o recuperado de metadatos si hiciéramos join
                    val name = "Dispositivo $devId"

                    list.add(DeviceStatus(devId, name, dbVal))
                }
                otherDevicesStatus = list
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        ref.addValueEventListener(listener)
        onDispose { ref.removeEventListener(listener) }
    }

    Scaffold(containerColor = Color(0xFF12141C)) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            // Header con botón atrás
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color.White)
            }

            Text("Monitor Multi-Dispositivo", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(20.dp))

            Text("Otros Dispositivos en la Red", color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(10.dp))

            if (otherDevicesStatus.isEmpty()) {
                Text("No hay otros dispositivos transmitiendo.", color = Color.Gray)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(otherDevicesStatus) { dev ->
                        OtherDeviceCard(dev)
                    }
                }
            }
        }
    }
}

@Composable
fun OtherDeviceCard(device: DeviceStatus) {
    val color = if(device.db > 80) Color.Red else Color.Green
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2129))) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(device.name, color = Color.White, fontWeight = FontWeight.Bold)
                Text("ID: ${device.id.take(4)}...", color = Color.Gray, fontSize = 12.sp)
            }
            Text("${device.db} dB", color = color, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}