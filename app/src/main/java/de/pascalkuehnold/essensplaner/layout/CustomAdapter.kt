package de.pascalkuehnold.essensplaner.layout

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import de.pascalkuehnold.essensplaner.R
import de.pascalkuehnold.essensplaner.activities.GerichtEditierenActivity
import de.pascalkuehnold.essensplaner.database.EinkaufslisteDatabase
import de.pascalkuehnold.essensplaner.dataclasses.Gericht
import de.pascalkuehnold.essensplaner.dataclasses.Zutat

//TODO() EinkaufslistenButton und funktion einführen
class CustomAdapter(newGerichte: List<Gericht>, newContext: Context, callback: View.OnClickListener): BaseAdapter(), ListAdapter {
    private val gerichte = newGerichte
    val context = newContext
    val mCallback = callback

    override fun getCount(): Int {
        return gerichte.size
    }

    override fun getItem(position: Int): Any {
        return gerichte[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    @SuppressLint("InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var view = convertView
        if(view == null){
            val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.custom_list_item, null)
        }

        val row = view?.findViewById<LinearLayout>(R.id.rowClick)
        row?.setOnClickListener(mCallback)
        row?.tag = position

        val gerichtName = view?.findViewById<TextView>(R.id.gerichtName)
        if (gerichtName != null) {
            gerichtName.text = gerichte[position].gerichtName
        }

        val gerichtZutaten = view?.findViewById<TextView>(R.id.zutaten)
        if (gerichtZutaten != null) {
            gerichtZutaten.text = gerichte[position].zutaten
        }

        val isVegetarianView = view?.findViewById<ImageView>(R.id.imageViewVegetarian)
        if (isVegetarianView != null) {
            if(gerichte[position].isVegetarisch){
                isVegetarianView.visibility = View.VISIBLE
            } else {
                isVegetarianView.visibility = View.INVISIBLE
            }
        }

        val btnGerichtZurEinkaufslisteHinzufuegen = view?.findViewById<Button>(R.id.btnHinzufuegenZurEinkaufsliste)
        btnGerichtZurEinkaufslisteHinzufuegen?.setOnClickListener{

            var alleZutaten: String = ""
            if(gerichte[position].zutaten.isEmpty()){
                alleZutaten = gerichte[position].gerichtName
            } else {
                alleZutaten = gerichte[position].zutaten
            }

            val alleZutatenList = alleZutaten.split(",")


            val einkauflisteDao = EinkaufslisteDatabase.getDatabase(parent!!.context).einkaufslisteDao()

            for(zutat in alleZutatenList){
                val tempZutat = Zutat(0, zutat, isChecked = false)

                einkauflisteDao.insertAll(tempZutat)
            }

        }


        val btnGerichtBearbeiten = view?.findViewById<Button>(R.id.btnBearbeiten)
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