package com.example.biblioverso.Views

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.biblioverso.Controllers.ClienteController
import com.example.biblioverso.Models.Cliente
import com.example.biblioverso.Models.Persona
import com.example.biblioverso.R
import com.example.biblioverso.Utils.textStr
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var etxtNombre: EditText
    private lateinit var etxtApellido: EditText
    private lateinit var etxtEmail: EditText
    private lateinit var etxtTelefono: EditText
    private lateinit var etxtDireccion: EditText
    private lateinit var etxtCI: EditText
    private lateinit var etxtUser: EditText
    private lateinit var etxtPass: EditText
    private lateinit var rbM: RadioButton
    private lateinit var rbF: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        etxtNombre = findViewById(R.id.etxtNombre)
        etxtApellido = findViewById(R.id.etxtApellido)
        etxtEmail = findViewById(R.id.etxtEmail)
        etxtTelefono = findViewById(R.id.etxtTelefono)
        etxtDireccion = findViewById(R.id.etxtDireccion)
        etxtCI = findViewById(R.id.etxtCI)
        etxtUser = findViewById(R.id.etxtUsuario)
        etxtPass = findViewById(R.id.etxtPassword)
        rbM = findViewById(R.id.rbM)
        rbF = findViewById(R.id.rbF)
        val btnRegistrar: Button = findViewById(R.id.btnRegistrar)

        btnRegistrar.setOnClickListener {
            val ciValida = etxtCI.textStr().isNotBlank() && etxtCI.textStr().length == 8
            val generoSeleccionado = rbM.isChecked || rbF.isChecked

            if (etxtNombre.textStr().isNotBlank() &&
                etxtApellido.textStr().isNotBlank() &&
                etxtEmail.textStr().isNotBlank() &&
                ciValida &&
                etxtUser.textStr().isNotBlank() &&
                etxtPass.textStr().isNotBlank() &&
                generoSeleccionado) {

                crearCliente()
            }
            else {
                Toast.makeText(this, "Rellene los campos obligatorios correctamente", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun crearCliente() {
        Toast.makeText(this, "Espere un momento", Toast.LENGTH_LONG).show()

        val nombre = etxtNombre.textStr()
        val apellido = etxtApellido.textStr()
        val email = etxtEmail.textStr()
        val telefono = etxtTelefono.textStr()
        val direccion = etxtDireccion.textStr()
        val ci = etxtCI.textStr()
        val usuario = etxtUser.textStr()
        val password = etxtPass.textStr()
        val genero = if (rbM.isChecked) "M" else "F"

        val persona = Persona(
            nombre = nombre,
            apellido = apellido,
            email = email,
            telefono = telefono,
            direccion = direccion,
            genero = genero
        )

        val cliente = Cliente(
            ciCliente = ci,
            usuario = usuario,
            password = password
        )

        lifecycleScope.launch {
            val success = ClienteController().crearCliente(persona, cliente)
            if (success) {
                Toast.makeText(this@RegisterActivity, "Registro Exitoso", Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this@RegisterActivity, "Error al registrar", Toast.LENGTH_LONG).show()
            }
        }
    }
}