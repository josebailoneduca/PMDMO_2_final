package com.imagenprogramada.things_swipper

import android.app.Application
import kotlin.math.min

/**
 * Clase de aplicacion. Mantienene el estado de la aplicación respecto a la partida, la dificultad
 * el skin usado para nuevas partidas, si se está jugando y los records.
 * Sirve de enlace entre la vista y la partida en si que se guarda en uno de sus atributos.
 */
class Aplicacion: Application() {
    /**
     * Partida en juego
     */
    private lateinit var partida: Partida

    /**
     * Dificultad selecconada
     */
    lateinit var dificultad: EnumDificultad

    /**
     * Skin seleccionado
     */
    lateinit var skin: EnumSkin

    /**
     * True si se esta jugando, false si no hay partida en curso
     */
    var jugando=false

    /**
     * Registro de los mejores tiempos para cada dificultad
     */
    var mejorResultadoFacil:Long=0
    var mejorResultadoMedio:Long=0
    var mejorResultadoDificil:Long=0


    /**
     * OnCreate. Inicializa dificultad y skin
     */
    override fun onCreate() {
        super.onCreate()
        dificultad=EnumDificultad.FACIL
        skin=EnumSkin.LLAVES
    }


    /**
     * Empieza una nueva partida
     */
    fun empezarPartida():EnumDificultad{
        this.partida = Partida(dificultad)
        this.jugando=true
        return dificultad
    }

    /**
     * Devuelve el trablero de la partida
     */
     fun getTableroPartida(): Array<Array<Celda?>> {
        return partida.tablero
    }

    /**
     * Ordena a la partida marcar una celda. Devuelve el resultado de haberla marcado
     */
    fun marcarCelda(fila: Int, columna: Int): ResultadoPartida? {
        //ignorar si no se esta jugando
        if (!jugando)
            return null
        //marcar y recoger el resultado de haber marcado
        val resultado=partida.marcarCelda(fila,columna)
        //si no es fracaso ni victoria seguimos jugando
        jugando=!(resultado.fracaso||resultado.victoria)
        //devolver el resultado
        return resultado
    }

    /**
     * Ordena a la partida destapar una celda si se esta jugando
     * @return True si no supone perder la partida. False si supone perder la partida
     */
    fun destaparCelda(fila:Int, columna:Int):Boolean
    {
        //si no se esta jugando termina
        if (!jugando)
            return true
        //recoger resultado de destapar la celda
        val resultado=partida.destaparCelda(fila,columna)
        //actualizar el estado de jugando segun el resultado(false supone terminar y por tanto no se estara jugando)
        jugando=resultado
        //devolver el resultado
        return resultado
    }



    /**
     * registra un resultado de partida
     */
    fun registraResultado(resultado: Long) {
        //seleccionar el mejor resultado segun dificultad
        var mejorResultado = when(partida.dificultad){
            EnumDificultad.FACIL->mejorResultadoFacil
            EnumDificultad.MEDIO->mejorResultadoMedio
            EnumDificultad.DIFICIL->mejorResultadoDificil
        }

        //si no hay reultado almacenamos el que  han pasado
        mejorResultado = if (mejorResultado== 0L)
                            resultado
                        else
                            //en caso contrario almacenamos el menor(mejor tiempo)
                            min(mejorResultado,resultado)

        //almacenar el mejor resultado
        when(partida.dificultad){
            EnumDificultad.FACIL->mejorResultadoFacil=mejorResultado
            EnumDificultad.MEDIO->mejorResultadoMedio=mejorResultado
            EnumDificultad.DIFICIL->mejorResultadoDificil=mejorResultado
        }
    }
}