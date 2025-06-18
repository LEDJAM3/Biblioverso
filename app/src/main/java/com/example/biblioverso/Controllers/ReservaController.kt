package com.example.biblioverso.Controllers

import android.util.Log
import com.example.biblioverso.Data.SessionManager.clienteActual
import com.example.biblioverso.Models.Libro
import com.example.biblioverso.Models.Reserva
import com.example.biblioverso.Models.ReservaAdap
import com.example.biblioverso.Models.Stock
import com.example.biblioverso.Utils.Constants.BASE_URL
import com.example.biblioverso.Utils.Constants.API_KEY
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import java.time.LocalDateTime

class ReservaController {
    private val client = HttpClient(Android)

    suspend fun reservar(libro: Libro): String {
        if (verificarStock(libro.idLibro)) {
            val idReserva = crearReserva()
            if (idReserva != null) {
                detallarReserva(idReserva, libro.idLibro)
                relacionarClienteReserva(idReserva)
                actualizarStock(libro.idLibro, estaDisponible = true, disponibilidad = false)
                return "Reserva realizada con éxito!"
            } else {
                return "Ha ocurrido un error, por favor intente más tarde."
            }
        } else {
            return "No hay unidades disponibles"
        }
    }

    suspend fun crearReserva(): Int? {
        return try {
            val fecLimite = LocalDateTime.now().plusDays(7)
            val response: HttpResponse = client.post("${BASE_URL}reserva") {
                contentType(ContentType.Application.Json)
                headers {
                    append("apikey", API_KEY)
                    append("Authorization", "Bearer $API_KEY")
                    append("Prefer", "return=representation")
                }
                setBody("""{"fec_limite": "$fecLimite", "estado": "pendiente"}""")
            }
            val body = response.bodyAsText()
            val reserva: Reserva = Gson().fromJson(body.trim().removeSurrounding("[", "]"), Reserva::class.java)
            reserva.idReserva
        }
        catch (e: Exception) {
            Log.e("Crear Reserva", e.message.toString())
            null
        }
    }

    suspend fun detallarReserva(idReserva: Int, idLibro: Int): Boolean {
        return try {
            val response: HttpResponse = client.post("${BASE_URL}detalle_reserva") {
                contentType(ContentType.Application.Json)
                headers {
                    append("apikey", API_KEY)
                    append("Authorization", "Bearer $API_KEY")
                    append("Prefer", "return=representation")
                }
                setBody("""{"id_reserva": $idReserva, "id_libro": $idLibro}""")
            }
            response.status.isSuccess()
        }
        catch (e: Exception) {
            Log.e("Detallar Reserva", e.message.toString())
            false
        }

    }

    suspend fun relacionarClienteReserva(idReserva: Int): Boolean {
        return try {
            val response: HttpResponse = client.post("${BASE_URL}cli_mul_res") {
                contentType(ContentType.Application.Json)
                headers {
                    append("apikey", API_KEY)
                    append("Authorization", "Bearer $API_KEY")
                    append("Prefer", "return=representation")
                }
                setBody("""{"ci_cliente": "${clienteActual?.ciCliente}", "id_reserva": $idReserva}""")
            }
            val success = response.status.isSuccess()
            success
        } catch (e: Exception) {
            Log.e("RelacionarClienteReserva", "Error al relacionar cliente con reserva: ${e.message}", e)
            false
        }
    }


    suspend fun verificarStock(id_libro: Int): Boolean {
        return try {
            val response: HttpResponse = client.get("${BASE_URL}stock?id_libro=eq.${id_libro}&disponibilidad=eq.true") {
                headers {
                    append("apikey", API_KEY)
                    append("Authorization", "Bearer $API_KEY")
                }
            }
            val body = response.bodyAsText()
            val unidades = Gson().fromJson(body, Array<Any>::class.java).toList()
            unidades.isNotEmpty()
        } catch (e: Exception) {
            Log.e("verificarStock: ", e.message.toString())
            false
        }
    }

    suspend fun actualizarStock(idLibro: Int, estaDisponible: Boolean, disponibilidad: Boolean): Boolean {
        return try {
            val response: HttpResponse =
                client.get("${BASE_URL}stock?id_libro=eq.$idLibro&disponibilidad=eq.$estaDisponible") {
                    headers {
                        append("apikey", API_KEY)
                        append("Authorization", "Bearer $API_KEY")
                    }
                }
            val body = response.bodyAsText()
            val unidades = Gson().fromJson(body, Array<Stock>::class.java).toList()
            if (unidades.isNotEmpty()) {
                val idStock = unidades[0].idStock
                val patchResponse: HttpResponse = client.patch("${BASE_URL}stock?id_stock=eq.$idStock") {
                    contentType(ContentType.Application.Json)
                    headers {
                        append("apikey", API_KEY)
                        append("Authorization", "Bearer $API_KEY")
                    }
                    setBody("""{"disponibilidad": $disponibilidad}""")
                }
                patchResponse.status.isSuccess()
            }
            else {
                false
            }
        }
        catch (e: Exception) {
            Log.e("Actualizar Stock", e.message.toString())
            false
        }
    }

    suspend fun obtenerReservasAdap(): List<ReservaAdap>? {
        return try {
            val response: HttpResponse =
                client.get("${BASE_URL}cli_mul_res?ci_cliente=eq.${clienteActual?.ciCliente}&select=reserva(id_reserva,estado,fec_limite,detalle_reserva(libro(id_libro,titulo,portada)))") {
                    headers {
                        append("apikey", API_KEY)
                        append("Authorization", "Bearer $API_KEY")
                    }
                }
            val body = response.bodyAsText()
            val reservas = Gson().fromJson(body, Array<ReservaAdap>::class.java).toList()
            reservas
        }
        catch (e: Exception) {
            Log.e("Obtener Reservas Adap", e.message.toString())
            null
        }
    }

    suspend fun cancelarReserva(idReserva: Int, idLibro: Int): Boolean {
        return try {
            val response: HttpResponse =
                client.delete("${BASE_URL}reserva?id_reserva=eq.$idReserva") {
                    headers {
                        append("apikey", API_KEY)
                        append("Authorization", "Bearer $API_KEY")
                    }
                }
            if (response.status.isSuccess()) {
                actualizarStock(idLibro, estaDisponible = false, disponibilidad = true)
                true
            } else {
                false
            }
        }
        catch (e: Exception) {
            Log.e("Cancelar Reserva", e.message.toString())
            false
        }
    }
}