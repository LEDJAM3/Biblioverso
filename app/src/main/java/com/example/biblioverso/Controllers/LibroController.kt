package com.example.biblioverso.Controllers

import android.util.Log
import com.example.biblioverso.Models.Libro
import com.example.biblioverso.Models.Opinion
import com.example.biblioverso.Utils.Constants.API_KEY
import com.example.biblioverso.Utils.Constants.BASE_URL
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess

class LibroController {
    private val client = HttpClient(Android)

    suspend fun ObtenerLibros(): List<Libro>? {
        return try {
            val response: HttpResponse = client.get("${BASE_URL}libro?select=id_libro,isbn,titulo,portada,sinopsis,genero,editorial,fecha_publicacion,autor(id_autor,nombre,biografia,fecha_nac,fecha_muerte,nacionalidad)") {
                headers {
                    append("apikey", API_KEY)
                    append("Authorization", "Bearer $API_KEY")
                }
            }
            if (response.status.isSuccess()) {
                val body = response.bodyAsText()
                val libros: List<Libro> = Gson().fromJson(body, Array<Libro>::class.java).toList()
                libros
            } else {
                null
            }
        }
        catch (e: Exception) {
            Log.e("Obtener Libros", e.message.toString())
            null
        }
    }

    suspend fun obtenerCalificacion(idLibro: Int): Float {
        return try {
            val response: HttpResponse = client.get("${BASE_URL}opiniones?id_libro=eq.$idLibro") {
                headers {
                    append("apikey", API_KEY)
                    append("Authorization", "Bearer $API_KEY")
                }
            }
            if (response.status.isSuccess()) {
                val body = response.bodyAsText()
                val opiniones = Gson().fromJson(body, Array<Opinion>::class.java).toList()
                if (opiniones.isNotEmpty()) {
                    val promedio = opiniones.sumOf { it.calificacion }.toDouble() / opiniones.size
                    String.format("%.1f", promedio).toFloat()
                } else {
                    0.0f
                }
            } else {
                0.0f
            }
        } catch (e: Exception) {
            Log.e("Obtener Calificaciones", e.message.toString())
            0.0f
        }
    }
}