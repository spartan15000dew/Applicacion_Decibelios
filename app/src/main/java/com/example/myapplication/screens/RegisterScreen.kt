package com.example.myapplication.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.google.firebase.database.database

@Composable
fun RegisterScreen(navController: NavHostController) {
    val context = LocalContext.current

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(containerColor = Color(0xFF12141C)) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Crear Cuenta", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(30.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Usuario") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
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
                            // Usamos el username como clave para simplificar la búsqueda
                            val myRef = database.getReference("users/$username")

                            // 1. HASHEAMOS LA CONTRASEÑA
                            val hash = SecurityUtils.hashPassword(password)

                            val newUser = User(username, email, hash)

                            myRef.setValue(newUser)
                                .addOnSuccessListener {
                                    isLoading = false
                                    Toast.makeText(context, "Registro Exitoso", Toast.LENGTH_SHORT).show()
                                    navController.navigate("login")
                                }
                                .addOnFailureListener {
                                    isLoading = false
                                    Toast.makeText(context, "Error al registrar", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(context, "Complete los campos", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Registrarse")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            TextButton(onClick = { navController.navigate("login") }) {
                Text("¿Ya tienes cuenta? Inicia Sesión", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}