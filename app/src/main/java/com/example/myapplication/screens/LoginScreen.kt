package com.example.myapplication.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
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
import com.example.myapplication.models.User
import com.example.myapplication.utils.SecurityUtils
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

@Composable
fun LoginScreen(navController: NavHostController) {
    val context = LocalContext.current

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(containerColor = Color(0xFF12141C)) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Bienvenido", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(40.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Usuario") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(30.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        if (username.isNotEmpty() && password.isNotEmpty()) {
                            isLoading = true
                            val database = Firebase.database
                            // Buscamos directamente en el nodo del usuario
                            val myRef = database.getReference("users/$username")

                            myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val user = snapshot.getValue(User::class.java)

                                    if (user != null) {
                                        // 1. Hasheamos la contraseña que ingresó el usuario
                                        val inputHash = SecurityUtils.hashPassword(password)

                                        // 2. Comparamos con el hash guardado en Firebase
                                        if (inputHash == user.passwordHash) {
                                            Toast.makeText(context, "Bienvenido ${user.username}", Toast.LENGTH_SHORT).show()
                                            // Navegar al menú principal y borrar el historial de login
                                            navController.navigate("menu") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        } else {
                                            Toast.makeText(context, "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        Toast.makeText(context, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                                    }
                                    isLoading = false
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
                                    isLoading = false
                                }
                            })
                        } else {
                            Toast.makeText(context, "Ingrese usuario y contraseña", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Iniciar Sesión")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            TextButton(onClick = { navController.navigate("register") }) {
                Text("¿No tienes cuenta? Regístrate", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}