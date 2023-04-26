package com.example.guarddog

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
    var uid: String
)

// Model del usuario que hace el login:
var userModel = UserModel(
    email = "",
    uid = ""
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