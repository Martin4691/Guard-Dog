package com.example.guarddog

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import java.nio.file.attribute.AclEntry.Builder

class AuthenticationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_GuardDog)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        // Analytics Event:
        var analytics:FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        var bundle = Bundle()
        bundle.putString("autenticacion", "Pantalla de Autenticación")
        analytics.logEvent("InitScreen",bundle)

        //Setup
        setup()
    }

    private fun setup() {
        title = "¡BIENVENIDO A GUARD DOG!"
        val loginButton = findViewById<Button>(R.id.loginButton)
        val signupButton = findViewById<Button>(R.id.signupButton)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)

        signupButton.setOnClickListener {
            if (emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailEditText.text.toString(),passwordEditText.text.toString()).addOnCompleteListener {

                    if (it.isSuccessful) {
                        goToPrincipal(it.result?.user?.email ?: "No data", ProviderType.BASIC)
                    } else {
                        showAlert()
                    }
                }
            }
        }

        loginButton.setOnClickListener {
            if (emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(emailEditText.text.toString(),passwordEditText.text.toString()).addOnCompleteListener {

                    if (it.isSuccessful) {
                        goToPrincipal(it.result?.user?.email ?: "No data", ProviderType.BASIC)
                    } else {
                        showAlert()
                    }
                }
            }
        }
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¡ERROR!")
        builder.setMessage("Se ha producido un error en la autenticación del usuario.")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun goToPrincipal(email: String, provider: ProviderType) {
        val principalIntent = Intent(this, PrincipalActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(principalIntent)
    }

}