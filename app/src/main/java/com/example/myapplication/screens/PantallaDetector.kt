package com.example.myapplication.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons // Import Agregado
import androidx.compose.material.icons.filled.ArrowBack // Import Agregado
import androidx.compose.material3.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapplication.models.SimulacionData
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

@Composable
fun PantallaDetector(navController: NavHostController) {
    // Usamos la misma ruta que tu monitor principal para que sea real
    val dbPath = "simulacion"

    var currentDb by remember { mutableStateOf(0) }
    // Podríamos guardar stats locales
    var minDb by remember { mutableStateOf(100) }
    var maxDb by remember { mutableStateOf(0) }

    DisposableEffect(Unit) {
        val database = Firebase.database
        val myRef = database.getReference(dbPath)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue(SimulacionData::class.java)
                if (data != null) {
                    currentDb = data.decibelios
                    // Cálculos simples locales
                    if (currentDb < minDb && currentDb > 0) minDb = currentDb
                    if (currentDb > maxDb) maxDb = currentDb
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        myRef.addValueEventListener(listener)
        onDispose { myRef.removeEventListener(listener) }
    }

    val maxLimit = 120f

    Scaffold(containerColor = Color(0xFF12141C)) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- CABECERA MODIFICADA ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Botón Atrás
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }

                // 2. Título
                Text(
                    text = "Live View",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                // 3. Espaciador para empujar "Online" a la derecha
                Spacer(modifier = Modifier.weight(1f))

                // 4. Estado
                Text("● Online", color = Color.Green, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Indicador Circular
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(250.dp)) {
                CircularProgressIndicator(
                    progress = 1f,
                    modifier = Modifier.fillMaxSize(),
                    color = Color.DarkGray.copy(alpha = 0.3f),
                    strokeWidth = 20.dp
                )
                CircularProgressIndicator(
                    progress = (currentDb / maxLimit).coerceIn(0f, 1f),
                    modifier = Modifier.fillMaxSize(),
                    color = if(currentDb > 80) Color(0xFFFF5722) else Color(0xFF4CAF50),
                    strokeWidth = 20.dp,
                    strokeCap = StrokeCap.Round
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("CURRENT LEVEL", color = Color.Gray, fontSize = 12.sp)
                    Text("$currentDb dB", color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Stats
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(Modifier.weight(1f), "MIN", "$minDb dB")
                StatCard(Modifier.weight(1f), "MAX", "$maxDb dB")
            }
            Spacer(modifier = Modifier.height(12.dp))

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { /* Lógica Stop */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("STOP DETECTION", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, title: String, value: String) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2129)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = Color.Gray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}