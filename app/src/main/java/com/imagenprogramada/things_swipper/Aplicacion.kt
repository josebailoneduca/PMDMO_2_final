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

    fun empezarPartida(){
        this.partida = Partida(dificultad,skin)
        this.jugando=true
    }

    fun difucultadPartida():EnumDificultad{
        return partida.dificultad
    }
    fun skinPartida():EnumSkin{
        return partida.skin
    }
}