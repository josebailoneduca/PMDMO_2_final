package com.imagenprogramada.things_swipper

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class SkinArrayAdapter( context: Context, val itemLayout: Int, val skins: Array<out EnumSkin>) :
    ArrayAdapter<EnumSkin>(context, itemLayout, skins) {

    var dropdownView:Int=0;

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflador: LayoutInflater = LayoutInflater.from(context)
        val vFila:View = inflador.inflate(itemLayout,parent,false);
        ((vFila.findViewById(R.id.nombre)) as TextView).setText(context.resources.getString(skins[position].nombre))
        (( vFila.findViewById(R.id.imagenIcono)) as ImageView).setImageResource(skins[position].imagen)
        return vFila;
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflador: LayoutInflater = LayoutInflater.from(context)
        val vFila:View = inflador.inflate(dropdownView,parent,false);
        ((vFila.findViewById(R.id.nombre)) as TextView).setText(context.resources.getString(skins[position].nombre))
        (( vFila.findViewById(R.id.imagenIcono)) as ImageView).setImageResource(skins[position].imagen)
        return vFila;
    }

    override fun setDropDownViewResource(dropdownView: Int) {
        this.dropdownView=dropdownView
    }
}