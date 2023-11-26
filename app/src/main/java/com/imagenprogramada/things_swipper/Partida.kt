package com.imagenprogramada.things_swipper

import android.util.Log
import kotlin.random.Random

/**
 * Clase con la logica y datos de una partida
 */
class Partida(val dificultad: EnumDificultad) {

    //tablero con las celdas. La asignacion lo rellena de celdas
    var tablero:Array<Array<Celda?>> = Array(dificultad.filas) { Array(dificultad.columnas){ Celda() } }

     init {
        inicializarTablero()
    }

    /**
     * Inicializa el tablero poniendo las minas y calculando las minas adyacentes
     */
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

    /**
     * Imprime el tablero por consola (para debug)
     */
    private fun imprimeTablero(){
        for (f in 0..< dificultad.filas){
            var fila=""
            for (c in 0..<dificultad.columnas){
                fila += if (tablero[f][c]?.mina==true)
                            "* "
                        else
                            ""+(tablero[f][c]?.adyacentes) + " "
            }
            Log.i("jjbo",fila)
        }
    }

    /**
     * Calcula las minas adyacentes a una celda
     */
    private fun calcularAdyacentes(fila: Int, columna: Int) {
        //contador de minas
        var totalminas=0

        //calculo de rango de celdas a mirar
        val fmin=maxOf(fila-1,0)
        val fmax=minOf(fila+1,dificultad.filas-1)
        val cmin=maxOf(columna-1,0)
        val cmax=minOf(columna+1,dificultad.columnas-1)

        //recorrer el rango de celdas y contar las minas
        for (f in fmin..fmax){
            for (c in cmin..cmax){
                if (!(f==fila && c==columna) && tablero[f][c]?.mina == true)
                    totalminas++
            }
        }
        tablero[fila][columna]?.adyacentes = totalminas
    }

    /**
     * Pone una mina de manera aleatoria en el tablero
     */
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
     * Destapa una celda y devuelve si destaparla permite continuar la partida
     * return True si no hay mina y False si hay mina
     */
    fun destaparCelda(f:Int,c:Int): Boolean {
        val celda = tablero[f][c]
        if (celda?.marcado==true)
            return true
        //Destapar la celda
        destapadoAutomatico(f,c)
        //devolver resultado
        return celda?.mina != true
    }

    /**
     * Destapa una celda y si no tiene minas adyacentes activa el destapado recursivo para destapar
     * todas las celdas que no tienen minas adyacentes que esten conectadas con la celda actual
     * tengan minas adyacentes
     */
    private fun destapadoAutomatico(fila: Int, columna: Int) {
        val celda = tablero[fila][columna]
        celda?.descubierto =true

        //si es mina o tiene adyacentes paramos
        if (celda?.mina ==true || celda?.adyacentes!=0)
            return

        //Si no es mina y no tiene adyacentes destapamos los alrededores
        //calcular rango de alredores
        val fmin=maxOf(fila-1,0)
        val fmax=minOf(fila+1,dificultad.filas-1)
        val cmin=maxOf(columna-1,0)
        val cmax=minOf(columna+1,dificultad.columnas-1)
        //recorrer rango y ordenar que se destape
        for (f in fmin..fmax){
            for (c in cmin..cmax){
                //destapar si no destapado
                if(tablero[f][c]?.descubierto!=true) {
                     destapadoAutomatico(f, c)
                }
            }
        }
    }

    /**
     * Marcar una celda
     * @return Devuelve el resultado de haber marcado la celda que puede suponer que se ha perdido, se ha ganado o nada
     */
    fun marcarCelda(fila:Int,columna:Int):ResultadoPartida{
        val celda = tablero[fila][columna]

        //si la celda estaba descubierta no se marca
        if (celda?.descubierto==true)
            return ResultadoPartida(victoria = false, fracaso = false)

        //marcar celda
        celda?.marcado=true

        //avisar fracaso si no es una mina
        if (celda?.mina!=true)
            return ResultadoPartida(victoria = false, fracaso = true)

        //avisar exito si se han marcado todas las minas
        var minasMarcadas=0
        for (f in 0..< dificultad.filas){
            for (c in 0..<dificultad.columnas){
                val celdaB =tablero[f][c]
                if (celdaB?.mina==true && celdaB.marcado)
                    minasMarcadas++
            }
        }
        //devolver si se ha terminado con exito o la partida continua
        return ResultadoPartida(minasMarcadas==dificultad.minas,false)
    }
}
