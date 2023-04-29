// Guard Dog
// Autor: Martín Sánchez Martínez
// Fecha: 6 de Abril de 2023

package com.example.guarddog

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.File

class FormActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_CODE = 1
        private const val REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 11
    }

    val storage = FirebaseStorage.getInstance()
    var storageRef = storage.reference
    private lateinit var imageUri: Uri
    var imageUriToString: String = ""
    var imageUriPath = File("")
    var downloadURL = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)
        supportActionBar?.hide()

        // Setup:
        setup(userModel.email)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        }

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
            // Creación de modelo de anuncio a publicar:
            var newNotice = NoticesModel(
                nombrePerro = formNameDog.text.toString(),
                nombreDueno = formNameOwner.text.toString(),
                zonaDesaparicion = formZone.text.toString(),
                diaDesaparicion = formDate.text.toString(),
                email = formEmail.text.toString(),
                telefono = formTelephone.text.toString(),
                imagenPerro = downloadURL,
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
                newNotice.imagenPerro.isBlank()
            ) {
                // Aviso sí falta algún campo por rellenar:
                Toast.makeText(this, "¡Todos los campos son obligatorios!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                // Llamada al método de ceración:
                createNotice(newNotice)
            }
        }

        // Boton volver:
        findViewById<Button>(R.id.botonVolver).setOnClickListener {
            val misAnuncios = Intent(this, MyNoticesActivity::class.java)
            startActivity(misAnuncios)
        }

        // Boton buscar imagen:
        findViewById<Button>(R.id.bookImageButton).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE)
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
            "imagen" to noticeToSave.imagenPerro,
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var imageView = findViewById<ImageView>(R.id.photoImageView)

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            imageUri = data.data!!
            imageUriToString = imageUri.toString()
            imageUriPath = File(imageUri?.path ?: "")

            Picasso.get().load(imageUri).into(imageView)

            uploadImage()
        }
    }

    private fun uploadImage() {
        var file = Uri.fromFile(File(imageUriPath.toString()))
        val photoRef = storageRef.child("images/${file.lastPathSegment}")
        var uploadTask = photoRef.putFile(imageUri)

        uploadTask.addOnFailureListener {
            println("GD Control---> ERROR REPORT FormsActivity: Problemas al subir la imagen en uploadImage()")
        }.addOnSuccessListener { taskSnapshot ->
            taskSnapshot.metadata.toString()
            println("GD Control---> taskSnapshot.metadata = ${taskSnapshot.metadata.toString()}")
        }

        val urlTask = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            photoRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                downloadURL = task.result.toString()
                println("GD Control---> URL = $downloadURL")
            } else {
                println("GD Control---> ERROR REPORT FormsActivity: Problemas al intentar conseguir la URL en uploadImage()")
            }
        }
    }

    // Maneja la respuesta del usuario a través del método onRequestPermissionsResult()
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION -> {
                // Si el usuario otorgó el permiso, realiza la acción necesaria
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    println("GD Control---> Permiso otorgado.")
                } else {
                    println("GD Control---> Permiso NO otorgado.")
                    Toast.makeText(this, "Necesitamos tu consentimiento para ver tus imagenes y poder publicarlas en nuestra comunidad.", Toast.LENGTH_SHORT)
                    val misAnuncios = Intent(this, MyNoticesActivity::class.java)
                    startActivity(misAnuncios)
                }
                return
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}