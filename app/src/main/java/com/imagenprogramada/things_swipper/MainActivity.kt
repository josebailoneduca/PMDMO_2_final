package com.imagenprogramada.things_swipper

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager

class MainActivity : AppCompatActivity(){
    lateinit var  aplicacion:Aplicacion
    lateinit var menu:Menu

    lateinit var tablero:Array<Array<ImageButton?>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        aplicacion = application as Aplicacion

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

    private fun empezarPartida() {
        aplicacion.empezarPartida()
        val dificultad : EnumDificultad = aplicacion.difucultadPartida()
        val grid = crearGrid(dificultad.filas,dificultad.columnas)
        tablero=Array(dificultad.filas) {fila -> Array(dificultad.columnas){col-> crearBoton(fila,col,grid) } }
     }



    private fun onLongBotonClicked(v: View?,fila: Int,columna: Int) {
    Log.i("jjbo","LARGO"+fila+"-"+columna)
    }

    private fun onBotonClicked(v: View?,fila: Int,columna: Int) {
        Log.i("jjbo","CORTO:"+fila+"-"+columna)
    }


    private fun crearBoton(fila: Int,columna: Int,grid: GridLayout): ImageButton {
        //creacion del boton y caracteristicas basicas
        val btn: ImageButton = ImageButton(this)
        //btn.setTextColor(ResourcesCompat.getColorStateList(resources, R.color.texto_boton, null))
          btn.id = View.generateViewId()



        // btn.setBackgroundResource(R.drawable.boton)
        //color del boton
       // val idColor = listaColores.getResourceId(i.mod(listaColores.length()), 0)
        //ViewCompat.setBackgroundTintMode(btn,PorterDuff.Mode.OVERLAY)
        //ViewCompat.setBackgroundTintList(
          //  btn,
           // ResourcesCompat.getColorStateList(resources, idColor, null)
       // );

        //Definicion de los parametros de layout
        // fila, columna y weight para los Gridlayout params
        val params: GridLayout.LayoutParams =
            GridLayout.LayoutParams(GridLayout.spec(fila, 1, 1f), GridLayout.spec(columna, 1, 1f))
        params.height = 0
        params.width = 0
//        //margen
        val margen: Int =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.1f, resources.displayMetrics)
                .toInt();
        params.setMargins(margen, margen, margen, margen)
        btn.layoutParams = params


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
                0->aplicacion.dificultad=EnumDificultad.FACIL
                1->aplicacion.dificultad=EnumDificultad.MEDIO
                2->aplicacion.dificultad=EnumDificultad.DIFICIL
            }
        }
        alertDialog.setPositiveButton("Ok"){_,_->}
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