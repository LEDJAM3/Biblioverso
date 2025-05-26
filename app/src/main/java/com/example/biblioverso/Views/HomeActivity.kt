package com.example.biblioverso.Views

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.biblioverso.Adapters.LibroAdapter
import com.example.biblioverso.Controllers.LibroController
import com.example.biblioverso.Data.LibroRepository
import com.example.biblioverso.Data.PreferencesHelper
import com.example.biblioverso.Data.SessionManager
import com.example.biblioverso.R
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {
    private lateinit var rvLibros: RecyclerView
    private lateinit var libroAdapter: LibroAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        Toast.makeText(this@HomeActivity, "Bienvenido, ${SessionManager.clienteActual?.usuario}", Toast.LENGTH_SHORT).show()

        val menu: Toolbar = findViewById(R.id.tbMenu)
        val svBuscador: SearchView = findViewById(R.id.svBuscador)

        rvLibros = findViewById(R.id.rvLibros)

        setSupportActionBar(menu)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        obtenerLibros()

        svBuscador.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val texto = newText.orEmpty().lowercase()
                val filtrados = LibroRepository.libros.filter { libro ->
                    libro.titulo.lowercase().contains(texto) ||
                            libro.sinopsis.lowercase().contains(texto) ||
                            libro.fechaPublicacion.lowercase().contains(texto) ||
                            libro.genero.lowercase().contains(texto) ||
                            libro.editorial.lowercase().contains(texto) ||
                            libro.isbn.lowercase().contains(texto) ||
                            libro.autor.any { it.nombre.lowercase().contains(texto) }
                }
                libroAdapter.actualizarLista(filtrados)
                return true
            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.miReservas -> {
                true
            }

            R.id.miImagen -> {
                true
            }

            R.id.miSalir -> {
                val prefsHelper = PreferencesHelper(this@HomeActivity)
                prefsHelper.limpiarSesion()
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun obtenerLibros() {
        lifecycleScope.launch {
            val libros = LibroController().ObtenerLibros()
            if (libros != null) {
                LibroRepository.libros = libros
                initRecycler()
            }
            Log.e("Libros", libros.toString())
        }
    }

    fun initRecycler() {
        libroAdapter = LibroAdapter(LibroRepository.libros)
        rvLibros.adapter = libroAdapter
    }
}