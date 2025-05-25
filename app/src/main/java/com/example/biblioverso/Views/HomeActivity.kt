package com.example.biblioverso.Views

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.biblioverso.Data.PreferencesHelper
import com.example.biblioverso.Data.SessionManager
import com.example.biblioverso.R

class HomeActivity : AppCompatActivity() {
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
        setSupportActionBar(menu)
        supportActionBar?.setDisplayShowTitleEnabled(false)
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
}