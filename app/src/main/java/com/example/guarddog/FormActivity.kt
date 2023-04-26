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
        val bundle: Bundle? = intent.extras
        val email: String? = bundle?.getString("email")
        setup(email ?: "No email")

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
        var createNoticeButton = findViewById<Button>(R.id.createNoticeButton)
     //   formEmail.text = email
        formEmail.text = userModel.email



        createButton.setOnClickListener {
            selectedNoticeModel = NoticesModel(
                nombrePerro = formNameDog.text.toString(),
                nombreDueno = formNameOwner.text.toString(),
                zonaDesaparicion = formZone.text.toString(),
                diaDesaparicion = formDate.text.toString(),
                email = formEmail.text.toString(),
                telefono = formTelephone.text.toString(),
                imagenPerro = "ESTO ESTA A PRUEBA",
                observaciones = formDescription.text.toString()
            )

            if (selectedNoticeModel.nombrePerro.isBlank() ||
                selectedNoticeModel.nombreDueno.isBlank() ||
                selectedNoticeModel.zonaDesaparicion.isBlank() ||
                selectedNoticeModel.diaDesaparicion.isBlank() ||
                selectedNoticeModel.email.isBlank() ||
                selectedNoticeModel.telefono.isBlank() ||
                selectedNoticeModel.observaciones.isBlank() ||
                selectedNoticeModel.imagenPerro.isBlank()) {
                // Show error message
                Toast.makeText(this, "¡Todos los campos son obligatorios!", Toast.LENGTH_SHORT).show()
            }  else {
                createNotice(selectedNoticeModel)
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

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¡Ups...!")
        builder.setMessage("Se ha producido un error al crear el usuario.\nRevise su conexión a internet o inténtelo más tarde.")
        builder.setNegativeButton("Continuar") { _, _ ->
            // Acción cuando se hace clic en el botón Aceptar
            val myNoticesActivity = Intent(this, MyNoticesActivity::class.java)
            startActivity(myNoticesActivity)
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}