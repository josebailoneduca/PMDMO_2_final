package com.imagenprogramada.things_swipper

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

/**
 * Fragmento dialogo para la seleccion del skin
 */
class DialogoSeleccionSkin: DialogFragment(), AdapterView.OnItemSelectedListener {

    /**
     * Referencia a la actividad
     */
    private lateinit var actividad:Activity


    /**
     * OnCreate. Construye el dialogo y lo muestra
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //construir dialogo
        val dialogBuilder = AlertDialog.Builder(actividad)
        val vista = LayoutInflater.from(actividad).inflate(R.layout.dialogo_seleccion_skin,null,false)
        dialogBuilder.setView(vista)
        val spinner = vista.findViewById<Spinner>(R.id.selectorSkin)
        dialogBuilder.setTitle(R.string.selecciona_qu_buscar)
        dialogBuilder.setPositiveButton("Ok"){_,_->}

        //configuracion del adaptador del spinner de seleccion de skin
        val adaptador = SkinArrayAdapter(actividad,R.layout.list_skins_item,EnumSkin.values())
        adaptador.dropdownView=R.layout.list_skins_item_dropdown
        spinner.adapter=adaptador
        spinner.setSelection((actividad.application as Aplicacion).skin.indice)
        spinner.onItemSelectedListener=this

        //mostrar dialogo
        return dialogBuilder.show()
    }

    /**
     * Almacenar referencia a la actividad onAttach
     */
     @Deprecated("Deprecated in Java")
     override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.actividad=activity
    }

    /**
     * Ordena a la aplicacion cambiar de skin segun lo seleccionado.
     */
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        //ajustar skin en aplicacion
        val app:Aplicacion =(actividad.applicationContext as Aplicacion)
        when(position){
            0->app.skin=EnumSkin.LLAVES
            1->app.skin=EnumSkin.CARTERA
            2->app.skin=EnumSkin.GAFAS
            3->app.skin=EnumSkin.MOVIL
        }
        //cambiar el icono de la barra superior de la aplicacion
        (actividad as MainActivity).menu.getItem(0)?.setIcon(app.skin.imagen)

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    /**
     * Ejecutado al cerrar el dialogo. Si se esta jugando reinicia la partida
     */
    override fun onDestroyView() {
        super.onDestroyView()
        if((actividad.applicationContext as Aplicacion).jugando)
            (actividad as MainActivity).empezarPartida()
    }
}