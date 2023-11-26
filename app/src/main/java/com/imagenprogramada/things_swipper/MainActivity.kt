package com.imagenprogramada. things_swipper

import android.os.Bundle
import android.os.SystemClock
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Chronometer
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class MainActivity : AppCompatActivity(){
    //referencia a la aplicacion
    private lateinit var  aplicacion:Aplicacion
    //referencia al menu superior
    lateinit var menu:Menu
    //tabla donde se almacenan los botones de la interfaz para la partida
    private lateinit var tableroBotones:Array<Array<ImageButton?>>


    /**
     * OnCreate. inicializa listener de boton de jugar y actualiza los records en la vista
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        aplicacion = application as Aplicacion
        findViewById<Button>(R.id.btnEmpezarPartida).setOnClickListener {empezarPartida()}
        actualizarRecords()
    }



    /**
     * OnCreateOptionsMenu. Configura el menu de opciones de la aplicacion
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_actionbar,menu)
        this.menu= menu!!//doble exclamacion para asegurar que no es null
        //actualizar imagen de skin seleccionada en la barra
        menu.getItem(0)?.setIcon(aplicacion.skin.imagen)
        return true
    }


    /**
     * Gestion del item de menu clickado
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.imInstrucciones -> mostrarInstrucciones()
            R.id.imConfiguraJuego -> abrirConfiguracion()
            R.id.imSeleccionaSkin -> seleccionarSkin()
            R.id.imNuevoJuego -> empezarPartida()
        }
        return true
    }

    /**
     * Empieza la partida. Ordea a aplicacion crear nueva partida y actualiza la vista acorde a la
     * partida crada
     */
     fun empezarPartida() {
        //empezar partida


        //crear grid
        val dificultad : EnumDificultad = aplicacion.empezarPartida()
        val grid = crearGrid(dificultad.filas,dificultad.columnas)

        //crear botones y guardar referencia
        tableroBotones=Array(dificultad.filas) { fila -> Array(dificultad.columnas){ col-> crearBoton(fila,col,grid) } }

        //ocultar boton de empezar partida, records y mensaje de victoria/derrota
        findViewById<Button>(R.id.btnEmpezarPartida).visibility=View.GONE
        findViewById<TextView>(R.id.txtFin).visibility=View.GONE
        findViewById<LinearLayout>(R.id.panelRecords).visibility=View.GONE

        //iniciar tiempo de la partida
        val cronometro = findViewById<Chronometer>(R.id.cronometro)
            cronometro.base = SystemClock.elapsedRealtime()
            cronometro.start()
     }

    /**
     * Acciones tras terminar la partida
     * @param exito True si se ha ganado la partida, False si se ha perdido
     */
    private fun terminarPartida(exito:Boolean){
        //parar cronometro
        val cronometro = findViewById<Chronometer>(R.id.cronometro)
        cronometro.stop()
        //mostrar records, boton de nueva partida y texto de exito/derrota
        val texto:TextView=findViewById<Button>(R.id.txtFin)
        texto.visibility=View.VISIBLE
        findViewById<Button>(R.id.btnEmpezarPartida).visibility=View.VISIBLE
        findViewById<LinearLayout>(R.id.panelRecords).visibility=View.VISIBLE
        //configurar el texto de exito/derrota y registrar el nuevo tiempo
        if (exito) {
            texto.text = getString(R.string.ganaste)
            aplicacion.registraResultado(SystemClock.elapsedRealtime() - cronometro.base)
            actualizarRecords()
        }
        else
            texto.text = getString(R.string.perdiste)
    }


    /**
     * Gestion de pulsacion larga. Ordena a la aplicacion marcar una casilla y actualiza la vista
     * segun el resultado
     */
    private fun marcarCelda(fila: Int, columna: Int) {
        //recoger resultado del marcado o volver si es nulo
        val resultado = aplicacion.marcarCelda(fila,columna) ?: return


        //actualizar iconos de boton
        actualizarEstadoBotones()

        //comprobar si ha terminado la partida y mostrar contenido de la casilla si toca
        val boton = tableroBotones[fila][columna]
        if (resultado.fracaso) {
            val celda = aplicacion.getTableroPartida()[fila][columna]
            if (boton!=null &&celda!=null)
                destapaNumero(boton,celda.adyacentes)
            terminarPartida(false)
        }
        else if (resultado.victoria)
            terminarPartida(true)
    }

    /**
     * Gestion de click corto. Ordena a la partida destapar una celda y destapa su contenido
     * Comprueba si el destapado implica la perdida de la partida
     */
    private fun destaparCelda(fila: Int, columna: Int) {
        if (!aplicacion.destaparCelda(fila,columna))
            terminarPartida(false)
        actualizarEstadoBotones()
    }

    /**
     * Actualiza los iconos de los botones segun el estado del tablero
     */
    private fun actualizarEstadoBotones() {
       val tablero=aplicacion.getTableroPartida()
        //recorrer el tablero
        for (f in tablero.indices)
            for (c in 0..<tablero[f].size){
                val celda=tablero[f][c]
                val boton = tableroBotones[f][c]
                //reset antes de actualizacion de imagenes de los botones
                boton?.setImageResource(android.R.color.transparent)
                //bandera para marcados
                if (celda?.marcado==true)
                    boton?.setImageResource(R.drawable.bandera)
                //gestionar cesdas descubiertas
                else if (celda?.descubierto == true){
                    //pinitado de minas de descubiertos
                    if (celda.mina)
                        boton?.setImageResource(aplicacion.skin.imagen)
                    else {
                        //pintado de numero de descubiertos
                        if (boton != null )  destapaNumero(boton,celda.adyacentes)
                    }
                }
            }
    }

    /**
     * Destapa una casilla mostrando el numero de minas adyacentes de la misma
     */
    private fun destapaNumero(boton:ImageButton,numero:Int){
        val imagenNumero:Int = when (numero) {
            1 -> R.drawable.n1
            2 -> R.drawable.n2
            3 -> R.drawable.n3
            4 -> R.drawable.n4
            5 -> R.drawable.n5
            6 -> R.drawable.n6
            7 -> R.drawable.n7
            8 -> R.drawable.n8
            else -> {R.drawable.n0}
        }
        boton.setImageResource(imagenNumero)
        boton.setBackgroundResource(R.drawable.parrilla)
    }

    /**
     * Crea un boton y lo introduce en el grid
     * @param fila La fila en la qu poner el boton
     * @param columna La columna en la que poner el boton
     * @param grid Grid en el que poner el boton
     * @return el boton creado
     * @return el boton creado
     */
    private fun crearBoton(fila: Int,columna: Int,grid: GridLayout):ImageButton {
        //creacion del boton y caracteristicas basicas
        val btn = ImageButton(this)
        btn.id = View.generateViewId()
        btn.scaleType=ImageView.ScaleType.FIT_CENTER
        //Definicion de los parametros de layout
        // fila, columna y weight para los Gridlayout params
        val params: GridLayout.LayoutParams =
            GridLayout.LayoutParams(GridLayout.spec(fila, 1, 1f), GridLayout.spec(columna, 1, 1f))
        params.height = 0
        params.width = 0
        //margen
        val margen: Int =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.1f, resources.displayMetrics)
                .toInt()
        params.setMargins(margen, margen, margen, margen)
        //aplicar parametros de layout
        btn.layoutParams = params

        //listeners
        btn.setOnClickListener { v ->
            destaparCelda(fila,columna)
        }
        btn.setOnLongClickListener{ v ->
            marcarCelda(fila,columna)
            return@setOnLongClickListener true
        }
        //poner el boton en el grid
        grid.addView(btn)
        return btn
    }


    /**
     * Construye un gridlayout segun los parametros suministrados y lo pone en el contenedor
     * de la vista
     *
     * @param filas Las filas del grid
     * @param columnas Las columnas del grid
     */
    private fun crearGrid(filas: Int, columnas: Int):GridLayout {

        //crear el grid
        val grid = GridLayout(this)
        grid.columnCount = columnas
        grid.rowCount = filas
        grid.id = View.generateViewId()

        //parametros de layout
        val params = GridLayout.LayoutParams()
        params.width = GridLayout.LayoutParams.MATCH_PARENT
        params.height = GridLayout.LayoutParams.MATCH_PARENT
        grid.layoutParams = params

        //recoge la vista contenedor donde se colocara el grid
        val contenedor = findViewById<ConstraintLayout>(R.id.contenedor)
        //limpiar contenedor
        contenedor.removeAllViews()
        //agregar grid al contenedor
        contenedor.addView(grid)
        return grid
    }

    /**
     * Inicia la seleccion de skin generando un dialogo de DialogoSeleccionSkin
     */
    private fun seleccionarSkin() {
        val fm: FragmentManager = supportFragmentManager
        val dialogo = DialogoSeleccionSkin()
        dialogo.show(fm, "dialogo_skins")
      }

    /**
     * Abre el dialogo de configuracion de dificultad
     */
    private fun abrirConfiguracion() {
        val alertDialog:AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialog.setTitle(getString(R.string.configuracion))
        //opciones entre la que elegir
        val opciones = arrayOf(
            getString(EnumDificultad.FACIL.texto),
            getString(EnumDificultad.MEDIO.texto),
            getString(EnumDificultad.DIFICIL.texto)
            )
        //opcion seleccionada actualmente
        val seleccionado:Int = aplicacion.dificultad.indice

        //generar dialgo
        alertDialog.setSingleChoiceItems(opciones,seleccionado){ dialogo,indice ->
            when(indice){
                0->{aplicacion.dificultad=EnumDificultad.FACIL}
                1->{aplicacion.dificultad=EnumDificultad.MEDIO}
                2->{aplicacion.dificultad=EnumDificultad.DIFICIL}
            }

        }
        //agregar boton de cerrar el dialogo. Si hay partida empezada la reinicia
        alertDialog.setPositiveButton("Ok"){_,_->
            if(aplicacion.jugando)
                empezarPartida()
        }
        //crear y mostrar el dialogo
        alertDialog.create().show()
    }

    /**
     * Muestra el dialogo de instrucciones
     */
    private fun mostrarInstrucciones() {
        val ad = AlertDialog.Builder(this)
        ad.apply {
            setTitle(getString(R.string.instrucciones))
            setMessage(getString(R.string.texto_instruciones))
            setPositiveButton("Ok"){_,_->}
        }.create().show()
    }


    /**
     * Actualiza la vista con los datos de los records
     */
    private fun actualizarRecords(){
        findViewById<TextView>(R.id.lbRecordFacil).text = milisegundosToString(aplicacion.mejorResultadoFacil)
        findViewById<TextView>(R.id.lbRecordMedio).text = milisegundosToString(aplicacion.mejorResultadoMedio)
        findViewById<TextView>(R.id.lbRecordDificil).text=milisegundosToString(aplicacion.mejorResultadoDificil)
    }


    /**
     * Convierte un long en un string mm:ss o "--" doble guión si el long es 0
     */
    private fun milisegundosToString(milisegundos:Long):String{
        if (milisegundos==0L)
            return "--"
        val duration = milisegundos.toDuration(DurationUnit.MILLISECONDS)
        return duration.toComponents { minutes, seconds, _ ->
            String.format("%02d:%02d", minutes, seconds)
        }
    }
}