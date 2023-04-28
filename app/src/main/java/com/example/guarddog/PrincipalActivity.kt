// Guard Dog
// Autor: Martín Sánchez Martínez
// Fecha: 6 de Abril de 2023

package com.example.guarddog

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import android.view.LayoutInflater
import android.widget.*
import com.squareup.picasso.Picasso

class PrincipalActivity : AppCompatActivity() {
    @SuppressLint("StringFormatInvalid")
    val noticesList = ArrayList<NoticesModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)
        supportActionBar?.hide()
        FirebaseFirestore.setLoggingEnabled(true)
        FirebaseApp.initializeApp(this)

        // Setup:
        setup(userModel.email ?: "No email")

        // Guardado de datos del usuario:
        // (email y que opción eligió para el inicio de sesión)
        val prefs: SharedPreferences.Editor =
            getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", userModel.email)
        prefs.putString("provider", userModel.provider.toString())
        prefs.apply()

        // Creación/ actualización del usuario en Firebase:
        val userAuthenticated = Firebase.auth.currentUser
        val userUID = userAuthenticated?.uid
        userModel.uid = userUID.toString()
        val user = hashMapOf("email" to userModel.email, "id" to userModel.uid)

        // Variables Firebase:
        val db = FirebaseFirestore.getInstance()

        val refNotices = db.collection("notices")

        println("GD Control---> USER: El userUID es: " + userUID + ", El email es: " + userModel.email)


        // Obtención de anuncios de Firestore:
        refNotices.get().addOnSuccessListener { result ->
            for (document in result) {
                var noticeModel = NoticesModel(
                    nombreDueno = document.getString("nombreDueno").toString(),
                    nombrePerro = document.getString("nombrePerro").toString(),
                    email = document.getString("email").toString(),
                    telefono = document.getString("telefono").toString(),
                    diaDesaparicion = document.getString("diaDesaparicion").toString(),
                    zonaDesaparicion = document.getString("zonaDesaparicion").toString(),
                    imagenPerro = document.getString("imagen").toString(),
                    observaciones = document.getString("observaciones").toString()
                )

                noticesList.add(noticeModel)
            }
            addRowsToTable(noticesList)
        }.addOnFailureListener { exception ->
            Toast.makeText(
                this,
                "¡Ups... estamos teniendo problemas para cargar los anuncios!",
                Toast.LENGTH_SHORT
            ).show()
            println("GD Control---> ERROR:\n exception = $exception")
        }
    }

    override fun onResume() {
        super.onResume()
        addRowsToTable(noticesList)
    }


    private fun setup(email: String) {
        val logoutButton = findViewById<Button>(R.id.logoutButton)
        val linksButton = findViewById<Button>(R.id.linksButton)
        val myNoticeButton = findViewById<Button>(R.id.myAdvertisementButton)

        // Botones de navegación:
        logoutButton.setOnClickListener {
            // Borrado de datos:
            val prefs: SharedPreferences.Editor =
                getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()

            // Logout y vuelta a la pantalla inicial:
            FirebaseAuth.getInstance().signOut()
            val linksActivity = Intent(this, AuthenticationActivity::class.java)
            startActivity(linksActivity)
        }

        linksButton.setOnClickListener {
            // Navegación a la pantalla Links:
            val linksActivity = Intent(this, LinksActivity::class.java)
            startActivity(linksActivity)
        }

        myNoticeButton.setOnClickListener {
            // Navegación a la pantalla mis anuncios:
            val myNoticesActivity = Intent(this, MyNoticesActivity::class.java).apply {
                // Reseteo del selectedNoticeModel para evitar conflictos
                selectedNoticeModel = NoticesModel(
                    nombrePerro = "",
                    nombreDueno = "",
                    zonaDesaparicion = "",
                    diaDesaparicion = "",
                    email = "",
                    telefono = "",
                    imagenPerro = "",
                    observaciones = ""
                )
            }
            startActivity(myNoticesActivity)
        }
    }

    // Configuración del TableRow
    private fun inflateTableRow(notice: NoticesModel): TableRow {
        val tableRow =
            LayoutInflater.from(this).inflate(R.layout.notice_row_layout, null) as TableRow

        val dogImage = tableRow.findViewById<ImageView>(R.id.rowImageView)

        tableRow.findViewById<TextView>(R.id.nombrePerroTextView).text = notice.nombrePerro
        tableRow.findViewById<TextView>(R.id.diaTextView).text = notice.diaDesaparicion
        tableRow.findViewById<TextView>(R.id.zonaTextView).text = notice.zonaDesaparicion
        tableRow.findViewById<TextView>(R.id.nombreDueTextView).text = notice.nombreDueno
        tableRow.findViewById<TextView>(R.id.emailTextView).text = notice.email
        tableRow.findViewById<TextView>(R.id.telefonoTextView).text = notice.telefono

        // Configuración de la imagen:
        try {
            if (notice.imagenPerro != null) {
                Picasso.get().load(notice.imagenPerro).fit().into(dogImage)
            } else {
                println("GD Control---> ERROR: función inflateTableRow/if -> imagenPerro está llegando nulo")
                Toast.makeText(
                    this,
                    "¡Ups... ha habido algún problema con la imagen!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            println("GD---> ERROR: Exception catch imagenPerro=> $e")
            Toast.makeText(
                this,
                "¡Ups... ha habido algún problema con la imagen!",
                Toast.LENGTH_SHORT
            ).show()
        }

        return tableRow
    }

    // Configuración de la TableLayout:
    private fun addRowsToTable(noticesList: List<NoticesModel>) {
        val tableLayout = findViewById<TableLayout>(R.id.idTableLayoutNotices)
        tableLayout.removeAllViews()

        for (notice in noticesList) {
            val tableRow = inflateTableRow(notice)
            tableRow.setOnClickListener {
                selectedNoticeModel = notice
                val noticeActivity = Intent(this, NoticeActivity::class.java)
                startActivity(noticeActivity)
            }
            tableLayout.addView(tableRow)
        }
    }
}