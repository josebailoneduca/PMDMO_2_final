package com.imagenprogramada.things_swipper

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

/**
 * Adaptador para el spiner del dialogo de selección de de skin del juego
 */
class SkinArrayAdapter(context: Context, private val itemLayout: Int, private val skins: Array<out EnumSkin>) :
    ArrayAdapter<EnumSkin>(context, itemLayout, skins) {

    //vista para los items del dropdown
    var dropdownView:Int=0


    /**
     * Crea la vista para el item principal
     */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflador: LayoutInflater = LayoutInflater.from(context)
        //reciclar vista existente o crear una nueva si no existe
        var vFila =convertView
        if (vFila==null)
         vFila = inflador.inflate(itemLayout,parent,false)
        //rellenar vista
        ((vFila?.findViewById(R.id.nombre)) as TextView).text = context.resources.getString(skins[position].nombre)
        (( vFila.findViewById(R.id.imagenIcono)) as ImageView).setImageResource(skins[position].imagen)
        //retornar vista
        return vFila
    }

    /**
     * Crea la vista para los items del dropdown
     */
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflador: LayoutInflater = LayoutInflater.from(context)
        val vFila:View = inflador.inflate(dropdownView,parent,false)
        ((vFila.findViewById(R.id.nombre)) as TextView).text = context.resources.getString(skins[position].nombre)
        (( vFila.findViewById(R.id.imagenIcono)) as ImageView).setImageResource(skins[position].imagen)
        return vFila
    }

    /**
     * Almacena la ResourceId de la vista usada para el dropdown
     */
    override fun setDropDownViewResource(dropdownView: Int) {
        this.dropdownView=dropdownView
    }
}