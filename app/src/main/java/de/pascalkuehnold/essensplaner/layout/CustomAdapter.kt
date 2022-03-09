package de.pascalkuehnold.essensplaner.layout

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.PorterDuff
import android.os.Handler
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import de.pascalkuehnold.essensplaner.R
import de.pascalkuehnold.essensplaner.activities.GerichtEditierenActivity
import de.pascalkuehnold.essensplaner.activities.Wochenplaner
import de.pascalkuehnold.essensplaner.activities.WochenplanerVeggieActivity
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
        val gerichtName = view?.findViewById<TextView>(R.id.gerichtName)
        val isVegetarianView = view?.findViewById<ImageView>(R.id.imageViewVegetarian)
        val btnGerichtZurEinkaufslisteHinzufuegen = view?.findViewById<Button>(R.id.btnHinzufuegenZurEinkaufsliste)
        val btnGerichtBearbeiten = view?.findViewById<Button>(R.id.btnBearbeiten)

        if(context is Wochenplaner || context is WochenplanerVeggieActivity) {
            if (weekOfDay != null) {
                weekOfDay.text = weekDays[position]
            }
        } else {
            if (weekOfDay != null) {
                weekOfDay.visibility = View.INVISIBLE
            }
        }


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


        if (isVegetarianView != null) {
            if(selectedGericht.isVegetarisch){
                isVegetarianView.visibility = View.VISIBLE
            } else {
                isVegetarianView.visibility = View.INVISIBLE
            }
        }


        btnGerichtZurEinkaufslisteHinzufuegen?.setOnClickListener{
            var res = this.context.resources


            lateinit var tempZutat: Zutat
            val einkauflisteDao = EinkaufslisteDatabase.getDatabase(parent!!.context).einkaufslisteDao()

            val alleZutatenList = selectedGericht.zutatenList
            if(alleZutatenList.isEmpty()){
                einkauflisteDao.insertAll(Zutat(0, selectedGericht.gerichtName))
            } else {
                for(zutat in alleZutatenList){
                    val zutatClean = zutat.zutatenName.removePrefix("`").removeSuffix("´")
                    tempZutat = Zutat(
                        0,
                        zutatClean,
                        false,
                        zutat.zutatenMengenEinheit,
                        zutat.zutatenMenge
                    )

                    einkauflisteDao.insertAll(tempZutat)
                }
            }

            if(alleZutatenList.size > 1){
                Toast.makeText(
                    parent.context,
                    String.format(res.getString(R.string.ingredientsFromMealWasAddedSuccessful), selectedGericht.gerichtName),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    parent.context,
                    String.format(res.getString(R.string.addedSuccessfullToShoppingList), selectedGericht.gerichtName),
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



        if(context is Wochenplaner || context is WochenplanerVeggieActivity){
            //btnGerichtBearbeiten!!.background = ContextCompat.getDrawable(context, R.drawable.ic_refresh)

            //btnGerichtBearbeiten.setOnClickListener{
                //Wochenplaner().refreshGericht(position, gerichte)
            //}


        } else {
            btnGerichtBearbeiten?.setOnClickListener {
                val intent = Intent(parent?.context, GerichtEditierenActivity::class.java).apply{
                    putExtra("ID", gerichte[position].id)
                }
                println("Btn gericht bearbeiten wurde gedrückt")
                parent?.context?.startActivity(intent)
            }
        }

        //makes a animation that the user clicked the button
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