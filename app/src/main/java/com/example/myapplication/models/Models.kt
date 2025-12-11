package com.example.myapplication.models



data class LedConfig(
    var freqVerde: Float = 1f,
    var freqAmarillo: Float = 1f,
    var freqRojo: Float = 1f,
    var umbralVerde: Float = 50f,
    var umbralAmarillo: Float = 70f,
    var umbralRojo: Float = 90f
)


data class Horario(
    var inicio: String = "08:00",
    var fin: String = "18:00"
)


data class SimulacionData(
    var decibelios: Int = 0,
    var actividad: String = ""
)