// Guard Dog
// Autor: Martín Sánchez Martínez
// Fecha: 6 de Abril de 2023

package com.example.guarddog

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class NoticeActivity : AppCompatActivity() {
    var imagenPerro = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice)
        supportActionBar?.hide()

        setup(userModel.email ?: "No email", selectedNoticeModel.nombrePerro ?: "No dogName")
    }


    private fun setup(noticeEmail: String, noticeDogName: String) {
        println("GD Control---> Notice setup params => Email = $noticeEmail, noticeDogName = $noticeDogName")
        var dogImage = findViewById<ImageView>(R.id.dogNoticeImageView)
        val db = FirebaseFirestore.getInstance()

        db.collection("notices")
            .whereEqualTo("email", noticeEmail)
            .whereEqualTo("nombrePerro", noticeDogName)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    findViewById<TextView>(R.id.noticeDogNameTextView).text =
                        document.getString("nombrePerro").toString()
                    findViewById<TextView>(R.id.noticeOwnerTextView).text =
                        document.getString("nombreDueno").toString()
                    findViewById<TextView>(R.id.noticeDateTextView).text =
                        document.getString("diaDesaparicion").toString()
                    findViewById<TextView>(R.id.noticeZoneTextView).text =
                        document.getString("zonaDesaparicion").toString()
                    findViewById<TextView>(R.id.noticeTelTextView).text =
                        document.getString("telefono").toString()
                    findViewById<TextView>(R.id.noticeEmailTextView).text =
                        document.getString("email").toString()
                    findViewById<TextView>(R.id.noticeDescriptionTextView).text =
                        document.getString("observaciones").toString()
                    imagenPerro = document.getString("imagen").toString()

                    // Con esta configuración abrira Google Maps justo en la zona que indique el anuncio:
                    findViewById<TextView>(R.id.noticeZoneTextView).setOnClickListener {
                        val url =
                            "https://www.google.com/maps/search/?api=1&query=" + document.getString(
                                "zonaDesaparicion"
                            ).toString()
                        val webView = Intent(Intent.ACTION_VIEW, Uri.parse(url))

                        startActivity(webView)
                    }
                }

                // Configuración de la imagen:
                try {
                    if (imagenPerro != null) {
                        Picasso.get().load(imagenPerro).fit().into(dogImage)
                    } else {
                        println("GD---> ERROR REPORT: imagenPerro está llegando nulo")
                        Toast.makeText(
                            this,
                            "¡Ups... ha habido algún problema con la imagen!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    println("GD---> ERROR REPORT: ${e}")
                    Toast.makeText(
                        this,
                        "¡Ups... ha habido algún problema con la imagen!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }.addOnFailureListener { exception ->
                println("GD---> ERROR REPORT: $exception")
                Toast.makeText(
                    this,
                    "¡Ups... ha habido algún problema al cargar tu anuncio!",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}