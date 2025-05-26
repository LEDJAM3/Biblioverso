package com.example.biblioverso.Views

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.biblioverso.Controllers.ClienteController
import com.example.biblioverso.Data.PreferencesHelper
import com.example.biblioverso.Data.SessionManager
import com.example.biblioverso.R
import com.example.biblioverso.Utils.textStr
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var etxtLoginUser: EditText
    private lateinit var etxtLoginPass: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        etxtLoginUser = findViewById(R.id.etxtLoginUsername)
        etxtLoginPass = findViewById(R.id.etxtLoginPassword)
        val btnLogin: Button = findViewById(R.id.btnLogin)
        val btnNewUser: TextView = findViewById(R.id.tvNewUser)

        btnNewUser.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            if (etxtLoginUser.textStr().isNotBlank() && etxtLoginPass.textStr().isNotBlank()) {
                iniciarSesion()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val prefsHelper = PreferencesHelper(this@MainActivity)
        val cliente = prefsHelper.obtenerCliente()
        if (cliente != null) {
            SessionManager.clienteActual = cliente

            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun iniciarSesion() {
        Toast.makeText(this@MainActivity, "Espere un momento...", Toast.LENGTH_SHORT).show()
        val user = etxtLoginUser.textStr()
        val pass = etxtLoginPass.textStr()

        lifecycleScope.launch {
            val cliente = ClienteController().iniciarSesion(user, pass)

            if (cliente != null) {
                val prefsHelper = PreferencesHelper(this@MainActivity)
                prefsHelper.guardarCliente(cliente)

                SessionManager.clienteActual = cliente

                val intent = Intent(this@MainActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this@MainActivity, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}