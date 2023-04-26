package com.example.guarddog

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class NoticeActivity : AppCompatActivity() {
    var imagenPerro = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice)
        supportActionBar?.hide()


        // Setup
        val bundle: Bundle? = intent.extras
        val noticeEmail: String? = bundle?.getString("noticeEmail")
        val noticeDogName: String? = bundle?.getString("noticeDogName")
        setup(noticeEmail ?: "No email", noticeDogName ?: "No dogName")

    }


    private fun setup(noticeEmail: String, noticeDogName: String) {
        println("msm: noticeEmail = $noticeEmail, noticeDogName = $noticeDogName")
        var dogImage = findViewById<ImageView>(R.id.dogNoticeImageView)
        val db = FirebaseFirestore.getInstance()

        db.collection("notices")
            .whereEqualTo("email", noticeEmail)
            .whereEqualTo("nombrePerro", noticeDogName)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    findViewById<TextView>(R.id.noticeDogNameTextView).text = document.getString("nombrePerro").toString()
                    findViewById<TextView>(R.id.noticeOwnerTextView).text = document.getString("nombreDueno").toString()
                    findViewById<TextView>(R.id.noticeDateTextView).text = document.getString("diaDesaparicion").toString()
                    findViewById<TextView>(R.id.noticeZoneTextView).text = document.getString("zonaDesaparicion").toString()
                    findViewById<TextView>(R.id.noticeTelTextView).text = document.getString("telefono").toString()
                    findViewById<TextView>(R.id.noticeEmailTextView).text = document.getString("email").toString()
                    findViewById<TextView>(R.id.noticeDescriptionTextView).text = document.getString("observaciones").toString()
                    imagenPerro = document.getString("imagen").toString()

                    findViewById<TextView>(R.id.noticeZoneTextView).setOnClickListener {
                        val url = "https://www.google.com/maps/search/?api=1&query=" + document.getString("zonaDesaparicion").toString()
                        val webView = Intent(Intent.ACTION_VIEW, Uri.parse(url))

                        startActivity(webView)
                    }
                }

                try {
                    if(imagenPerro != null) {
                        Picasso.get().load(imagenPerro).fit().into(dogImage)
                    } else {
                        println("GD---> ERROR REPORT: imagenPerro está llegando nulo")
                    }
                } catch (e: Exception) {
                    println("GD---> ERROR REPORT: ${e}")
                }

            }.addOnFailureListener { exception ->
                println("GD---> ERROR REPORT: $exception")
            }

    }



}