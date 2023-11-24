package com.imagenprogramada.things_swipper

enum class EnumDificultad (val filas:Int, val columnas:Int, val minas:Int, val texto:Int, val indice:Int){
        FACIL(5,3,2, R.string.facil,0),
        MEDIO(8,6, 5, R.string.medio,1),
        DIFICIL(12,8, 10, R.string.dificil,2),
}
