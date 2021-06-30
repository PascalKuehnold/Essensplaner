package de.pascalkuehnold.essensplaner.activities

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import de.pascalkuehnold.essensplaner.R
import de.pascalkuehnold.essensplaner.database.AppDatabase
import de.pascalkuehnold.essensplaner.database.WochenplanerDatabase
import de.pascalkuehnold.essensplaner.dataclasses.Gericht
import de.pascalkuehnold.essensplaner.interfaces.WochenplanerDao
import de.pascalkuehnold.essensplaner.layout.CustomAdapter
import kotlin.random.Random

class Wochenplaner : AppCompatActivity(){
    private lateinit var listWochenplaner: ListView

    val timeToRefresh = 7

    private var daysToGenerate = 7
    private var weeksGerichte = ArrayList<Gericht>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wochenplaner)

        listWochenplaner = findViewById(R.id.listViewWochenplan)

        weeksGerichte = getWeeksGerichte()

        if(weeksGerichte.isEmpty()){
            generateList()
            generateListOnScreen()
        } else {
            loadWeekgerichte()
            generateListOnScreen()
        }

        val btnNeuerPlan = findViewById<Button>(R.id.btnNeuerPlan)
        btnNeuerPlan.setOnClickListener{
            if(weeksGerichte.isEmpty()){
                generateList()
                generateListOnScreen()
            } else {
                println("Wochenplaner >> AlertBox start")
                val builder = AlertDialog.Builder(this)
                        .setTitle("Neuen Plan erstellen?")
                        .setMessage("Der alte Plan geht dabei verloren.")
                        .setPositiveButton("OK", DialogInterface.OnClickListener(function = positiveButtonClick))

                builder.show()

            }

        }

    }

    val positiveButtonClick = {
        _: DialogInterface, _: Int ->
        Toast.makeText(applicationContext, "Neuer Plan wurde erstellt", Toast.LENGTH_SHORT).show()
        println("Wochenplaner >> positiveButtonClick on AlertDialog clicked")

        deleteDatabase()

        generateList()
        generateListOnScreen()
    }

    private fun deleteDatabase() {
        val wochenplanerDao = createConnection()

        wochenplanerDao.delete()
        println("Wochenplaner >> deleteWeekgerichte() -> Daten wurden gelöscht")

    }

    private fun createConnection(): WochenplanerDao {
        return WochenplanerDatabase.getDatabase(applicationContext).wochenGerichteDao()
    }


    private fun saveWeekgerichte() {
        val wochenplanerDao = createConnection()

        for(gericht in weeksGerichte) {
            gericht.gerichtName = gericht.gerichtName
            wochenplanerDao.insertAll(gericht)
        }
        println("Wochenplaner >> saveWeekgerichte() -> Neue Daten wurden gespeichert")

    }

    private fun loadWeekgerichte(){
        val wochenplanerDao = createConnection()

        weeksGerichte = wochenplanerDao.getAll() as ArrayList<Gericht>
        println("Wochenplaner >> loadWeekgerichte() -> Daten wurden geladen")

    }


    private fun generateListOnScreen(){
        val gerichteListe = getWeeksGerichte()

        //val listItems = List<Gericht>(daysToGenerate)

        val adapter =  CustomAdapter(weeksGerichte, this)
        listWochenplaner.adapter = adapter

        println("Wochenplaner >> generateListOnScreen() -> Daten wurden an den Screen übergeben")
    }


    private fun generateList() {

        val gerichtDao = AppDatabase.getDatabase(applicationContext).gerichtDao()
        println("Wochenplaner >> btnNeuerPlan pressed")



        val gerichte = gerichtDao.getAll()
        if(gerichte.size >= 7){
            generateRandomGerichte(gerichte)
            saveWeekgerichte()
        } else {
            println("Wochenplan konnte nicht erstellt werden, keine Gericht verfügbar")
            val alert = AlertDialog.Builder(this)
                    .setTitle("Nicht genügend Gerichte verfügbar")
                    .setMessage("Die angelegte Gerichteliste beträgt nicht genug Gerichte um einen Wochenplan zu erstellen.")
                    .setCancelable(true)
            alert.show()

            return
        }

        println("Wochenplaner >> Ende generatelist()")

    }

    private fun generateRandomGerichte(gerichte: List<Gericht>) {
        weeksGerichte.clear()
        while(weeksGerichte.size < daysToGenerate){
            val rnd = Random.nextInt(0, gerichte.lastIndex + 1)
            if(weeksGerichte.contains(gerichte[rnd])){
                continue
            }
            weeksGerichte.add(gerichte[rnd])
            println(gerichte[rnd].gerichtName + " was added to the weekly list")
        }
    }

    fun printWochenplan(){
        for(gericht in weeksGerichte) println(gericht.gerichtName)
    }

    private fun getWeeksGerichte(): ArrayList<Gericht> {
        return createConnection().getAll() as ArrayList<Gericht>
    }

}