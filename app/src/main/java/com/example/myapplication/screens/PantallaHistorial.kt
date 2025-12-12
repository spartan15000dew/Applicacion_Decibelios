package com.example.myapplication.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack // Import Agregado
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapplication.models.LogEntry
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PantallaHistorial(navController: NavHostController) {
    val dbPath = "historial_logs"
    var logs by remember { mutableStateOf(listOf<LogEntry>()) }
    var isLoading by remember { mutableStateOf(true) }

    DisposableEffect(Unit) {
        val database = Firebase.database
        val myRef = database.getReference(dbPath)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<LogEntry>()
                for (child in snapshot.children) {
                    val entry = child.getValue(LogEntry::class.java)
                    if (entry != null) list.add(0, entry) // Agregar al inicio para ver el más reciente
                }
                logs = list
                isLoading = false
            }
            override fun onCancelled(error: DatabaseError) { isLoading = false }
        }
        myRef.addValueEventListener(listener)
        // Opcional: Escribir datos dummy si está vacío para probar
        // if (logs.isEmpty()) createDummyLogs(myRef)
        onDispose { myRef.removeEventListener(listener) }
    }

    Scaffold(containerColor = Color(0xFF1A1C29)) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {

            // --- CABECERA CON BOTÓN ATRÁS ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                // 1. Botón Atrás
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.Gray
                    )
                }

                // 2. Título pequeño
                Text(
                    text = "Historial de Registros",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            // Título Principal
            Text(
                text = "Sound Level Log",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 12.dp) // Un pequeño ajuste para alinear visualmente
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Valor promedio simple de los logs cargados
            val avg = if (logs.isNotEmpty()) logs.map { it.db }.average().toInt() else 0
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("$avg dB (Avg)", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("Detailed Log", color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(logs) { log ->
                        LogItemRow(log)
                    }
                }
            }
        }
    }
}

@Composable
fun LogItemRow(log: LogEntry) {
    val color = when(log.type) {
        "danger" -> Color(0xFFEF4444)
        "warning" -> Color(0xFFF59E0B)
        else -> Color(0xFF10B981)
    }
    val icon = when(log.type) {
        "danger" -> Icons.Default.Warning
        "warning" -> Icons.Default.Notifications
        else -> Icons.Default.Info
    }

    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF252836)), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).background(color.copy(alpha=0.2f), RoundedCornerShape(20.dp)), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = color)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("${log.db} dB", color = Color.White, fontWeight = FontWeight.Bold)
                val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                Text(sdf.format(Date(log.timestamp)), color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}