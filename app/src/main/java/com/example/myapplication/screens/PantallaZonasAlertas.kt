package com.example.myapplication.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons // Import Agregado
import androidx.compose.material.icons.filled.ArrowBack // Import Agregado
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapplication.models.AlertZone
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

@Composable
fun PantallaZonasAlertas(navController: NavHostController) {
    val context = LocalContext.current
    val dbPath = "configuracion_zonas"

    var zonas by remember { mutableStateOf(listOf<AlertZone>()) }
    var isLoading by remember { mutableStateOf(true) }

    // Leer lista de zonas desde Firebase
    DisposableEffect(Unit) {
        val database = Firebase.database
        val myRef = database.getReference(dbPath)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listaTemp = mutableListOf<AlertZone>()
                // Recorrer los hijos del nodo
                for (child in snapshot.children) {
                    val zona = child.getValue(AlertZone::class.java)
                    if (zona != null) {
                        listaTemp.add(zona)
                    }
                }
                zonas = listaTemp
                isLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                isLoading = false
            }
        }
        myRef.addValueEventListener(listener)
        onDispose { myRef.removeEventListener(listener) }
    }

    Scaffold(containerColor = Color(0xFF12141C)) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {

            // --- CABECERA MODIFICADA ---
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Botón Atrás
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Atrás",
                        tint = Color.White
                    )
                }

                // 2. Título
                Text(
                    text = "Alert Zones",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                // 3. Espaciador para empujar el botón + a la derecha
                Spacer(modifier = Modifier.weight(1f))

                // 4. Botón para resetear zonas (demo de escritura)
                IconButton(onClick = {
                    createDefaultZones(dbPath, context)
                }) {
                    Text("+", color = Color.White, fontSize = 24.sp)
                }
            }

            Text("Tap a zone to edit or '+' to reset defaults.", color = Color.Gray, modifier = Modifier.padding(bottom = 16.dp))

            // Barra de colores
            Row(modifier = Modifier.fillMaxWidth().height(20.dp).clip(RoundedCornerShape(4.dp))) {
                Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color(0xFF4CAF50)))
                Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color(0xFFFFC107)))
                Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color(0xFFFF5722)))
                Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color(0xFFF44336)))
            }
            Spacer(modifier = Modifier.height(20.dp))

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(zonas) { zona ->
                        ZoneItem(zona)
                    }
                }
            }
        }
    }
}

// Función auxiliar para crear datos de prueba si la DB está vacía
fun createDefaultZones(path: String, context: android.content.Context) {
    val database = Firebase.database
    val ref = database.getReference(path)
    val defaultZones = listOf(
        AlertZone("1", "Quiet", 0, 50, "No Alert"),
        AlertZone("2", "Moderate", 51, 80, "Subtle Vibration"),
        AlertZone("3", "Loud", 81, 110, "Strong Vibration"),
        AlertZone("4", "Harmful", 111, 140, "Push Notification")
    )
    ref.setValue(defaultZones).addOnSuccessListener {
        Toast.makeText(context, "Zonas por defecto creadas", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun ZoneItem(zona: AlertZone) {
    val color = when {
        zona.rangeMax <= 50 -> Color(0xFF4CAF50)
        zona.rangeMax <= 80 -> Color(0xFFFFC107)
        zona.rangeMax <= 110 -> Color(0xFFFF5722)
        else -> Color(0xFFF44336)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2129)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).background(color, RoundedCornerShape(5.dp)))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(zona.label, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("${zona.rangeMin} - ${zona.rangeMax} dB", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(zona.action, color = Color.Gray, fontSize = 12.sp)
            }
            Button(
                onClick = { /* Lógica de Editar futura */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Edit")
            }
        }
    }
}