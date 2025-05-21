package com.example.biblioverso.Models

import com.google.gson.annotations.SerializedName

data class Persona(
    @SerializedName("id_persona")
    var idPersona: Int? = null,
    var nombre: String,
    var apellido: String,
    var email: String,
    var telefono: String? = "",
    var direccion: String? = "",
    var genero: String = "M"
)

data class Cliente(
    @SerializedName("ci_cliente")
    var ciCliente: String,
    @SerializedName("id_persona")
    var idPersona: Int? = null,
    var usuario: String,
    var password: String
)
