package com.example.biblioverso.Controllers

import android.util.Log
import com.example.biblioverso.Models.Cliente
import com.example.biblioverso.Models.Persona
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import com.example.biblioverso.Utils.Constants.API_KEY
import com.example.biblioverso.Utils.Constants.BASE_URL

class ClienteController {
    private val client = HttpClient(Android)

    suspend fun crearCliente(persona: Persona, cliente: Cliente):Boolean {
        return try {
            val responsePersona: HttpResponse = client.post("${BASE_URL}persona") {
                contentType(ContentType.Application.Json)
                headers {
                    append("apikey", API_KEY)
                    append("Authorization", "Bearer $API_KEY")
                    append("Prefer", "return=representation")
                }
                setBody(Gson().toJson(persona))
            }

            val personaBody = responsePersona.bodyAsText()
            Log.e("CrearCliente", "Persona response: $personaBody")
            val personaCreada = Gson().fromJson(personaBody.trim().removeSurrounding("[", "]"), Persona::class.java)
            val idPersona = personaCreada.idPersona
            Log.e("idPersona", idPersona.toString())
            cliente.idPersona = idPersona
            Log.e("Cliente", cliente.toString())

            val response: HttpResponse = client.post("${BASE_URL}cliente") {
                contentType(ContentType.Application.Json)
                headers {
                    append("apikey", API_KEY)
                    append("Authorization", "Bearer $API_KEY")
                }
                setBody(Gson().toJson(cliente))
            }
            response.status.isSuccess()
        }
        catch (e: Exception) {
            Log.e("Crear Cliente", e.message.toString())
            false
        }
    }

    suspend fun iniciarSesion(user: String, pass: String): Cliente? {
        return try {
            val response: HttpResponse = client.get("${BASE_URL}cliente?usuario=eq.$user&password=eq.$pass") {
                headers {
                    append("apikey", API_KEY)
                    append("Authorization", "Bearer $API_KEY")
                }
            }
            if (response.status.isSuccess()) {
                val body = response.bodyAsText()
                val clientes: List<Cliente> = Gson().fromJson(body, Array<Cliente>::class.java).toList()
                clientes.firstOrNull()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
