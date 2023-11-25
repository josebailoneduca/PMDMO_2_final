package com.imagenprogramada.things_swipper

/**
 * Almacena un estado de la partida. Usado tras marcaciones
 * Si se ha ganado victoria=True
 * Si se ha peridod fracaso=True
 * si no se ha ganado ni perdido ambas a False
 */
class ResultadoPartida(
    val victoria:Boolean,
    val fracaso:Boolean)