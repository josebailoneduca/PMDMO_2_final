package com.imagenprogramada.things_swipper

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentManager

class MainActivity : AppCompatActivity(){
    lateinit var  aplicacion:Aplicacion
    lateinit var menu:Menu
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

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if(aplicacion.jugando) {
            menu?.getItem(0)?.setEnabled(false)
            menu?.getItem(2)?.setEnabled(false)
            menu?.getItem(3)?.setEnabled(false)
        }
        return super.onPrepareOptionsMenu(menu);
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var id:Int = item.getItemId()
        when (id){
            R.id.imInstrucciones -> mostrarInstrucciones()
            R.id.imConfiguraJuego -> abrirConfiguracion()
            R.id.imSeleccionaSkin -> seleccionarSkin();
        }
        return super.onOptionsItemSelected(item)
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