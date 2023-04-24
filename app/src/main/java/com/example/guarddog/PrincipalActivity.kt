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


enum class ProviderType() {
    BASIC,
    GOOGLE
}

class PrincipalActivity : AppCompatActivity() {
    @SuppressLint("StringFormatInvalid")
    val noticesList = ArrayList<NoticesModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)
        supportActionBar?.hide()
        FirebaseFirestore.setLoggingEnabled(true)
        FirebaseApp.initializeApp(this)

        // Setup
        val bundle: Bundle? = intent.extras
        val email: String? = bundle?.getString("email")
        val provider: String? = bundle?.getString("provider")
        setup(email ?: "No email", provider ?: "No provider")

        // Guardado de datos del usuario
        val prefs: SharedPreferences.Editor =
            getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()

        // Creación/ actualización del usuario en Firestore Data Base
        val db = FirebaseFirestore.getInstance()
        val userAuthenticated = Firebase.auth.currentUser
        val userUID = userAuthenticated?.uid
        val user = hashMapOf("email" to email, "id" to userUID)
        val refLinks = db.collection("notices")
        var noticeModel = NoticesModel(
            nombreDueno = "",
            nombrePerro = "",
            email = "",
            telefono = "",
            diaDesaparicion = "",
            zonaDesaparicion = "",
            imagenPerro = "",
            observaciones = ""
        )

        db.collection("users").document(email.toString()).set(user)
        println("GD---> USER INFO:\nEl userUID es: " + userUID + "\nEl email es: " + email)

        // Obtención de anuncios de Firestore
        refLinks.get().addOnSuccessListener { result ->
            for (document in result) {
                noticeModel.nombrePerro = document.getString("nombrePerro").toString()
                noticeModel.nombreDueno = document.getString("nombreDueno").toString()
                noticeModel.email = document.getString("email").toString()
                noticeModel.telefono = document.getString("telefono").toString()
                noticeModel.observaciones = document.getString("observaciones").toString()
                noticeModel.zonaDesaparicion = document.getString("zonaDesaparicion").toString()
                noticeModel.diaDesaparicion = document.getString("diaDesaparicion").toString()
                noticeModel.imagenPerro = document.getString("imagen").toString()

                println(
                    "GD---> DOCUMENT INFO:\n nombrePerro = ${noticeModel.nombrePerro} \n nombreDueño = ${noticeModel.nombreDueno} \n email = ${noticeModel.email} \n telefono = ${noticeModel.telefono} \n zonaDesaparicion = ${noticeModel.zonaDesaparicion} \n diaDesaparicion = ${noticeModel.diaDesaparicion} \n observaciones = ${noticeModel.observaciones} \n imagen = ${noticeModel.imagenPerro}"
                )
            }
        }
            .addOnFailureListener { exception ->
                println("GD---> ERROR REPORT:\n exception = $exception")
            }


    }

    private fun setup(email: String, provider: String) {
        val logoutButton = findViewById<Button>(R.id.logoutButton)
        val linksButton = findViewById<Button>(R.id.linksButton)

        addRowsToTable(noticesList)

        logoutButton.setOnClickListener {
            // Borrado de datos
            val prefs: SharedPreferences.Editor = getSharedPreferences(getString(R.string.prefs_file),Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()

            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }

        linksButton.setOnClickListener {
            val linksActivity = Intent(this, LinksActivity::class.java)
            startActivity(linksActivity)
        }

    }

    private fun inflateTableRow(dog: String, owner: String, day: String, zone: String, email: String, telephone: String, imagenPerro: String, observaciones: String): TableRow {
        val tableRow = LayoutInflater.from(this).inflate(R.layout.notice_row_layout, null) as TableRow

        val dogImage = tableRow.findViewById<ImageView>(R.id.rowImageView)
        //tableRow.findViewById<ImageView>(R.id.rowImageView).setImageBitmap(imagenPerro)
        tableRow.findViewById<TextView>(R.id.nombrePerroTextView).text = dog
        tableRow.findViewById<TextView>(R.id.diaTextView).text = day
        tableRow.findViewById<TextView>(R.id.zonaTextView).text = zone
        tableRow.findViewById<TextView>(R.id.nombreDueTextView).text = owner
        tableRow.findViewById<TextView>(R.id.emailTextView).text = email
        tableRow.findViewById<TextView>(R.id.telefonoTextView).text = telephone
        tableRow.findViewById<TextView>(R.id.descriptionTextView).text = observaciones

        if(imagenPerro != null) {
            Picasso.get().load(imagenPerro).fit().into(dogImage)
        } else {
            println("GD---> ERROR REPORT: función inflateTableRow/if -> imagenPerro está llegando nulo")
        }

        return tableRow
    }

    private fun addRowsToTable(dataList: List<NoticesModel>) {
        val tableLayout = findViewById<TableLayout>(R.id.idTableLayout)
        for (data in dataList) {
            val tableRow = inflateTableRow(dog = data.nombrePerro, owner = data.nombreDueno, day = data.diaDesaparicion, zone = data.zonaDesaparicion, email = data.email, telephone = data.telefono, imagenPerro = data.imagenPerro, observaciones = data.observaciones)
            tableLayout.addView(tableRow)
        }
    }
}