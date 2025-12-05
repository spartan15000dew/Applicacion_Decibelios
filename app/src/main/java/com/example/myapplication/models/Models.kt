package com.example.myapplication.models


data class SimulacionData(
    val decibelios: Int = 0,
    val actividad: String = "Sin datos"
)

data class Horario(
    val inicio: String = "08:00",
    val fin: String = "18:00"
)

data class LedConfig(
    val estado: Boolean = false,
    val brillo: Int = 0,
    val color: String = "#FFFFFF"
)