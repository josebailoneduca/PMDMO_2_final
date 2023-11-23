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

class DialogoSeleccionSkin: DialogFragment(), AdapterView.OnItemSelectedListener {

    lateinit var actividad:Activity


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(actividad)
        val vista = LayoutInflater.from(actividad).inflate(R.layout.dialogo_seleccion_skin,null,false)
        dialogBuilder.setView(vista)
        val spinner = vista.findViewById<Spinner>(R.id.selectorSkin)
        dialogBuilder.setTitle(R.string.selecciona_qu_buscar);
        dialogBuilder.setPositiveButton("Ok"){_,_->}
        val adaptador:SkinArrayAdapter= SkinArrayAdapter(actividad,R.layout.list_skins_item,EnumSkin.values())
        adaptador.dropdownView=R.layout.list_skins_item_dropdown
        spinner.adapter=adaptador
        spinner.setSelection((actividad.application as Aplicacion).skin.indice)
        spinner.onItemSelectedListener=this

        return dialogBuilder.show()
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.actividad=activity
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val app:Aplicacion =(actividad.applicationContext as Aplicacion)
        when(position){
            0->app.skin=EnumSkin.LLAVES
            1->app.skin=EnumSkin.CARTERA
            2->app.skin=EnumSkin.GAFAS
            3->app.skin=EnumSkin.MOVIL
        }
        (actividad as MainActivity).menu?.getItem(0)?.setIcon(app.skin.imagen)

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }
}