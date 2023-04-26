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
    var noticeEmail = ""
    var noticeDogName = ""


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
        val refNotices = db.collection("notices")

        db.collection("users").document(email.toString()).set(user)
        println("GD---> USER INFO:\nEl userUID es: " + userUID + "\nEl email es: " + email)
        userModel = UserModel(email = email.toString(), uid = userUID.toString())

        // Obtención de anuncios de Firestore
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
                println("GD---> ERROR REPORT:\n exception = $exception")
            }

    }

    override fun onResume() {
        super.onResume()
        addRowsToTable(noticesList)
    }


    private fun setup(email: String, provider: String) {
        val logoutButton = findViewById<Button>(R.id.logoutButton)
        val linksButton = findViewById<Button>(R.id.linksButton)
        val myNoticeButton = findViewById<Button>(R.id.myAdvertisementButton)

        logoutButton.setOnClickListener {
            // Borrado de datos
            val prefs: SharedPreferences.Editor = getSharedPreferences(getString(R.string.prefs_file),Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()

            // Logout y vuelta al Login
            FirebaseAuth.getInstance().signOut()
            val linksActivity = Intent(this, AuthenticationActivity::class.java)
            startActivity(linksActivity)
        }

        linksButton.setOnClickListener {
            val linksActivity = Intent(this, LinksActivity::class.java)
            startActivity(linksActivity)
        }

        myNoticeButton.setOnClickListener {
            val myNoticesActivity = Intent(this, MyNoticesActivity::class.java).apply {
                putExtra("email", email)
        }
            startActivity(myNoticesActivity)
        }

    }

    private fun inflateTableRow(dog: String, owner: String, day: String, zone: String, email: String, telephone: String, imagenPerro: String, observaciones: String): TableRow {
        val tableRow = LayoutInflater.from(this).inflate(R.layout.notice_row_layout, null) as TableRow

        val dogImage = tableRow.findViewById<ImageView>(R.id.rowImageView)

        tableRow.findViewById<TextView>(R.id.nombrePerroTextView).text = dog
        tableRow.findViewById<TextView>(R.id.diaTextView).text = day
        tableRow.findViewById<TextView>(R.id.zonaTextView).text = zone
        tableRow.findViewById<TextView>(R.id.nombreDueTextView).text = owner
        tableRow.findViewById<TextView>(R.id.emailTextView).text = email
        tableRow.findViewById<TextView>(R.id.telefonoTextView).text = telephone

        try {
            if(imagenPerro != null) {
                Picasso.get().load(imagenPerro).fit().into(dogImage)
            } else {
                println("GD---> ERROR REPORT: función inflateTableRow/if -> imagenPerro está llegando nulo")
            }
        } catch (e: Exception) {
            println("GD---> ERROR REPORT: $e")
        }

        return tableRow
    }

    private fun addRowsToTable(noticesList: List<NoticesModel>) {
        val tableLayout = findViewById<TableLayout>(R.id.idTableLayoutNotices)
        tableLayout.removeAllViews()

        for (notice in noticesList) {
            val tableRow = inflateTableRow(dog = notice.nombrePerro, owner = notice.nombreDueno, day = notice.diaDesaparicion, zone = notice.zonaDesaparicion, email = notice.email, telephone = notice.telefono, imagenPerro = notice.imagenPerro, observaciones = notice.observaciones)
            tableRow.setOnClickListener {
                val noticeActivity = Intent(this, NoticeActivity::class.java).apply {
                    noticeEmail = notice.email
                    noticeDogName = notice.nombrePerro
                    putExtra("noticeEmail", noticeEmail)
                    putExtra("noticeDogName", noticeDogName)
                }
            startActivity(noticeActivity)
            }
            tableLayout.addView(tableRow)
        }
    }
}