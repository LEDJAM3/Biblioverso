package com.example.biblioverso.Views

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.biblioverso.Controllers.LibroController
import com.example.biblioverso.Models.Libro
import com.example.biblioverso.R
import com.google.android.material.appbar.CollapsingToolbarLayout
import kotlinx.coroutines.launch

class LibroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_libro)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val libro = intent.getSerializableExtra("Libro") as Libro
        cargarLibro(libro)
    }

    private fun cargarLibro(libro: Libro) {
        val ctbLibro: CollapsingToolbarLayout = findViewById(R.id.ctbLibro)
        ctbLibro.title = libro.titulo

        val imgPortada: ImageView = findViewById(R.id.imgPortada)
        val urlSegura = libro.portada.replace("http://", "https://")
        Glide.with(this)
            .load(urlSegura)
            .placeholder(R.drawable.icons8_imagen_100)
            .error(R.drawable.icons8_imagen_100)
            .into(imgPortada)

        val tvSinopsis: TextView = findViewById(R.id.tvSinopsis)
        tvSinopsis.text = libro.sinopsis

        val tvNombreAutor: TextView = findViewById(R.id.tvAutorNombre)
        tvNombreAutor.text = libro.autor[0].nombre

        val tvFechasAutor: TextView = findViewById(R.id.tvAutorFechas)
        if (libro.autor[0].fechaMuerte == null)
        {
            tvFechasAutor.text = "${libro.autor[0].fechaNac} - Hoy"
        }
        else {
            tvFechasAutor.text = "${libro.autor[0].fechaNac} - ${libro.autor[0].fechaMuerte}"
        }

        val tvBioAutor: TextView = findViewById(R.id.tvAutorBio)
        tvBioAutor.text = libro.autor[0].biografia

        obtenerCalificacion(libro.idLibro)
    }

    private fun obtenerCalificacion(idLibro: Int) {
        lifecycleScope.launch {
            val libroController = LibroController()
            val calificacion = libroController.obtenerCalificacion(idLibro)
            val ratingBar: android.widget.RatingBar = findViewById(R.id.rbarCalificacion)
            ratingBar.rating = calificacion
        }
    }
}