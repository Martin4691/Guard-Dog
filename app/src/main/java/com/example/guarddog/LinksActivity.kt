package com.example.guarddog

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class LinksActivity : AppCompatActivity() {
    val linkList = ArrayList<LinksModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_links)
        supportActionBar?.hide()
        FirebaseFirestore.setLoggingEnabled(true)
        FirebaseApp.initializeApp(this)

        // Creación/ actualización del usuario en Firestore Data Base
        val db = FirebaseFirestore.getInstance()
        val refLinks = db.collection("links")
        var linkModel = LinksModel(
            web = "",
            descripcion = "",
            nombre = ""
        )

        // Obtención de anuncios de Firestore
        refLinks.get().addOnSuccessListener { result ->
            for (document in result) {
                linkModel.web = document.getString("web").toString()
                linkModel.nombre = document.getString("nombre").toString()
                linkModel.descripcion = document.getString("descripcion").toString()

                println(
                    "GD---> DOCUMENT INFO:\n web = ${linkModel.web} \n nombre = ${linkModel.nombre} \n descripcion = ${linkModel.descripcion}"
                )
            }
        }
            .addOnFailureListener { exception ->
                println("GD---> ERROR REPORT:\n exception = $exception")
            }

        // Setup
        setup()
    }

    private fun setup() {
        val backButton = findViewById<Button>(R.id.backButton)

        backButton.setOnClickListener {
            val PrincipalActivity = Intent(this, PrincipalActivity::class.java)
            startActivity(PrincipalActivity)
        }

        addRowsToTable(linkList)
    }

    private fun inflateTableRow(web: String, nombre: String, descripcion: String): TableRow {
        val tableRow = LayoutInflater.from(this).inflate(R.layout.link_row_layout, null) as TableRow

        tableRow.findViewById<TextView>(R.id.webRow).text = web
        tableRow.findViewById<TextView>(R.id.nameRow).text = nombre
        tableRow.findViewById<TextView>(R.id.descripcionRow).text = descripcion

        return tableRow
    }

    private fun addRowsToTable(dataList: List<LinksModel>) {
        val tableLayout = findViewById<TableLayout>(R.id.idTableLayout)
        for (data in dataList) {
            val tableRow = inflateTableRow(web = data.web, nombre = data.nombre, descripcion = data.descripcion)
            tableLayout.addView(tableRow)
        }
    }

}