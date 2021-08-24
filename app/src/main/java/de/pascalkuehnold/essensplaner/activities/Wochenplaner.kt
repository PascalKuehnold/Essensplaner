package de.pascalkuehnold.essensplaner.activities

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import de.pascalkuehnold.essensplaner.MainActivity
import de.pascalkuehnold.essensplaner.R
import de.pascalkuehnold.essensplaner.database.AppDatabase
import de.pascalkuehnold.essensplaner.database.WochenplanerDatabase
import de.pascalkuehnold.essensplaner.database.WochenplanerVeggieDatabase
import de.pascalkuehnold.essensplaner.dataclasses.Gericht
import de.pascalkuehnold.essensplaner.interfaces.GerichtDao
import de.pascalkuehnold.essensplaner.interfaces.Wochenplan
import de.pascalkuehnold.essensplaner.interfaces.WochenplanerDao
import de.pascalkuehnold.essensplaner.layout.CustomAdapter
import kotlin.random.Random


open class Wochenplaner : Wochenplan(),AdapterView.OnItemSelectedListener, View.OnClickListener {
    private lateinit var listWochenplaner: ListView
    private lateinit var dropdownTitleSpinner: Spinner
    private lateinit var listOfTitles: Array<String>

    var daysToGenerate = 7
    private var weeksGerichte = ArrayList<Gericht>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wochenplaner)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#266799")))
        supportActionBar!!.setCustomView(R.layout.wochenplan_title)
        supportActionBar!!.setDisplayShowCustomEnabled(true)


        listOfTitles = arrayOf(getString(R.string.wochenplaner), getString(R.string.wochenplanerveggie))

        dropdownTitleSpinner = supportActionBar!!.customView.findViewById<Spinner>(R.id.spinnerWochenplanerTitle)
        dropdownTitleSpinner.onItemSelectedListener = this
        val dropdownAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listOfTitles)

        dropdownTitleSpinner.adapter = dropdownAdapter



        listWochenplaner = findViewById(R.id.listViewWochenplan)

        weeksGerichte = getWeeksGerichte()

        if(weeksGerichte.isEmpty() || weeksGerichte.size < 7){
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
                        .setTitle(getString(R.string.textCreateNewPlan))
                        .setMessage(getString(R.string.textWarningLosingOldPlan))
                        .setPositiveButton(getString(R.string.textOK), DialogInterface.OnClickListener(function = positiveButtonClick))

                builder.show()
            }
        }
    }

    override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {

        if(getGerichtDao().getAll().size < 7){
            val alert = AlertDialog.Builder(this)
                .setTitle(getString(R.string.textNotEnoughMeals))
                .setMessage(getString(R.string.textNotEnoughMealsDesc))
                .setCancelable(true)
                .setOnCancelListener {
                    finish()
                }
            alert.show()
            dropdownTitleSpinner.setSelection(0)
        }

        if(listOfTitles[position] == getString(R.string.wochenplaner)){
            return
        } else if(listOfTitles[position] == getString(R.string.wochenplanerveggie) && AppDatabase.getDatabase(applicationContext).gerichtDao().findByIsVegetarisch(true).size >= 7){
            startActivity(Intent(this, WochenplanerVeggieActivity::class.java))
        }  else {
            println("Veggie Wochenplaner konnte nicht erstellt werden, keine Gericht verfügbar")
            val alert = AlertDialog.Builder(this)
                    .setTitle(getString(R.string.textNotEnoughMealsVeggie))
                    .setMessage(getString(R.string.textNotEnoughMealsDescVeggie))
                    .setCancelable(true)
            alert.show()
            dropdownTitleSpinner.setSelection(0)

            return
        }

        println("Wochenplaner >> Ende generatelist()")


    }

    override fun onNothingSelected(arg0: AdapterView<*>) {

    }

    override fun onResume() {
        super.onResume()
        weeksGerichte = getWeeksGerichte()
        generateListOnScreen()
    }

    private val positiveButtonClick = { _: DialogInterface, _: Int ->
        Toast.makeText(applicationContext, getString(R.string.textNewPlanWasCreated), Toast.LENGTH_SHORT).show()
        println("Wochenplaner >> positiveButtonClick on AlertDialog clicked")

        deleteDatabase()

        generateList()
        generateListOnScreen()
    }

    override fun deleteDatabase() {
        val wochenplanerDao = createConnection()

        wochenplanerDao.delete()
        println("Wochenplaner >> deleteWeekgerichte() -> Daten wurden gelöscht")

    }

    private fun createConnection(): WochenplanerDao {
        return WochenplanerDatabase.getDatabase(applicationContext).wochenGerichteDao()
    }


    override fun saveWeekgerichte() {
        val wochenplanerDao = createConnection()

        for(gericht in weeksGerichte) {
            gericht.gerichtName = gericht.gerichtName
            wochenplanerDao.insertAll(gericht)
        }
        println("Wochenplaner >> saveWeekgerichte() -> Neue Daten wurden gespeichert")

    }

    override fun loadWeekgerichte(){
        val wochenplanerDao = createConnection()

        weeksGerichte = wochenplanerDao.getAll() as ArrayList<Gericht>

        println("Wochenplaner >> loadWeekgerichte() -> Daten wurden geladen")

    }


    private fun generateListOnScreen(){
        val adapter =  CustomAdapter(weeksGerichte, this, this)
        listWochenplaner.adapter = adapter

        adapter.notifyDataSetChanged()
        println("Wochenplaner >> generateListOnScreen() -> Daten wurden an den Screen übergeben")
    }




    private fun generateList() {

        val gerichtDao = getGerichtDao()
        println("Wochenplaner >> btnNeuerPlan pressed")



        val gerichte = gerichtDao.getAll()
        if(gerichte.size >= 7){
            generateRandomGerichte(gerichte)
            saveWeekgerichte()
        }

    }

    private fun getGerichtDao(): GerichtDao {
        return AppDatabase.getDatabase(applicationContext).gerichtDao()
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

    private fun getWeeksGerichte(): ArrayList<Gericht> {
        return createConnection().getAll() as ArrayList<Gericht>
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onClick(v: View?) {
        val gerichte = getWeeksGerichte()
        val gericht = gerichte[v?.tag as Int]
        val gerichtName = gericht.gerichtName
        var gerichtZutaten = gericht.zutaten
        val multipleDays = gericht.mehrereTage
        val shortPrepareTime = gericht.schnellesGericht

        val alleZutatenList = gerichtZutaten.split(",")
        var prevZutat = ""

        for(zutat: String in alleZutatenList){
            prevZutat += if(zutat == alleZutatenList.last()){
                zutat
            } else {
                "$zutat, "
            }
            gerichtZutaten = prevZutat
        }


        AlertDialog.Builder(this)
                .setMessage((
                        getString(R.string.gerichtNameInfo) + " " + gerichtName + "\n\n" +
                                getString(R.string.zutatenInfo) + " " + gerichtZutaten + "\n\n" +
                                getString(R.string.f_r_mehr_als_einen_tag) + ": " + (if(multipleDays)getString(R.string.yes) else getString(R.string.no)) + "\n\n" +
                                getString(R.string.schnelle_zubereitung) + ": " + (if(shortPrepareTime)getString(R.string.yes) else getString(R.string.no))
                        )

                )
                .setPositiveButton(getString(R.string.rezeptAnsicht)) { _, _ ->

                }
                .setCancelable(true)
                .setTitle(getString(R.string.information))
                .setIcon(R.drawable.ic_info)
                .create()
                .show()
    }

}