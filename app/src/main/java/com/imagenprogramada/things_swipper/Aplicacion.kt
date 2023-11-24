package com.imagenprogramada.things_swipper

import android.app.Application
import kotlin.math.min

class Aplicacion: Application() {
    lateinit var partida: Partida
    lateinit var dificultad: EnumDificultad
    lateinit var skin: EnumSkin
    var jugando=false
    var mejorResultadoFacil:Long=0
    var mejorResultadoMedio:Long=0
    var mejorResultadoDificil:Long=0
    override fun onCreate() {
        super.onCreate()
        dificultad=EnumDificultad.FACIL
        skin=EnumSkin.LLAVES
    }

    fun empezarPartida(){
        this.partida = Partida(dificultad,skin)
        this.jugando=true
    }

    fun destaparCelda(fila:Int, columna:Int):Boolean
    {
        if (!jugando)
            return true;
        val resultado=partida.destaparCelda(fila,columna)
        jugando=resultado
        return resultado
    }

    fun getDifucultadPartida():EnumDificultad{
        return partida.dificultad
    }
    fun getSkinPartida():EnumSkin{
        return partida.skin
    }

    fun getTableroPartida(): Array<Array<Celda?>> {
        return partida.tablero
    }

    fun marcarCelda(fila: Int, columna: Int): ResultadoPartida? {
        if (!jugando)
            return null;
        val resultado=partida.marcarCelda(fila,columna)
        jugando=!(resultado.fracaso||resultado.victoria)
        return resultado
    }

    fun registraResultado(resultado: Long) {
        var mejorResultado = when(partida.dificultad){
            EnumDificultad.FACIL->mejorResultadoFacil
            EnumDificultad.MEDIO->mejorResultadoMedio
            EnumDificultad.DIFICIL->mejorResultadoDificil
        }

        if (mejorResultado== 0L)
            mejorResultado = resultado
        else
            mejorResultado= min(mejorResultado,resultado)

        when(partida.dificultad){
            EnumDificultad.FACIL->mejorResultadoFacil=mejorResultado
            EnumDificultad.MEDIO->mejorResultadoMedio=mejorResultado
            EnumDificultad.DIFICIL->mejorResultadoDificil=mejorResultado
        }
    }
}