package com.example.biblioverso.Models

import com.google.gson.annotations.SerializedName

data class Opinion(
    @SerializedName("id_libro")
    val idLibro: Int,
    @SerializedName("ci_cliente")
    val ciCliente: String,
    val calificacion: Int = 0,
    val comentario: String = ""
)