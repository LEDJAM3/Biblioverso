package com.example.biblioverso.Views

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.biblioverso.Adapters.ReservaAdapter
import com.example.biblioverso.Controllers.ReservaController
import com.example.biblioverso.Models.ReservaAdap
import com.example.biblioverso.R
import kotlinx.coroutines.launch

class ReservasActivity : AppCompatActivity() {
    lateinit var rvReservas: RecyclerView
    lateinit var reservas: List<ReservaAdap>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reservas)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        rvReservas = findViewById(R.id.rvReservas)
        prueba()
    }

    fun prueba() {
        lifecycleScope.launch {
            val lista = ReservaController().obtenerReservasAdap()
            if (lista != null) {
                reservas = lista
                initRecycler()
            }
            Log.d("Reservas",reservas.toString())
        }
    }

    fun initRecycler() {
        val reservaAdapter = ReservaAdapter(reservas)
        rvReservas.adapter = reservaAdapter

    }
}