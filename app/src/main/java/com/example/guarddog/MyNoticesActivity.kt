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

    //Firestore:
    val db = FirebaseFirestore.getInstance()
    val refNotices = db.collection("notices")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_notices)
        supportActionBar?.hide()

        // Obtención de anuncios (que sean del user logeado) de Firestore:
        refNotices.get().addOnSuccessListener { result ->
            println("GD Control---> Mis Anuncios: userModel.email = ${userModel.email}")

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

                if (userModel.email == notice.email) {
                    println("GD---> DOCUMENT INFO: notice => $notice")
                    myNoticesList.add(notice)
                }
            }

            addRowsToTable(myNoticesList)
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "¡Ups... ha habido algún problema al cargar tus anuncios!", Toast.LENGTH_SHORT).show()
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
            //Reseteo selectedNoticeModel:
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

            val principalActivity = Intent(this, PrincipalActivity::class.java)
            startActivity(principalActivity)
        }

        newNoticeButton.setOnClickListener {
            val noticeActivity = Intent(this, FormActivity::class.java)
            startActivity(noticeActivity)
        }
    }

    // Configuración TableRows:
    private fun inflateTableRow(selectedNotice: NoticesModel): TableRow {
        val tableRow = LayoutInflater.from(this).inflate(R.layout.my_notice_row_layout, null) as TableRow
        var rowSelectedName = tableRow.findViewById<TextView>(R.id.myDogNameTextView)
        rowSelectedName.text = selectedNotice.nombrePerro

        // Se le asignara la funcion de borrar al botón "Delete":
        tableRow.findViewById<Button>(R.id.deleteButton).setOnClickListener {
            val documentId = userModel.email + "-" + selectedNotice.nombrePerro
            val docRef = db.collection("notices").document(documentId)

            docRef.delete()
                .addOnSuccessListener {
                    println("GD Control ---> DELETE: documentId = $documentId")
                    Toast.makeText(this, "¡Anuncio eliminado correctamente!", Toast.LENGTH_SHORT)
                        .show()
                    val principalActivity = Intent(this, PrincipalActivity::class.java)
                    startActivity(principalActivity)
                }
                .addOnFailureListener { e ->
                    println("GD---> Error deleting document, $e")
                    Toast.makeText(
                        this,
                        "¡Ups!... Hubo un problema al eliminar su anuncio.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

        // Pero si clica en "Nombre:" o el nombre del perro abrira el anuncio a pantalla completa:
        rowSelectedName.setOnClickListener {
                selectedNoticeModel.nombrePerro = selectedNotice.nombrePerro
                val noticeActivity = Intent(this, NoticeActivity::class.java)
                startActivity(noticeActivity)
            }

        tableRow.findViewById<TextView>(R.id.myNoticeLabelTV).setOnClickListener {
                selectedNoticeModel.nombrePerro = selectedNotice.nombrePerro
                val noticeActivity = Intent(this, NoticeActivity::class.java)
                startActivity(noticeActivity)
            }

        return tableRow
    }

    // Configuración TableLayout:
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