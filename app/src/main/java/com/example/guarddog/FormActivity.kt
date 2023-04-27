package com.example.guarddog

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FormActivity : AppCompatActivity() {
    //   var formImage = findViewById<>(R.id.)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)
        supportActionBar?.hide()

        // Setup:
        setup(userModel.email)

    }


    private fun setup(email: String) {
        val createButton = findViewById<Button>(R.id.createNoticeButton)
        var formNameDog = findViewById<EditText>(R.id.editTextDogName)
        var formNameOwner = findViewById<EditText>(R.id.editTextOwnerName)
        var formZone = findViewById<EditText>(R.id.editTextZone)
        var formTelephone = findViewById<EditText>(R.id.editTextTelephone)
        var formDate = findViewById<EditText>(R.id.editTextDate)
        var formEmail = findViewById<TextView>(R.id.labelEmailLogInTV)
        var formDescription = findViewById<EditText>(R.id.editTextDescription)
        formEmail.text = email

        createButton.setOnClickListener {
            // Creación de modelo de noticia:
            var newNotice = NoticesModel(
                nombrePerro = formNameDog.text.toString(),
                nombreDueno = formNameOwner.text.toString(),
                zonaDesaparicion = formZone.text.toString(),
                diaDesaparicion = formDate.text.toString(),
                email = formEmail.text.toString(),
                telefono = formTelephone.text.toString(),
                imagenPerro = "No image", //msm: TO DO: meter la imagen aqui
                observaciones = formDescription.text.toString()
            )

            // Comprobación que no falte nada por rellenar:
            if (newNotice.nombrePerro.isBlank() ||
                newNotice.nombreDueno.isBlank() ||
                newNotice.zonaDesaparicion.isBlank() ||
                newNotice.diaDesaparicion.isBlank() ||
                newNotice.email.isBlank() ||
                newNotice.telefono.isBlank() ||
                newNotice.observaciones.isBlank() ||
                newNotice.imagenPerro.isBlank()) {
                // Aviso sí falta algún campo por rellenar:
                Toast.makeText(this, "¡Todos los campos son obligatorios!", Toast.LENGTH_SHORT).show()
            }  else {
                // Llamada al método de ceración:
                createNotice(newNotice)
            }
        }
    }

    private fun createNotice(noticeToSave: NoticesModel) {
        val db = Firebase.firestore
        val noticeMap = hashMapOf(
            "nombrePerro" to noticeToSave.nombrePerro,
            "nombreDueno" to noticeToSave.nombreDueno,
            "zonaDesaparicion" to noticeToSave.zonaDesaparicion,
            "diaDesaparicion" to noticeToSave.diaDesaparicion,
            "email" to noticeToSave.email,
            "telefono" to noticeToSave.telefono,
            "imagenPerro" to noticeToSave.imagenPerro,
            "observaciones" to noticeToSave.observaciones
        )

        // Creación de la noticia en Firebase con id personalizado:
        db.collection("notices")
            .document(userModel.email + "-" + noticeToSave.nombrePerro)
            .set(noticeMap)
            .addOnSuccessListener { documentReference ->
                val myNoticesActivity = Intent(this, MyNoticesActivity::class.java)
                startActivity(myNoticesActivity)
            }
            .addOnFailureListener { e ->
                showAlert()
            }
    }

    // Creación de un AlertDialog que avise de problemas para crear el usuario:
    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¡Ups...!")
        builder.setMessage("Se ha producido un error al crear el anuncio.\nRevise su conexión a internet o inténtelo más tarde.")
        builder.setNegativeButton("Continuar") { _, _ ->
            // Acción de volver a "mis anuncios" cuando se hace click en el botón Aceptar:
            val myNoticesActivity = Intent(this, MyNoticesActivity::class.java)
            startActivity(myNoticesActivity)
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}