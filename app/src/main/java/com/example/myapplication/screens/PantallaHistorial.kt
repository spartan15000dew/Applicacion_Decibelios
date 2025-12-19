package com.example.myapplication.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
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
import com.example.myapplication.utils.DeviceSession // Importante para saber qué dispositivo es
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

// Modelo local para unir el dato del Arduino con la descripción del Usuario
data class HistorialItem(
    val hora: String,
    val db: Int,
    val descripcion: String = ""
)

@Composable
fun PantallaHistorial(navController: NavHostController) {
    val context = LocalContext.current
    val deviceId = DeviceSession.currentDeviceId
    val deviceName = DeviceSession.currentDeviceName

    // Rutas de Firebase
    val dbPathDatos = "devices_data/$deviceId/historial_hoy"
    val dbPathDesc = "devices_data/$deviceId/historial_descripciones"

    // Estados
    var historialLista by remember { mutableStateOf(listOf<HistorialItem>()) }
    var rawDatos by remember { mutableStateOf(mapOf<String, Int>()) }
    var rawDescripciones by remember { mutableStateOf(mapOf<String, String>()) }
    var isLoading by remember { mutableStateOf(true) }

    // Estados para el Diálogo de Descripción
    var showDialog by remember { mutableStateOf(false) }
    var selectedHour by remember { mutableStateOf("") }
    var tempDescription by remember { mutableStateOf("") }

    // Conexión a Firebase
    DisposableEffect(Unit) {
        val db = Firebase.database
        val refDatos = db.getReference(dbPathDatos)
        val refDesc = db.getReference(dbPathDesc)

        // 1. Escuchar los datos del Arduino (Horas y dB)
        val listenerDatos = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val mapaTemp = mutableMapOf<String, Int>()
                for (child in snapshot.children) {
                    val hora = child.key ?: ""
                    val valor = child.getValue(Int::class.java) ?: 0
                    mapaTemp[hora] = valor
                }
                rawDatos = mapaTemp
                isLoading = false
            }
            override fun onCancelled(error: DatabaseError) { isLoading = false }
        }

        // 2. Escuchar las descripciones del Usuario
        val listenerDesc = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val mapaTemp = mutableMapOf<String, String>()
                for (child in snapshot.children) {
                    val hora = child.key ?: ""
                    val texto = child.getValue(String::class.java) ?: ""
                    mapaTemp[hora] = texto
                }
                rawDescripciones = mapaTemp
            }
            override fun onCancelled(error: DatabaseError) {}
        }

        refDatos.addValueEventListener(listenerDatos)
        refDesc.addValueEventListener(listenerDesc)

        onDispose {
            refDatos.removeEventListener(listenerDatos)
            refDesc.removeEventListener(listenerDesc)
        }
    }

    // Unir los dos mapas (Datos + Descripciones) en una sola lista ordenada
    LaunchedEffect(rawDatos, rawDescripciones) {
        val listaUnida = rawDatos.map { (hora, db) ->
            HistorialItem(
                hora = hora,
                db = db,
                descripcion = rawDescripciones[hora] ?: ""
            )
        }.sortedByDescending { it.hora } // Ordenar por hora (la más reciente arriba)
        historialLista = listaUnida
    }

    Scaffold(containerColor = Color(0xFF1A1C29)) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {

            // --- CABECERA ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.Gray)
                }
                Column {
                    Text("Historial Diario", color = Color.Gray, fontSize = 14.sp)
                    Text(deviceName, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                }
            }

            Text(
                text = "Registro de Eventos",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 12.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Promedio del día
            val avg = if (historialLista.isNotEmpty()) historialLista.map { it.db }.average().toInt() else 0
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$avg dB", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Promedio Hoy", color = Color.Gray, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("Detalle por Hora (Toca para editar)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(10.dp))

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (historialLista.isEmpty()) {
                Text("No hay registros hoy todavía.", color = Color.Gray, modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(historialLista) { item ->
                        LogItemRow(item) {
                            // Al hacer click, abrir diálogo
                            selectedHour = item.hora
                            tempDescription = item.descripcion
                            showDialog = true
                        }
                    }
                }
            }
        }
    }

    // --- DIÁLOGO PARA AGREGAR DESCRIPCIÓN ---
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Agregar Nota") },
            text = {
                Column {
                    Text("Hora: $selectedHour:00", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = tempDescription,
                        onValueChange = { tempDescription = it },
                        label = { Text("Descripción (ej: Camión pasó)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    // Guardar solo la descripción en Firebase
                    val db = Firebase.database
                    db.getReference("devices_data/$deviceId/historial_descripciones/$selectedHour")
                        .setValue(tempDescription)
                    showDialog = false
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
fun LogItemRow(item: HistorialItem, onClick: () -> Unit) {
    val color = when {
        item.db >= 80 -> Color(0xFFEF4444) // Rojo
        item.db >= 60 -> Color(0xFFF59E0B) // Amarillo
        else -> Color(0xFF10B981)          // Verde
    }

    val icon = when {
        item.db >= 80 -> Icons.Default.Warning
        item.db >= 60 -> Icons.Default.Notifications
        else -> Icons.Default.Info
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF252836)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() } // Click para editar
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono Circular
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color.copy(alpha = 0.2f), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color)
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Datos Centrales
            Column(modifier = Modifier.weight(1f)) {
                Text("${item.db} dB Max", color = Color.White, fontWeight = FontWeight.Bold)
                Text("Hora: ${item.hora}:00 - ${item.hora}:59", color = Color.Gray, fontSize = 12.sp)

                // Mostrar descripción si existe
                if (item.descripcion.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "📝 ${item.descripcion}",
                        color = MaterialTheme.colorScheme.primary, // Color destacado
                        fontSize = 13.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }

            // Icono de edición pequeño
            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.DarkGray, modifier = Modifier.size(16.dp))
        }
    }
}