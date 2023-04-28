// Guard Dog
// Autor: Martín Sánchez Martínez
// Fecha: 6 de Abril de 2023

package com.example.guarddog

enum class ProviderType() {
    BASIC,
    GOOGLE
}

class NoticesModel(
    var nombrePerro: String,
    var nombreDueno: String,
    var zonaDesaparicion: String,
    var diaDesaparicion: String,
    var email: String,
    var telefono: String,
    var imagenPerro: String,
    var observaciones: String
)

class LinksModel(
    var web: String,
    var descripcion: String,
    var nombre: String
)

class UserModel(
    var email: String,
    var uid: String,
    var provider: ProviderType?
)

// Model del usuario que hace el login:
var userModel = UserModel(
    email = "",
    uid = "",
    provider = null
)

// Model de la noticia que se seleccionado (o creado en la FormActivity):
var selectedNoticeModel = NoticesModel(
    nombrePerro = "",
    nombreDueno = "",
    zonaDesaparicion = "",
    diaDesaparicion = "",
    email = "",
    telefono = "",
    imagenPerro = "",
    observaciones = ""
)