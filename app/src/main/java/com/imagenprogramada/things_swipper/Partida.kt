package com.imagenprogramada.things_swipper

import android.util.Log
import com.imagenprogramada.things_swipper.ResultadoPartida
import com.imagenprogramada.things_swipper.Celda
import com.imagenprogramada.things_swipper.EnumDificultad
import com.imagenprogramada.things_swipper.EnumSkin


import kotlin.random.Random

class Partida(val dificultad: EnumDificultad, val skin: EnumSkin) {
    lateinit var tablero:Array<Array<Celda?>>

    init {
        tablero=Array(dificultad.filas) { Array(dificultad.columnas){col-> Celda() } }
        inicializarTablero()
    }

    private fun inicializarTablero() {
        //poner minas
        for (i in 1..dificultad.minas) {
            ponerMina()
        }
        imprimeTablero()
        //calcular adyacentes
        for (f in 0..< dificultad.filas){
            for (c in 0..<dificultad.columnas){
                if(tablero[f][c]?.mina!=true) {
                    calcularAdyacentes(f, c)
                    imprimeTablero()
                }
            }
        }



    }

    private fun imprimeTablero(){
        for (f in 0..< dificultad.filas){
            var fila=""
            for (c in 0..<dificultad.columnas){
                if (tablero[f][c]?.mina==true)
                    fila+="* "
                else
                    fila+=""+(tablero[f][c]?.adyacentes) + " "
            }
            Log.i("jjbo",fila)
        }
    }
    private fun calcularAdyacentes(fila: Int, columna: Int) {
            var totalminas=0;
        val fmin=maxOf(fila-1,0)
        val fmax=minOf(fila+1,dificultad.filas-1)
        val cmin=maxOf(columna-1,0)
        val cmax=minOf(columna+1,dificultad.columnas-1)

        for (f in fmin..fmax){
            for (c in cmin..cmax){
                if (!(f==fila && c==columna) && tablero[f][c]?.mina == true)
                    totalminas++
            }
        }
        tablero[fila][columna]?.adyacentes = totalminas
    }

    private fun ponerMina() {
        var puesta=false
        while (!puesta) {
            val fila = Random.nextInt(dificultad.filas)
            val columna = Random.nextInt(dificultad.columnas)
            if (tablero[fila][columna]?.mina != true) {
                tablero[fila][columna]?.mina = true
                puesta=true
            }
        }
    }

    /**
     * Devuelve si destapar la celda permite continuar la partida
     * Si hay mina devuelve false
     * Si no hay mina devuelve true
     */
    fun destaparCelda(f:Int,c:Int): Boolean {
        val celda = tablero[f][c]
        if (celda?.marcado==true)
            return true
        celda?.descubierto =true

        //si no hay mina y no hay minas adyacentes destapamos automaticamente los alrededores
        if (celda?.mina ==false && celda.adyacentes==0){
            destaparAutomatico(f,c)
        }
        //devolver resultado
        return celda?.mina != true
    }

    private fun destaparAutomatico(fila: Int, columna: Int) {
        //destapamos los alrededores
        val fmin=maxOf(fila-1,0)
        val fmax=minOf(fila+1,dificultad.filas-1)
        val cmin=maxOf(columna-1,0)
        val cmax=minOf(columna+1,dificultad.columnas-1)

        for (f in fmin..fmax){
            for (c in cmin..cmax){
                //llamada recursiva si es 0
                if(tablero[f][c]?.descubierto!=true) {
                    tablero[f][c]?.descubierto = true
                    if (tablero[f][c]?.adyacentes == 0)
                        destaparAutomatico(f, c)
                }
            }
        }
    }

    fun marcarCelda(f:Int,c:Int):ResultadoPartida{
        val celda = tablero[f][c]
        celda?.marcado=true
        //avisar fracaso si no es una mina
        if (celda?.mina!=true)
            return ResultadoPartida(false,true)
        //avisar exito si se han marcado todas las minas
        var minasMarcadas=0
        for (f in 0..< dificultad.filas){
            for (c in 0..<dificultad.columnas){
                val celda =tablero[f][c]
                if (celda?.mina==true && celda?.marcado==true)
                    minasMarcadas++
            }
        }
        //devolver si se ha terminado con exito
        return ResultadoPartida(minasMarcadas==dificultad.minas,false)
    }
}
