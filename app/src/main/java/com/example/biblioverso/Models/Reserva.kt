package com.example.biblioverso.Models

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class Reserva(
    @SerializedName("id_reserva")
    val idReserva: Int? = null,
    @SerializedName("fec_reserva")
    val fecReserva: String? = null,
    @SerializedName("fec_prestamo")
    val fecPrestamo: String? = null,
    @SerializedName("fec_limite")
    val fecLimite: String = LocalDateTime.now().plusDays(7).toString(),
    val estado: String = "pendiente"
)

data class ReservaAdap(
    val reserva: ReservaEsp
)

data class ReservaEsp(
    @SerializedName("id_reserva")
    val idReserva: Int,
    val estado: String,
    val fec_limite: String,
    val detalle_reserva: List<DetalleReserva>
)

data class DetalleReserva(
    val libro: LibroEsp
)

data class LibroEsp(
    @SerializedName("id_libro")
    val idLibro: Int,
    val titulo: String,
    val portada: String
)
