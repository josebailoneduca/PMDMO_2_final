package com.imagenprogramada.things_swipper

import android.app.Application

class Aplicacion: Application() {
    lateinit var partida: Partida
    lateinit var dificultad: EnumDificultad
    lateinit var skin: EnumSkin
    var jugando=false

    override fun onCreate() {
        super.onCreate()
        dificultad=EnumDificultad.FACIL
        skin=EnumSkin.LLAVES

    }
}