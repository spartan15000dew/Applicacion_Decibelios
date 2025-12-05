package com.example.myapplication.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.text.SimpleDateFormat
import java.util.*
import com.example.myapplication.models.SimulacionData
import com.example.myapplication.models.Horario

@Composable
fun PantallaMonitor(navController: NavHostController) {


    var simulacion by remember { mutableStateOf<SimulacionData?>(null) }
    var horarioConfig by remember { mutableStateOf<Horario?>(null) }


    DisposableEffect(Unit) {
        val db = Firebase.database
        val refSimulacion = db.getReference("simulacion")
        val refHorario = db.getReference("horarios/principal")


        val simListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue(SimulacionData::class.java)
                if (data != null) simulacion = data
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error simulación: ${error.message}")
            }
        }


        val horarioListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue(Horario::class.java)
                if (data != null) horarioConfig = data
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error horario: ${error.message}")
            }
        }

        refSimulacion.addValueEventListener(simListener)
        refHorario.addValueEventListener(horarioListener)

        onDispose {
            refSimulacion.removeEventListener(simListener)
            refHorario.removeEventListener(horarioListener)
        }
    }


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


    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    label = { Text("Menu") },
                    selected = false,
                    onClick = { navController.navigate("menu") }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Monitor de Actividad", fontSize = 24.sp, color = MaterialTheme.colorScheme.onBackground)

            // Texto de actividad con color dinámico
            Text(
                text = actividad,
                fontSize = 18.sp,
                color = if(dentroDeHorario) Color(0xFFF59E0B) else Color.Gray,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text("$decibelios dB", fontSize = 60.sp, fontWeight = FontWeight.Bold, color = barColor)
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
                Text(
                    "Monitoreo Activo: ${horarioConfig?.inicio ?: "--:--"} - ${horarioConfig?.fin ?: "--:--"}",
                    color = Color.Gray
                )
            } else {
                Text(
                    "Monitoreo Pausado (Horario: ${horarioConfig?.inicio ?: "--:--"} - ${horarioConfig?.fin ?: "--:--"})",
                    color = Color.Red
                )
            }
        }
    }
}