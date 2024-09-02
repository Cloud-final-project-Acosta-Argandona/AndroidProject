package com.example.reporductordemusica.ui.register

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
import com.example.reporductordemusica.ViewModel.UserViewModel
import com.example.reporductordemusica.ui.listArtist.ArtistListActivity
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : AppCompatActivity() {
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val usernameInput = findViewById<TextInputEditText>(R.id.textInputUsername)
        val emailInput = findViewById<TextInputEditText>(R.id.textInputEmailRegister)
        val passwordInput = findViewById<TextInputEditText>(R.id.textInputPasswordRegister)
        val registerButton = findViewById<Button>(R.id.buttonRegisterEnter)

        registerButton.setOnClickListener {
            val username = usernameInput.text.toString()
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            userViewModel.registerUser(email, password, username, {
                navigateToArtistList()
            }, {
                Toast.makeText(this, "Registration failed: ${it.message}", Toast.LENGTH_SHORT).show()
            })
        }
    }

    private fun navigateToArtistList() {
        val intent = Intent(this, ArtistListActivity::class.java)
        startActivity(intent)
    }
}
