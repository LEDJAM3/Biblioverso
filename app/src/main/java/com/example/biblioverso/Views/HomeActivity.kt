package com.example.biblioverso.Views

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {
    private lateinit var rvLibros: RecyclerView
    private lateinit var libroAdapter: LibroAdapter
    private lateinit var svBuscador: SearchView

    private val cameraResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            val imageBitmap = data?.extras?.get("data") as? Bitmap
            imageBitmap?.let { processImage(it) }
        }
    }

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
        svBuscador = findViewById(R.id.svBuscador)

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
                val intent = Intent(this@HomeActivity, ReservasActivity::class.java)
                startActivity(intent)
                true
            }

            R.id.miImagen -> {
                checkPermissions()
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
        }
    }

    fun initRecycler() {
        libroAdapter = LibroAdapter(LibroRepository.libros)
        rvLibros.adapter = libroAdapter
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission()
        } else {
            // Iniciar cámara si ya se tiene permiso
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraResultLauncher.launch(cameraIntent)
        }
    }

    private fun requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)) {
            Toast.makeText(this, "Permiso de cámara requerido", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 100)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso de cámara concedido", Toast.LENGTH_SHORT).show()
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                cameraResultLauncher.launch(cameraIntent)
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun processImage(bitmap: Bitmap) {

        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                // Mostrar el texto reconocido en el TextView
                /*val recognizedText = visionText.text.replace("\n", " ")
                svBuscador.setQuery(recognizedText, false)*/

                val imageHeight = bitmap.height

                val titleBlock = visionText.textBlocks.maxByOrNull { block ->
                    val box = block.boundingBox
                    val heightScore = box?.height() ?: 0
                    val positionScore = when {
                        box != null && box.top < imageHeight / 3 -> 50
                        box != null && box.top < imageHeight / 2 -> 25
                        else -> 0
                    }
                    val lengthScore = block.text.length
                    heightScore + positionScore + lengthScore
                }
                val recognizedText = titleBlock?.text?.replace("\n", " ")
                svBuscador.setQuery(recognizedText ?: "", false)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                svBuscador.setQuery("", false)
                Log.e("MLKit", "Error reconociendo texto: ${e.message}")
            }
    }
}