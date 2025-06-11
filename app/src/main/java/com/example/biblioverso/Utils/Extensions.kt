package com.example.biblioverso.Utils

import android.widget.EditText
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun EditText.textStr(): String = this.text.toString()

fun formatearFechaCorta(fechaIso: String): String {
    val fecha = ZonedDateTime.parse(fechaIso)
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    return fecha.format(formatter)
}