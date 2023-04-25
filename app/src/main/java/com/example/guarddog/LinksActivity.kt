package com.example.guarddog

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class LinksActivity : AppCompatActivity() {
    val linkList = ArrayList<LinksModel>()

    // Creación/ actualización del usuario en Firestore Data Base
    val db = FirebaseFirestore.getInstance()
    val refLinks = db.collection("links")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_links)
        supportActionBar?.hide()
        FirebaseFirestore.setLoggingEnabled(true)
        FirebaseApp.initializeApp(this)

        // Obtención de anuncios de Firestore
        refLinks.get().addOnSuccessListener { result ->
            for (document in result) {
                var linkModel = LinksModel(
                    web = document.getString("web").toString(),
                    nombre = document.getString("nombre").toString(),
                    descripcion = document.getString("descripcion").toString()
                )
                linkList.add(linkModel)

                println(
                    "GD---> DOCUMENT INFO:\n web = ${linkModel.web} \n nombre = ${linkModel.nombre} \n descripcion = ${linkModel.descripcion}"
                )
            }

            addRowsToTable(linkList)
        }.addOnFailureListener { exception ->
                println("GD---> ERROR REPORT:\n exception = $exception")
            }

        // Setup
        setup()
    }

    private fun setup() {
        val backButton = findViewById<Button>(R.id.backButton)

        backButton.setOnClickListener {
            val principalActivity = Intent(this, PrincipalActivity::class.java)
            startActivity(principalActivity)
        }
    }

    private fun inflateTableRow(web: String, nombre: String, descripcion: String): TableRow {
        val tableRow = LayoutInflater.from(this).inflate(R.layout.link_row_layout, null) as TableRow

        tableRow.findViewById<TextView>(R.id.webRow).text = web
        tableRow.findViewById<TextView>(R.id.nameRow).text = nombre
        tableRow.findViewById<TextView>(R.id.descripcionRow).text = descripcion

        tableRow.findViewById<TextView>(R.id.webRow).setOnClickListener {
            val url = web
            val webView = Intent(Intent.ACTION_VIEW, Uri.parse(url))

            startActivity(webView)
        }
        println("msm: inflateTableRow => web = $web --- nombre = $nombre --- descripcion = $descripcion")

        return tableRow
    }

    private fun addRowsToTable(rowList: List<LinksModel>) {
        val tableLayout = findViewById<TableLayout>(R.id.idTableLayoutLinks)
        for (row in rowList) {
            val tableRow = inflateTableRow(web = row.web, nombre = row.nombre, descripcion = row.descripcion)
            println("msm: addRowsToTable => web = $row.web --- nombre = $row.nombre --- descripcion = $row.descripcion")
            tableLayout.addView(tableRow)
        }
    }

}