package com.example.biblioverso.Data

import android.content.Context
import com.example.biblioverso.Models.Cliente
import com.google.gson.Gson

class PreferencesHelper(context: Context) {
    private val prefs = context.getSharedPreferences("biblioverso_prefs", Context.MODE_PRIVATE)

    fun guardarCliente(cliente: Cliente) {
        val clienteJson = Gson().toJson(cliente)
        prefs.edit().putString("cliente_json", clienteJson).apply()
    }

    fun obtenerCliente(): Cliente? {
        val json = prefs.getString("cliente_json", null)
        return json?.let { Gson().fromJson(it, Cliente::class.java) }
    }

    fun limpiarSesion() {
        prefs.edit().clear().apply()
    }
}