package com.example.reporductordemusica.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.reporductordemusica.R
import com.example.reporductordemusica.ViewModel.AuthViewModel
import com.example.reporductordemusica.ui.listArtist.ArtistListActivity
import com.example.reporductordemusica.ui.register.RegisterActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var signInLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show()
            }
        }

        val emailInput = findViewById<EditText>(R.id.textinputEditEmail)
        val passwordInput = findViewById<EditText>(R.id.textinputEditPassword)
        val loginButton = findViewById<Button>(R.id.buttonEnterAccount)
        val registerButton = findViewById<Button>(R.id.buttonRegister)
        val googleSignInButton = findViewById<Button>(R.id.buttonAuthGOogle)

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

        googleSignInButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            signInLauncher.launch(signInIntent)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        authViewModel.signInWithCredential(credential, { email ->
            navigateToArtistList(email)
        }, {
            Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
        })
    }


    private fun navigateToArtistList(email: String) {
        val intent = Intent(this, ArtistListActivity::class.java).apply {
            putExtra("USER_EMAIL", email)
        }
        startActivity(intent)
    }
}
