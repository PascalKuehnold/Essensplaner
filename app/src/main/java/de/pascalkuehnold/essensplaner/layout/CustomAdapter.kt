package de.pascalkuehnold.essensplaner.layout

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListAdapter
import android.widget.TextView
import de.pascalkuehnold.essensplaner.R
import de.pascalkuehnold.essensplaner.activities.GerichtEditierenActivity
import de.pascalkuehnold.essensplaner.dataclasses.Gericht

//TODO() EinkaufslistenButton und funktion einführen
class CustomAdapter(newGerichte: List<Gericht>, newContext: Context): BaseAdapter(), ListAdapter {
    private val gerichte = newGerichte
    val context = newContext

    override fun getCount(): Int {
        return gerichte.size
    }

    override fun getItem(position: Int): Any {
        return gerichte[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var view = convertView
        if(view == null){
            val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.custom_list_item, null)
        }

        val gerichtName = view?.findViewById<TextView>(R.id.gerichtName)
        if (gerichtName != null) {
            gerichtName.text = gerichte[position].gerichtName
        }

        val gerichtZutaten = view?.findViewById<TextView>(R.id.zutaten)
        if (gerichtZutaten != null) {
            gerichtZutaten.text = gerichte[position].zutaten
            
        }

        val btnGerichtBearbeiten = view?.findViewById<android.widget.Button>(R.id.btnBearbeiten)
        btnGerichtBearbeiten?.setOnClickListener {
            val intent = Intent(parent?.context, GerichtEditierenActivity::class.java).apply{
                putExtra("ID", gerichte[position].id)
            }
            println("Btn gericht bearbeiten wurde gedrückt")
            parent?.context?.startActivity(intent)
        }

        return view
    }
}