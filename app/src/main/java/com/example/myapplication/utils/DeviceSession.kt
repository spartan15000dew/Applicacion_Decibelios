// utils/DeviceSession.kt
package com.example.myapplication.utils

import android.content.Context

object DeviceSession {
    private const val PREFS_NAME = "app_prefs"
    private const val KEY_DEVICE_ID = "current_device_id"
    private const val KEY_DEVICE_NAME = "current_device_name"

    // Variable en memoria para acceso rápido
    var currentDeviceId: String = ""
    var currentDeviceName: String = ""

    fun saveDevice(context: Context, id: String, name: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_DEVICE_ID, id).putString(KEY_DEVICE_NAME, name).apply()
        currentDeviceId = id
        currentDeviceName = name
    }

    fun loadDevice(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        currentDeviceId = prefs.getString(KEY_DEVICE_ID, "") ?: ""
        currentDeviceName = prefs.getString(KEY_DEVICE_NAME, "") ?: ""
        return currentDeviceId.isNotEmpty()
    }

    fun clearSession(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
        currentDeviceId = ""
        currentDeviceName = ""
    }
}