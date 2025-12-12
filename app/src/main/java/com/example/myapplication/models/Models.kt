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
data class AlertThreshold(
    var isEnabled: Boolean = false,
    var level: Float = 0f,
    var type: String = "" // Warning, Critical, Notice
)

data class AlertsConfig(
    var warning: AlertThreshold = AlertThreshold(true, 80f, "Warning"),
    var critical: AlertThreshold = AlertThreshold(false, 100f, "Critical"),
    var notice: AlertThreshold = AlertThreshold(false, 55f, "Notice")
)

data class AlertZone(
    var id: String = "",
    var label: String = "",
    var rangeMin: Int = 0,
    var rangeMax: Int = 0,
    var action: String = ""
)

data class LogEntry(
    var db: Int = 0,
    var timestamp: Long = 0,
    var type: String = "info"
)
data class User(
    var username: String = "",
    var email: String = "",
    var passwordHash: String = "" // Guardaremos el hash, no la contraseña real
)