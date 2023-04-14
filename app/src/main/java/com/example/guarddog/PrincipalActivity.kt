package com.example.guarddog

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth

enum class ProviderType() {
    BASIC
}

class PrincipalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)

        // Setup
        val bundle: Bundle? = intent.extras
        val email: String? = bundle?.getString("email")
        val provider: String? = bundle?.getString("provider")
        setup(email ?: "No email", provider ?: "No provider")
    }

    private fun setup(email: String, provider: String) {
        title = "¡PERDIDOS!"
        val logoutButton = findViewById<Button>(R.id.logoutButton)

        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }


    }



}