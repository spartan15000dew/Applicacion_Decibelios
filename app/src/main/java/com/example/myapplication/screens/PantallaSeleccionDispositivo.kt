package com.example.myapplication.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack // <--- IMPORTANTE
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapplication.utils.DeviceSession
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database


data class DeviceInfo(val id: String = "", val name: String = "", val password: String = "")

@Composable
fun PantallaSeleccionDispositivo(navController: NavHostController) {
    val context = LocalContext.current
    var devicesList by remember { mutableStateOf(listOf<DeviceInfo>()) }
    var showDialog by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {

        if (DeviceSession.loadDevice(context)) {
        }

        val db = Firebase.database.getReference("devices_meta")
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<DeviceInfo>()
                for (child in snapshot.children) {
                    val dev = child.getValue(DeviceInfo::class.java)
                    if (dev != null) list.add(dev)
                }
                devicesList = list
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo Dispositivo")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // --- NUEVA CABECERA CON BOTÓN DE RETROCESO ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Atrás"
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Seleccionar Dispositivo",
                    fontSize = 22.sp, // Ajusté un poco el tamaño para que quepa mejor
                    fontWeight = FontWeight.Bold
                )
            }
            // ---------------------------------------------

            Text("Elige el dispositivo principal o secundario a controlar", color = Color.Gray)

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(devicesList) { device ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable {
                            loginToDevice(context, device, navController)
                        },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Computer, contentDescription = null)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(device.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        DialogAddDevice(
            onDismiss = { showDialog = false },
            onConfirm = { name, pass ->
                val id = System.currentTimeMillis().toString()
                val newDevice = DeviceInfo(id, name, pass)
                Firebase.database.getReference("devices_meta/$id").setValue(newDevice)
                showDialog = false
            }
        )
    }
}

// Función auxiliar para pedir contraseña (sin cambios)
fun loginToDevice(context: android.content.Context, device: DeviceInfo, navController: NavHostController) {
    DeviceSession.saveDevice(context, device.id, device.name)
    Toast.makeText(context, "Conectado a ${device.name}", Toast.LENGTH_SHORT).show()
    navController.navigate("menu")
}

@Composable
fun DialogAddDevice(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Dispositivo") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = pass,
                    onValueChange = { pass = it },
                    label = { Text("Contraseña interna") },
                    visualTransformation = PasswordVisualTransformation()
                )
            }
        },
        confirmButton = {
            Button(onClick = { if(name.isNotEmpty() && pass.isNotEmpty()) onConfirm(name, pass) }) {
                Text("Crear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}