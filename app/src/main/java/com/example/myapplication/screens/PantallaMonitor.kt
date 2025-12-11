package com.example.myapplication.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.myapplication.models.Horario
import com.example.myapplication.models.SimulacionData
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PantallaMonitor(navController: NavHostController) {

    // Estados
    var simulacion by remember { mutableStateOf<SimulacionData?>(null) }
    var horarioConfig by remember { mutableStateOf<Horario?>(null) }
    // Nuevo estado para guardar el mapa de horas -> decibelios máximos
    var historialMap by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }

    DisposableEffect(Unit) {
        val db = Firebase.database
        val refSimulacion = db.getReference("simulacion")
        val refHorario = db.getReference("horarios/principal")
        val refHistorial = db.getReference("historial_hoy") // Nueva referencia

        // Listener Simulación (Tiempo Real)
        val simListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue(SimulacionData::class.java)
                if (data != null) simulacion = data
            }
            override fun onCancelled(error: DatabaseError) {}
        }

        // Listener Horario
        val horarioListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue(Horario::class.java)
                if (data != null) horarioConfig = data
            }
            override fun onCancelled(error: DatabaseError) {}
        }

        // Listener Historial (Barras por hora)
        val historialListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val mapaTemp = mutableMapOf<String, Int>()
                for (child in snapshot.children) {
                    // La clave es la hora (ej: "08"), el valor son los dB (ej: 90)
                    val hora = child.key ?: ""
                    val dbValue = child.getValue(Int::class.java) ?: 0
                    mapaTemp[hora] = dbValue
                }
                historialMap = mapaTemp
            }
            override fun onCancelled(error: DatabaseError) {}
        }

        refSimulacion.addValueEventListener(simListener)
        refHorario.addValueEventListener(horarioListener)
        refHistorial.addValueEventListener(historialListener)

        onDispose {
            refSimulacion.removeEventListener(simListener)
            refHorario.removeEventListener(horarioListener)
            refHistorial.removeEventListener(historialListener)
        }
    }

    // Cálculos de hora actual y rangos
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    val currentTime = sdf.format(Date())

    // Solo la hora actual (ej: "15") para resaltar la barra actual
    val currentHourOnly = SimpleDateFormat("HH", Locale.getDefault()).format(Date())

    val dentroDeHorario = try {
        val start = horarioConfig?.inicio ?: "00:00"
        val end = horarioConfig?.fin ?: "23:59"
        currentTime >= start && currentTime <= end
    } catch (e: Exception) { false }

    val decibeliosActuales = if (dentroDeHorario) simulacion?.decibelios ?: 0 else 0
    val actividad = if (dentroDeHorario) simulacion?.actividad ?: "Inactivo" else "Fuera de Horario"

    // Color dinámico para el valor actual
    val mainColor = getColorForDb(decibeliosActuales)

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
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Monitor en Vivo", fontSize = 22.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(20.dp))

            // --- CÍRCULO CENTRAL (Valor Actual) ---
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(180.dp)
                    .background(mainColor.copy(alpha = 0.2f), RoundedCornerShape(100))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$decibeliosActuales",
                        fontSize = 60.sp,
                        fontWeight = FontWeight.Bold,
                        color = mainColor
                    )
                    Text("dB", fontSize = 20.sp, color = Color.Gray)
                }
            }

            Text(
                text = actividad,
                fontSize = 16.sp,
                color = if (dentroDeHorario) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier.padding(top = 10.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))
            Divider()
            Spacer(modifier = Modifier.height(20.dp))

            // --- SECCIÓN HISTORIAL POR HORA ---
            Text(
                "Pico Máximo por Hora",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Generamos la lista de horas basada en el horario configurado
            val listaHoras = generarRangoHoras(horarioConfig?.inicio, horarioConfig?.fin)

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth().height(150.dp)
            ) {
                items(listaHoras) { horaStr ->
                    // Obtenemos el valor máximo registrado para esta hora (o 0 si no hay dato)
                    val maxDb = historialMap[horaStr] ?: 0
                    val esHoraActual = (horaStr == currentHourOnly)

                    BarraHistorial(hora = horaStr, db = maxDb, isCurrent = esHoraActual)
                }
            }
        }
    }
}

// --- Componentes Auxiliares ---

@Composable
fun BarraHistorial(hora: String, db: Int, isCurrent: Boolean) {
    val barColor = getColorForDb(db)
    // Altura máxima de la barra (ej: 120dp). Calculamos porcentaje basado en 120dB máx.
    val porcentaje = (db / 120f).coerceIn(0.1f, 1f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.fillMaxHeight()
    ) {
        // Valor numérico arriba de la barra
        Text(text = "$db", fontSize = 12.sp, color = Color.Gray)

        // La Barra
        Box(
            modifier = Modifier
                .width(20.dp) // Ancho de la barra
                .fillMaxHeight(porcentaje) // Altura dinámica
                .background(
                    color = if (db == 0) Color.LightGray else barColor,
                    shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                )
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Etiqueta de la hora (ej: "09:00")
        Text(
            text = "$hora:00",
            fontSize = 12.sp,
            fontWeight = if(isCurrent) FontWeight.Bold else FontWeight.Normal,
            color = if(isCurrent) MaterialTheme.colorScheme.primary else Color.Gray
        )
    }
}

// Función auxiliar para colores
fun getColorForDb(db: Int): Color {
    return when {
        db == 0 -> Color.Gray
        db < 60 -> Color.Green
        db < 85 -> Color(0xFFFFC107) // Amber/Yellow
        else -> Color.Red
    }
}

// Función para generar lista de strings ["08", "09", "10"...]
fun generarRangoHoras(inicio: String?, fin: String?): List<String> {
    if (inicio == null || fin == null) return emptyList()

    val lista = mutableListOf<String>()
    try {
        val hInicio = inicio.split(":")[0].toInt()
        val hFin = fin.split(":")[0].toInt()

        if (hInicio <= hFin) {
            for (h in hInicio..hFin) {
                lista.add(h.toString().padStart(2, '0'))
            }
        } else {
            // Caso especial si cruza medianoche (ej: 22:00 a 02:00), simple por ahora
            for (h in hInicio..23) lista.add(h.toString().padStart(2, '0'))
            for (h in 0..hFin) lista.add(h.toString().padStart(2, '0'))
        }
    } catch (e: Exception) {
        return listOf("08", "09", "10", "11", "12") // Fallback
    }
    return lista
}