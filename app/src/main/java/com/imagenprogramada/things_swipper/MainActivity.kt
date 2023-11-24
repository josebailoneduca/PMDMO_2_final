package com.imagenprogramada. things_swipper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class MainActivity : AppCompatActivity(){
    lateinit var  aplicacion:Aplicacion
    lateinit var menu:Menu
    lateinit var tableroBotones:Array<Array<ImageButton?>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        aplicacion = application as Aplicacion
        findViewById<Button>(R.id.btnEmpezarPartida).setOnClickListener(View.OnClickListener {  empezarPartida()})
        actualizarRecords()
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_actionbar,menu)
        this.menu= menu!!
        menu?.getItem(0)?.setIcon(aplicacion.skin.imagen)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var id:Int = item.getItemId()
        when (id){
            R.id.imInstrucciones -> mostrarInstrucciones()
            R.id.imConfiguraJuego -> abrirConfiguracion()
            R.id.imSeleccionaSkin -> seleccionarSkin();
            R.id.imNuevoJuego -> empezarPartida()
        }
        return super.onOptionsItemSelected(item)
    }

    public fun empezarPartida() {
        aplicacion.empezarPartida()
        val dificultad : EnumDificultad = aplicacion.getDifucultadPartida()
        val grid = crearGrid(dificultad.filas,dificultad.columnas)
        tableroBotones=Array(dificultad.filas) { fila -> Array(dificultad.columnas){ col-> crearBoton(fila,col,grid) } }
        findViewById<Button>(R.id.btnEmpezarPartida).visibility=View.GONE
        findViewById<TextView>(R.id.txtFin).visibility=View.GONE
        findViewById<LinearLayout>(R.id.panelRecords).visibility=View.GONE
        val cronometro:Chronometer = findViewById<Chronometer>(R.id.cronometro)
            cronometro.base = SystemClock.elapsedRealtime()
            cronometro.start()

     }

    private fun terminarPartida(exito:Boolean){
        val cronometro:Chronometer = findViewById<Chronometer>(R.id.cronometro)
        cronometro.stop()
        val texto:TextView=findViewById<Button>(R.id.txtFin)
        texto.visibility=View.VISIBLE
        findViewById<Button>(R.id.btnEmpezarPartida).visibility=View.VISIBLE
        findViewById<LinearLayout>(R.id.panelRecords).visibility=View.VISIBLE
        if (exito) {
            texto.setText(getString(R.string.ganaste))
            aplicacion.registraResultado(SystemClock.elapsedRealtime() - cronometro.getBase())
        }
        else
            texto.setText(getString(R.string.perdiste))

        actualizarRecords()
    }

    fun actualizarRecords(){
        findViewById<TextView>(R.id.lbRecordFacil).setText(milisegundosToString(aplicacion.mejorResultadoFacil))
        findViewById<TextView>(R.id.lbRecordMedio).setText(milisegundosToString(aplicacion.mejorResultadoMedio))
        findViewById<TextView>(R.id.lbRecordDificil).setText(milisegundosToString(aplicacion.mejorResultadoDificil))
    }
    fun milisegundosToString(milisegundos:Long):String{
        if (milisegundos==0L)
            return "--"
        val duration = milisegundos.toDuration(DurationUnit.MILLISECONDS)
        return duration.toComponents { minutes, seconds, _ ->
                String.format("%02d:%02d", minutes, seconds)
            }
    }

    private fun onLongBotonClicked(v: View?,fila: Int,columna: Int) {
        val boton = tableroBotones[fila][columna]
        boton?.setBackgroundResource(R.drawable.bandera)
        boton?.setBackgroundResource(R.drawable.parrilla)
        val resultado = aplicacion.marcarCelda(fila,columna)
        actualizarEstadoBotones()
        if (resultado?.fracaso==true) {
            val celda = aplicacion.getTableroPartida()[fila][columna]
            if (boton!=null &&celda!=null)
                destapaNumero(boton,celda.adyacentes)
            terminarPartida(false)
        }
        else if (resultado?.victoria==true)
            terminarPartida(true)
    }

    private fun onBotonClicked(v: View?,fila: Int,columna: Int) {
        if (!aplicacion.destaparCelda(fila,columna))
            terminarPartida(false)
        actualizarEstadoBotones()
    }

    private fun actualizarEstadoBotones() {
       val tablero=aplicacion.getTableroPartida()
        if (tablero==null)
            return
        for (f in 0..<tablero.size)
            for (c in 0..<tablero[f].size){
                val celda=tablero[f][c]
                val boton = tableroBotones[f][c]
                //reset antes de actualizacion
                boton?.setImageResource(android.R.color.transparent)
                if (celda?.marcado==true)
                    boton?.setImageResource(R.drawable.bandera)
                else if (celda?.descubierto == true){
                    if (celda?.mina==true)
                        boton?.setImageResource(aplicacion.getSkinPartida().imagen)
                    else {
                        if (boton != null && celda!=null) {
                            destapaNumero(boton,celda.adyacentes)
                        }
                    }
                }
            }
    }

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
        boton?.setImageResource(imagenNumero)
        boton?.setBackgroundResource(R.drawable.parrilla)
    }

    private fun crearBoton(fila: Int,columna: Int,grid: GridLayout): ImageButton {
        //creacion del boton y caracteristicas basicas
        val btn: ImageButton = ImageButton(this)
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
                .toInt();
        params.setMargins(margen, margen, margen, margen)
        //aplicar parametros de layout
        btn.layoutParams = params

        //listeners
        btn.setOnClickListener { v ->
            onBotonClicked(v,fila,columna)
        }
        btn.setOnLongClickListener{ v ->
            onLongBotonClicked(v,fila,columna)
            return@setOnLongClickListener true
        }

        grid.addView(btn)
        return btn
    }


    /**
     * Construye un gridlayout segun los parametros suministrados
     *
     * @param filas Las filas del grid
     * @param columnas Las columnas del grid
     */
    private fun crearGrid(filas: Int, columnas: Int):GridLayout {

        //crear el grid
        var grid = GridLayout(this)
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

    private fun seleccionarSkin() {

        val fm: FragmentManager = supportFragmentManager
       val dialogo:DialogoSeleccionSkin = DialogoSeleccionSkin()
        dialogo.show(fm, "dialogo_skins")
      }

    private fun abrirConfiguracion() {
        val alertDialog:AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialog.setTitle(getString(R.string.configuracion));
        val opciones = arrayOf(
            getString(EnumDificultad.FACIL.texto),
            getString(EnumDificultad.MEDIO.texto),
            getString(EnumDificultad.DIFICIL.texto)
            )
        val seleccionado:Int = aplicacion.dificultad.indice;
        alertDialog.setSingleChoiceItems(opciones,seleccionado){ dialogo,indice ->
            when(indice){
                0->{aplicacion.dificultad=EnumDificultad.FACIL}
                1->{aplicacion.dificultad=EnumDificultad.MEDIO}
                2->{aplicacion.dificultad=EnumDificultad.DIFICIL}
            }

        }
        alertDialog.setPositiveButton("Ok"){_,_->empezarPartida()}
        alertDialog.create();
        alertDialog.show();
    }
    private fun mostrarInstrucciones() {
        val ad = AlertDialog.Builder(this)
        ad.apply {
            setTitle(getString(R.string.instrucciones))
            setMessage(getString(R.string.texto_instruciones))
            setPositiveButton("Ok"){_,_->}
        }.create().show()
    }



}