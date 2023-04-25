package com.example.guarddog

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class MyNoticesActivity : AppCompatActivity() {
    val myNoticesList = ArrayList<NoticesModel>()

    //Firestore
    val db = FirebaseFirestore.getInstance()
    val refNotices = db.collection("notices")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_notices)
        supportActionBar?.hide()

        // Obtención de datos de usuario para comparar:
        val bundle: Bundle? = intent.extras
        val email: String? = bundle?.getString("email")
        val userAuthenticated = Firebase.auth.currentUser
        val userUID = userAuthenticated?.uid
        val user = hashMapOf("email" to email, "id" to userUID)


        println("GD---> USER INFO:\nEl userUID es: " + userUID + "\nEl email es: " + email)

        // Obtención de anuncios de Firestore

        refNotices.get().addOnSuccessListener { result ->
            for (document in result) {
                var notice = NoticesModel(
                    nombreDueno = document.getString("nombreDueno").toString(),
                    nombrePerro = document.getString("nombrePerro").toString(),
                    email = document.getString("email").toString(),
                    telefono = document.getString("telefono").toString(),
                    diaDesaparicion = document.getString("diaDesaparicion").toString(),
                    zonaDesaparicion = document.getString("zonaDesaparicion").toString(),
                    imagenPerro = document.getString("imagen").toString(),
                    observaciones = document.getString("observaciones").toString()
                )

                if (email == notice.email) {
                    println(
                        "GD---> DOCUMENT INFO:\n nombrePerro = ${notice.nombrePerro} \n nombreDueño = ${notice.nombreDueno} \n email = ${notice.email} \n telefono = ${notice.telefono} \n zonaDesaparicion = ${notice.zonaDesaparicion} \n diaDesaparicion = ${notice.diaDesaparicion} \n observaciones = ${notice.observaciones} \n imagen = ${notice.imagenPerro}"
                    )
                    myNoticesList.add(notice)
                }
            }

            addRowsToTable(myNoticesList)
        }.addOnFailureListener { exception ->
            println("GD---> ERROR REPORT:\n exception = $exception")
        }

        // Setup
        setup()
    }

    private fun setup() {
        val backButton = findViewById<Button>(R.id.backIconButton)
        val newNoticeButton = findViewById<Button>(R.id.newNoticeButton)

        backButton.setOnClickListener {
            val principalActivity = Intent(this, PrincipalActivity::class.java)
            startActivity(principalActivity)
        }

        newNoticeButton.setOnClickListener {
            val noticeActivity = Intent(this, FormActivity::class.java)
            startActivity(noticeActivity)
        }
    }

    private fun inflateTableRow(dog: String): TableRow {
        val tableRow = LayoutInflater.from(this).inflate(R.layout.my_notice_row_layout, null) as TableRow

        tableRow.findViewById<TextView>(R.id.myDogNameTextView).text = dog

        return tableRow
    }

    private fun addRowsToTable(noticesList: List<NoticesModel>) {
        val tableLayout = findViewById<TableLayout>(R.id.idTableLayoutMyNotices)
        for (notice in noticesList) {
            val tableRow = inflateTableRow(dog = notice.nombrePerro)
            tableRow.setOnClickListener {
                val noticeActivity = Intent(this, NoticeActivity::class.java)
                startActivity(noticeActivity)
            }
            tableLayout.addView(tableRow)
        }
    }
}