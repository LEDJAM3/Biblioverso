package com.example.biblioverso.Models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Libro(
    @SerializedName("id_libro")
    val idLibro: Int,
    val isbn: String,
    val titulo: String,
    val portada: String,
    val sinopsis: String,
    val genero: String,
    val editorial: String,
    @SerializedName("fecha_publicacion")
    val fechaPublicacion: String,
    val autor: List<Autor>
): Serializable

data class Autor(
    val nombre: String,
    @SerializedName("id_autor")
    val idAutor: Int,
    val biografia: String,
    @SerializedName("fecha_nac")
    val fechaNac: Int,
    @SerializedName("fecha_muerte")
    val fechaMuerte: Int?,
    val nacionalidad: String
): Serializable

data class Stock(
    @SerializedName("id_stock")
    val idStock: Int,
    @SerializedName("id_libro")
    val idLibro: Int,
    val ubicacion: String,
    val disponibilidad: Boolean,
    val estado: String
)