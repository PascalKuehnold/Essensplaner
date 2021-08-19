package de.pascalkuehnold.essensplaner.layout

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.text.TextUtils
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
    private val mCallback = callback

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
        val selectedGericht = gerichte[position]

        if(view == null){
            val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.custom_list_item, null)
        }

        val row = view?.findViewById<LinearLayout>(R.id.rowClick)
        row?.setOnClickListener(mCallback)
        row?.tag = position

        val gerichtName = view?.findViewById<TextView>(R.id.gerichtName)
        if (gerichtName != null) {
            gerichtName.text = selectedGericht.gerichtName
        }

        val gerichtZutaten = view?.findViewById<TextView>(R.id.zutaten)
        if (gerichtZutaten != null) {
            gerichtZutaten.text = selectedGericht.zutaten
        }

        val isVegetarianView = view?.findViewById<ImageView>(R.id.imageViewVegetarian)
        if (isVegetarianView != null) {
            if(selectedGericht.isVegetarisch){
                isVegetarianView.visibility = View.VISIBLE
            } else {
                isVegetarianView.visibility = View.INVISIBLE
            }
        }

        val btnGerichtZurEinkaufslisteHinzufuegen = view?.findViewById<Button>(R.id.btnHinzufuegenZurEinkaufsliste)
        btnGerichtZurEinkaufslisteHinzufuegen?.setOnClickListener{
            lateinit var tempZutat: Zutat


            val alleZutaten: String
            if(selectedGericht.zutaten.isEmpty()){
                alleZutaten = selectedGericht.gerichtName
            } else {
                alleZutaten = selectedGericht.zutaten
            }

            val alleZutatenList = alleZutaten.split(",")


            val einkauflisteDao = EinkaufslisteDatabase.getDatabase(parent!!.context).einkaufslisteDao()

            for(zutat in alleZutatenList){
                tempZutat = Zutat(0, zutat, isChecked = false)

                einkauflisteDao.insertAll(tempZutat)
            }

            if(alleZutatenList.size > 1){
                Toast.makeText(parent.context, "TODO() -> Zutaten von Gericht ${selectedGericht.gerichtName} wurden zur Einkaufsliste hinzugefügt", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(parent.context, "TODO() -> ${tempZutat.zutatenName} wurde zur Einkaufsliste hinzugefügt" , Toast.LENGTH_SHORT).show()
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