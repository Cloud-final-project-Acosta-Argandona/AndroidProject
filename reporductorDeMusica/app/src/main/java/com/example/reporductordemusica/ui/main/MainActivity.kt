package com.example.reporductordemusica.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.reporductordemusica.R
import com.example.reporductordemusica.ViewModel.AuthViewModel
import com.example.reporductordemusica.ui.listArtist.ArtistListActivity
import com.example.reporductordemusica.ui.register.RegisterActivity

class MainActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val emailInput = findViewById<EditText>(R.id.textinputEditEmail)
        val passwordInput = findViewById<EditText>(R.id.textinputEditPassword)
        val loginButton = findViewById<Button>(R.id.buttonEnterAccount)
        val registerButton = findViewById<Button>(R.id.buttonRegister)

        loginButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            authViewModel.signIn(email, password, {
                navigateToArtistList(email)
            }, {
                Toast.makeText(this, "Login failed: ${it.message}", Toast.LENGTH_SHORT).show()
            })
        }

        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun navigateToArtistList() {
        val intent = Intent(this, ArtistListActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToArtistList(email: String) {
        val intent = Intent(this, ArtistListActivity::class.java).apply {
            putExtra("USER_EMAIL", email)
        }
        startActivity(intent)
    }

}
