package de.pascalkuehnold.essensplaner.activities

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import de.pascalkuehnold.essensplaner.R
import de.pascalkuehnold.essensplaner.database.AppDatabase
import de.pascalkuehnold.essensplaner.database.WochenplanerVeggieDatabase
import de.pascalkuehnold.essensplaner.dataclasses.Gericht
import de.pascalkuehnold.essensplaner.interfaces.Wochenplan
import de.pascalkuehnold.essensplaner.interfaces.WochenplanerVeggieDao
import de.pascalkuehnold.essensplaner.layout.CustomAdapter
import kotlin.random.Random


open class WochenplanerVeggieActivity :Wochenplan(),AdapterView.OnItemSelectedListener{
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

        listOfTitles = arrayOf(getString(R.string.wochenplanerveggie), getString(R.string.wochenplaner))

        dropdownTitleSpinner = supportActionBar!!.customView.findViewById<Spinner>(R.id.spinnerWochenplanerTitle)
        dropdownTitleSpinner.onItemSelectedListener = this
        val dropdownAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listOfTitles)

        dropdownTitleSpinner.adapter = dropdownAdapter

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
                        .setTitle(getString(R.string.textCreateNewPlan))
                        .setMessage(getString(R.string.textWarningLosingOldPlan))
                        .setPositiveButton(getString(R.string.textOK), DialogInterface.OnClickListener(function = positiveButtonClick))

                builder.show()
            }
        }
    }

    override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
        if(listOfTitles[position] == getString(R.string.wochenplaner)){
            startActivity(Intent(this, Wochenplaner::class.java))
        }
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
        val wochenplanerVeggieDao = createConnection()

        wochenplanerVeggieDao.delete()
        println("Wochenplaner >> deleteWeekgerichte() -> Daten wurden gelöscht")

    }

    private fun createConnection(): WochenplanerVeggieDao {
        return WochenplanerVeggieDatabase.getDatabase(applicationContext).wochenGerichteVeggieDao()
    }


    override fun saveWeekgerichte() {
        val wochenplanerVeggieDao = createConnection()

        for(gericht in weeksGerichte) {
            gericht.gerichtName = gericht.gerichtName
            wochenplanerVeggieDao.insertAll(gericht)
        }
        println("Wochenplaner >> saveWeekgerichte() -> Neue Daten wurden gespeichert")

    }

    override fun loadWeekgerichte(){
        val wochenplanerVeggieDao = createConnection()

        weeksGerichte = wochenplanerVeggieDao.getAll() as ArrayList<Gericht>
        println("Wochenplaner >> loadWeekgerichte() -> Daten wurden geladen")

    }


    private fun generateListOnScreen(){
        val adapter =  CustomAdapter(weeksGerichte, this)
        listWochenplaner.adapter = adapter

        adapter.notifyDataSetChanged()
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
                    .setTitle(getString(R.string.textNotEnoughMeals))
                    .setMessage(getString(R.string.textNotEnoughMealsDesc))
                    .setCancelable(true)
                    .setOnCancelListener {
                        finish()
                    }
            alert.show()


            return
        }

        println("Wochenplaner >> Ende generatelist()")

    }

    private fun generateRandomGerichte(gerichte: List<Gericht>) {
        weeksGerichte.clear()
        while(weeksGerichte.size < daysToGenerate){
            val rnd = Random.nextInt(0, gerichte.lastIndex + 1)
            if(weeksGerichte.contains(gerichte[rnd]) && !gerichte[rnd].isVegetarisch){
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

}