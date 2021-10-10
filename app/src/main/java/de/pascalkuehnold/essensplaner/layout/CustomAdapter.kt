package de.pascalkuehnold.essensplaner.layout

import android.R.attr.button
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Handler
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.*
import androidx.multidex.MultiDexApplication
import de.pascalkuehnold.essensplaner.R
import de.pascalkuehnold.essensplaner.activities.*
import de.pascalkuehnold.essensplaner.database.EinkaufslisteDatabase
import de.pascalkuehnold.essensplaner.dataclasses.Gericht
import de.pascalkuehnold.essensplaner.dataclasses.Zutat
import java.util.*


class CustomAdapter(
    newGerichte: List<Gericht>,
    newContext: Context,
    callback: View.OnClickListener?
): BaseAdapter(), ListAdapter {
    private val gerichte = newGerichte
    val context = newContext
    private val mCallback = callback

    private val language = Locale.getDefault().language

    private val weekDays = if(language == "de") {
        arrayOf("Mo.", "Di.", "Mi.", "Do.", "Fr.", "Sa.", "So.")
    } else {
        arrayOf("Mo.", "Tu.", "Wed.", "Thu.", "Fri.", "Sat.", "Sun.")
    }

    override fun getCount(): Int {
        return gerichte.size
    }

    override fun getItem(position: Int): Any {
        return gerichte[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    @SuppressLint("InflateParams", "ClickableViewAccessibility")
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

        val weekOfDay = view?.findViewById<TextView>(R.id.dayOfTheWeek)
        if(context is GerichteListeActivity || context is MultiDexApplication){
            if (weekOfDay != null) {
                weekOfDay.visibility = View.INVISIBLE
            }
        }
        if(context is Wochenplaner || context is WochenplanerVeggieActivity) {
            if (weekOfDay != null) {
                weekOfDay.text = weekDays[position]
            }
        }

        val gerichtName = view?.findViewById<TextView>(R.id.gerichtName)
        if (gerichtName != null) {
            gerichtName.text = selectedGericht.gerichtName
        }

        /*
        val gerichtZutaten = view?.findViewById<TextView>(R.id.zutaten)
        if (gerichtZutaten != null) {
            val alleZutatenList = if(selectedGericht.zutaten.startsWith("`")){
                selectedGericht.zutaten.split("´")
            } else {
                selectedGericht.zutaten.split(",")
            }
            var prevZutat = ""

            for(zutat: String in alleZutatenList){
                prevZutat += if(zutat == alleZutatenList.last()){
                    zutat
                } else {
                    "$zutat, "
                }
                gerichtZutaten.text = prevZutat
            }
        }
        */

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
            val einkauflisteDao = EinkaufslisteDatabase.getDatabase(parent!!.context).einkaufslisteDao()

            val alleZutatenList = selectedGericht.zutatenList
            if(alleZutatenList.isEmpty()){
                einkauflisteDao.insertAll(Zutat(0, selectedGericht.gerichtName))
            } else {
                for(zutat in alleZutatenList){
                    val zutatClean = zutat.zutatenName.removePrefix("`").removeSuffix("´")
                    tempZutat = Zutat(0, zutatClean)

                    einkauflisteDao.insertAll(tempZutat)
                }
            }

            if(alleZutatenList.size > 1){
                Toast.makeText(
                    parent.context,
                    "TODO() -> Zutaten von Gericht ${selectedGericht.gerichtName} wurden zur Einkaufsliste hinzugefügt",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    parent.context,
                    "TODO() -> ${selectedGericht.gerichtName} wurde zur Einkaufsliste hinzugefügt",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
        btnGerichtZurEinkaufslisteHinzufuegen?.setOnTouchListener { mView, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                Handler().postDelayed(Runnable {
                    mView.background.clearColorFilter()
                    mView.invalidate()
                }, 500)
            } else if (event.action == MotionEvent.ACTION_DOWN) {
                mView.background.setColorFilter(-0x1f0b8adf, PorterDuff.Mode.SRC_ATOP)
                mView.invalidate()
            }
            false
        }


        val btnGerichtBearbeiten = view?.findViewById<Button>(R.id.btnBearbeiten)
        btnGerichtBearbeiten?.setOnClickListener {
            val intent = Intent(parent?.context, GerichtEditierenActivity::class.java).apply{
                putExtra("ID", gerichte[position].id)
            }
            println("Btn gericht bearbeiten wurde gedrückt")
            parent?.context?.startActivity(intent)
        }

        btnGerichtBearbeiten?.setOnTouchListener { mView, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                mView.background.clearColorFilter()
                mView.invalidate()
            } else if (event.action == MotionEvent.ACTION_DOWN) {
                mView.background.setColorFilter(-0x1f0b8adf, PorterDuff.Mode.SRC_ATOP)
                mView.invalidate()
            }
            false
        }



        return view
    }


}