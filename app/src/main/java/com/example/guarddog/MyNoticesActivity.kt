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
    var email = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_notices)
        supportActionBar?.hide()

        // Obtención de datos de usuario para comparar:
        val bundle: Bundle? = intent.extras
        email = bundle?.getString("email").toString()
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
                        "GD---> DOCUMENT INFO: notice => $notice"
                    )

                    selectedNoticeModel = notice
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

    override fun onResume() {
        super.onResume()
        addRowsToTable(myNoticesList)
    }


    private fun setup() {
        val backButton = findViewById<Button>(R.id.backIconButton)
        val newNoticeButton = findViewById<Button>(R.id.newNoticeButton)

        backButton.setOnClickListener {
            val principalActivity = Intent(this, PrincipalActivity::class.java)
            startActivity(principalActivity)
        }

        newNoticeButton.setOnClickListener {
            val noticeActivity = Intent(this, FormActivity::class.java).apply {
            putExtra("email", email)
            }
            startActivity(noticeActivity)
        }
    }

    private fun inflateTableRow(selectedNotice: NoticesModel): TableRow {
        val tableRow = LayoutInflater.from(this).inflate(R.layout.my_notice_row_layout, null) as TableRow
        var rowSelectedName = tableRow.findViewById<TextView>(R.id.myDogNameTextView)
        rowSelectedName.text = selectedNotice.nombrePerro

        tableRow.findViewById<Button>(R.id.deleteButton).setOnClickListener {
            val documentId = userModel.email + "-" + selectedNotice.nombrePerro
            val docRef = db.collection("notices").document(documentId)

            println("msm: documentId = $documentId")

            docRef.delete()
                .addOnSuccessListener {
                    println("GD---> DOCUMENT INFO: Se borró el documento $documentId correctamente")
                }
                .addOnFailureListener { e ->
                    // Ocurrió un error al eliminar el documento
                    println("GD---> Error deleting document, $e")
                }

        }

        return tableRow
    }

    private fun addRowsToTable(noticesList: List<NoticesModel>) {
        val tableLayout = findViewById<TableLayout>(R.id.idTableLayoutMyNotices)
        tableLayout.removeAllViews()

        for (notice in noticesList) {
            val tableRow = inflateTableRow(notice)
            tableRow.setOnClickListener {
                val noticeActivity = Intent(this, NoticeActivity::class.java)
                startActivity(noticeActivity)
            }
            tableLayout.addView(tableRow)
        }
    }
}