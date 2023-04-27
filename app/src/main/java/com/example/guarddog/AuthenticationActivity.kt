// Guard Dog
// Autor: Martín Sánchez Martínez
// Fecha: 6 de Abril de 2023

package com.example.guarddog

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import android.widget.LinearLayout
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider


class AuthenticationActivity : AppCompatActivity() {
    private val GOOGLE_SIGN_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        // Pantalla de carga o Splash Screen
        setTheme(R.style.Theme_GuardDog)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        supportActionBar?.hide()

        // Analytics Event:
        val analytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("autenticacion", "Pantalla de Autenticación")
        analytics.logEvent("InitScreen", bundle)

        //Setup
        setup()
        session()
    }

    override fun onStart() {
        super.onStart()
        val authLayout = findViewById<LinearLayout>(R.id.authLayout)
        authLayout.visibility = View.VISIBLE
    }

    // Control de sesión:
    private fun session() {
        val authLayout = findViewById<LinearLayout>(R.id.authLayout)
        val prefs: SharedPreferences? =
            getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email: String? = prefs?.getString("email", null)
        val provider: String? = prefs?.getString("provider", null)

        // Si el usuario no cerró hizo logout, no tendrá que volver a hacer login.
        if (email != null && provider != null) {
            authLayout.visibility = View.INVISIBLE
            goToPrincipal(email, ProviderType.valueOf(provider))
        }
    }

    private fun setup() {
        val loginButton = findViewById<Button>(R.id.loginButton)
        val signupButton = findViewById<Button>(R.id.signupButton)
        val googleButton = findViewById<Button>(R.id.googleButton)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)

        // Registrarse:
        signupButton.setOnClickListener {
            if (emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    emailEditText.text.toString(),
                    passwordEditText.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        goToPrincipal(it.result?.user?.email ?: "No data", ProviderType.BASIC)
                    } else {
                        showAlert()
                    }
                }
            }
        }

        // Iniciar sesión con email:
        loginButton.setOnClickListener {
            if (emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    emailEditText.text.toString(),
                    passwordEditText.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        goToPrincipal(it.result?.user?.email ?: "No data", ProviderType.BASIC)
                    } else {
                        showAlert()
                    }
                }
            }
        }

        // Iniciar sesión con la cuenta de Google (Botón):
        googleButton.setOnClickListener {
            val googleConf: GoogleSignInOptions =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id)).requestEmail()
                    .build()
            val googleClient: GoogleSignInClient = GoogleSignIn.getClient(this, googleConf)

            googleClient.signOut()

            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
        }
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¡ERROR!")
        builder.setMessage("Se ha producido un error en la autenticación del usuario.\nRevise su Usuario, Contraseña y que esté accediendo por el mismo método por el que se registró.")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    // Navegación siguiente pantalla:
    private fun goToPrincipal(email: String, provider: ProviderType) {
        val principalIntent = Intent(this, PrincipalActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(principalIntent)
    }

    // Inicio de sesión con Google:
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)

                if (account != null) {
                    val credential: AuthCredential =
                        GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                goToPrincipal(
                                    account.email ?: "No email in account.",
                                    ProviderType.GOOGLE
                                )
                            } else {
                                showAlert()
                            }
                        }
                }
            } catch (error: ApiException) {
                showAlert()
            }
        }
    }
}