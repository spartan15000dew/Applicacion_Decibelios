package com.example.myapplication.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapplication.models.Horario
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

@Composable
fun PantallaConfigHorarios(navController: NavHostController) {
    val context = LocalContext.current
    val dbPath = "horarios/principal"

    var horario by remember { mutableStateOf(Horario()) }
    var isLoading by remember { mutableStateOf(true) }

    // Leer configuración actual de Firebase
    DisposableEffect(Unit) {
        val database = Firebase.database
        val myRef = database.getReference(dbPath)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.getValue(Horario::class.java)
                if (value != null) {
                    horario = value
                }
                isLoading = false
            }
            override fun onCancelled(error: DatabaseError) {
                isLoading = false
            }
        }
        myRef.addValueEventListener(listener)
        onDispose { myRef.removeEventListener(listener) }
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
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Configuración de Horarios", fontSize = 24.sp, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(30.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Text("Hora de Inicio (HH:mm)", fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = horario.inicio,
                    onValueChange = { horario = horario.copy(inicio = it) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text("Hora de Fin (HH:mm)", fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = horario.fin,
                    onValueChange = { horario = horario.copy(fin = it) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = {
                        val database = Firebase.database
                        val myRef = database.getReference(dbPath)
                        myRef.setValue(horario)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Horario guardado", Toast.LENGTH_SHORT).show()
                            }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar Horario")
                }
            }
        }
    }
}