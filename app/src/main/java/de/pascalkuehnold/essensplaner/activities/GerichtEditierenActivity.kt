package de.pascalkuehnold.essensplaner.activities


import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import de.pascalkuehnold.essensplaner.R
import de.pascalkuehnold.essensplaner.database.AppDatabase
import de.pascalkuehnold.essensplaner.database.WochenplanerDatabase
import de.pascalkuehnold.essensplaner.database.WochenplanerVeggieDatabase
import de.pascalkuehnold.essensplaner.dataclasses.Gericht
import de.pascalkuehnold.essensplaner.dataclasses.Zutat
import de.pascalkuehnold.essensplaner.interfaces.WochenplanerI
import de.pascalkuehnold.essensplaner.layout.CustomZutatenAdapter
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

class GerichtEditierenActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var inputFieldGericht: TextInputEditText
    private lateinit var oldGerichtName: TextView
    private lateinit var listViewZutaten: ListView
    private lateinit var switchVegetarisch: SwitchCompat
    private lateinit var switchMultipleDays: SwitchCompat
    private lateinit var switchFastPreperation: SwitchCompat

    private lateinit var btnSubmit: Button
    private lateinit var btnZutatHinzufuegen: FloatingActionButton


    private var mealName =""
    private var mealIsVeggie = false
    private var mealIsForMultipleDays = false
    private var mealIsFastPrepared = false
    private var mealIsByChefkoch = false
    private var mealReceipt = ""
    private var mealCooktime = ""
    private var mealAuthor = ""
    private var mealChefkochUrl = ""
    private var mealPeopleCount = 1

    private var newGericht: Gericht? = null
    private lateinit var adapter: CustomZutatenAdapter

    private var zutaten: ArrayList<Zutat> = ArrayList()

    private var isSaved = false
    private var nameIsSaved = false
    private var gerichtID: Long = -1

    private lateinit var mContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gericht_editieren)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.gericht_bearbeiten)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#266799")))

        inputFieldGericht = findViewById(R.id.textGerichtEditierenName)
        oldGerichtName = findViewById(R.id.gerichtAlterName)
        listViewZutaten = findViewById(R.id.listViewZutatenlisteGerichtAendern)
        switchVegetarisch = findViewById(R.id.switchGerichteEditierenVegetarisch)
        switchMultipleDays = findViewById(R.id.switchMultipleDays)
        switchFastPreperation = findViewById(R.id.switchFastPreperation)

        btnSubmit = findViewById(R.id.btnSubmit)
        btnZutatHinzufuegen = findViewById(R.id.btnAddZutat)

        mContext = this

        btnSubmit.setOnClickListener{
            if(!inputFieldGericht.text.isNullOrBlank()){
                mealName = inputFieldGericht.text.toString()
                changeGericht(zutaten)
                nameIsSaved = true
                oldGerichtName.text = mealName
            }
            if(newGericht == null){
                Toast.makeText(this, ("Es wurden keine Änderungen vorgenommen."), Toast.LENGTH_SHORT).show()
            } else {
                Gericht.saveEditedGericht(this, newGericht!!)
                isSaved = true
                Toast.makeText(this, (newGericht!!.gerichtName + " wurde erfolgreich bearbeitet."), Toast.LENGTH_SHORT).show()
            }
        }

        btnZutatHinzufuegen.setOnClickListener{
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.textZutatHinzufuegen))

            val input = EditText(this)
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(input)
            input.requestFocus()

            builder.setPositiveButton(R.string.hinzuf_gen) { _, _ ->
                //val inputText = input.text.toString().replace(',', ' ').trim()
                //inputText.split("\\s*,\\s*")

                val reg = Regex("\\s*,\\s*")
                val inputText = input.text.toString().trim().replace(reg, "\n")
                val items = inputText.lines()

                for(item in items){
                    try {
                        zutaten.add(Zutat(0, item.capitalize(Locale.getDefault())))
                        changeGericht(zutaten)
                        isSaved = false

                        adapter.notifyDataSetChanged()
                        Toast.makeText(this, getString(R.string.zutat) + " " + inputText + " " + getString(R.string.addedSuccessfully), Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(this, "TODO014 $item ist schon vorhanden", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            builder.setNegativeButton(R.string.abbrechen) { dialog, _ ->
                dialog.cancel()
            }
            // Create the AlertDialog object and return it


            builder.create()
            val alert = builder.show()
            alert.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }


        val gericht: Bundle? = intent.extras

        if(gericht != null){
            gerichtID = gericht.getLong("ID")

            val tempGericht = AppDatabase.getDatabase(applicationContext).gerichtDao().loadByID(gerichtID)

            if (tempGericht != null) {
                oldGerichtName.text = tempGericht.gerichtName
                mealName = tempGericht.gerichtName
                mealIsVeggie = tempGericht.isVegetarisch
                mealIsForMultipleDays = tempGericht.mehrereTage
                mealIsFastPrepared = tempGericht.schnellesGericht
                mealIsByChefkoch = tempGericht.isChefkochGericht
                mealCooktime = tempGericht.gesamtKochzeit
                mealAuthor = tempGericht.gerichtAuthor
                mealReceipt = tempGericht.gerichtRezept
                mealChefkochUrl = tempGericht.chefkochUrl
                if(!tempGericht.zutatenList.isNullOrEmpty()){
                    zutaten = tempGericht.zutatenList as ArrayList<Zutat>
                }
                mealPeopleCount = tempGericht.personenAnzahl

            }

            switchVegetarisch.isChecked = mealIsVeggie
            switchMultipleDays.isChecked = mealIsForMultipleDays
            switchFastPreperation.isChecked = mealIsFastPrepared
        }

        //sets the hint text of the meal name
        inputFieldGericht.hint = "Neuer Gerichtename"


        //for the checkbox if the meal is vegetarian or not
        switchVegetarisch.setOnCheckedChangeListener{ _, isChecked ->
            mealIsVeggie = isChecked
            isSaved = false
            changeGericht(zutaten)
        }

        //for the checkbox if the meal can be for multiple days or not
        switchMultipleDays.setOnCheckedChangeListener{ _, isChecked ->
            mealIsForMultipleDays = isChecked
            isSaved = false
            changeGericht(zutaten)
        }

        //for the checkbox if the meal can be fast prepared
        switchFastPreperation.setOnCheckedChangeListener{ _, isChecked ->
            mealIsFastPrepared = isChecked
            isSaved = false
            changeGericht(zutaten)
        }

        //creates the custom adapter
        adapter = CustomZutatenAdapter(this, zutaten, this)

        //setting the adapter for the listview of ingredients
        listViewZutaten.adapter = adapter

        //Creates an FocusChangeListener and hides the keyboard is the inputfield is no longer focused
        inputFieldGericht.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideSoftKeyboard(v)
                mealName = inputFieldGericht.text.toString()
                isSaved = false
                changeGericht(zutaten)
            }
        }
    }




    //Method for waiting an amount of Toast.LENGTH_SHORT, before leaving the activity
    private var waitForToastShortThread: Thread = object : Thread() {
        override fun run() {
            try {
                sleep(Toast.LENGTH_SHORT.toLong())
                finish()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    //Method for hiding the keyboard after pressing on an empty space on screen
    private fun hideSoftKeyboard(view: View) {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }


    //Method for changing the entire meal
    fun changeGericht(inZutaten: ArrayList<Zutat>){

        newGericht = Gericht(
                gerichtID,
                gerichtName = mealName,
                isVegetarisch =  mealIsVeggie,
                mehrereTage =  mealIsForMultipleDays,
                schnellesGericht = mealIsFastPrepared,
                isChefkochGericht = mealIsByChefkoch,
                gesamtKochzeit = mealCooktime,
                gerichtAuthor = mealAuthor,
                gerichtRezept =  mealReceipt,
                chefkochUrl = mealChefkochUrl,
            zutatenList = inZutaten,
            personenAnzahl = mealPeopleCount
        )
        isSaved = false
    }



    //Method was modified to alert the user if the changes were not saved
    override fun onBackPressed() {
        if(!inputFieldGericht.text.isNullOrBlank() && !nameIsSaved){
            mealName = inputFieldGericht.text.toString()
            changeGericht(zutaten)
        }

        if (newGericht != null && !isSaved){
            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.eingabe_nicht_gespeichert)
            builder.setMessage(R.string.aenderungen_speichern)
            builder.setIcon(android.R.drawable.ic_dialog_alert)

            builder.setPositiveButton(R.string.yes) { _, _ ->

                Gericht.saveEditedGericht(this, newGericht!!)
                isSaved = true
                super.onBackPressed()
            }

            builder.setNegativeButton(R.string.no) { _, _ ->
                super.onBackPressed()
            }
            // Create the AlertDialog object and return it
            builder.create()
            builder.show()
        } else {
            super.onBackPressed()
        }

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.deleteMealButton -> runBlocking{
                deleteMeal()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        Toast.makeText(this, "TODO()009 Nicht implementiert", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        menuInflater.inflate(R.menu.mymenu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun deleteMeal()  {
        val alert = AlertDialog.Builder(this)
        alert.setTitle(getString(R.string.deleteGericht))
        alert.setIcon(R.drawable.ic_delete)

        alert.setMessage(getString(R.string.deleteGerichtText))
                .setPositiveButton(R.string.delete) { _: DialogInterface, _: Int ->
                    val tempDao = AppDatabase.getDatabase(applicationContext).gerichtDao()
                    val tempGericht = tempDao.findByName(mealName)

                    val wochenPlanerDao = WochenplanerDatabase.getDatabase(applicationContext).wochenGerichteDao()
                    val wochenPlanerVeggieDao = WochenplanerVeggieDatabase.getDatabase(applicationContext).wochenGerichteVeggieDao()
                    val weeksGerichte = wochenPlanerDao.getAll() as ArrayList<Gericht>
                    val veggieWeeksGerichte = wochenPlanerVeggieDao.getAll() as ArrayList<Gericht>

                    tempDao.delete(tempGericht)

                    for(mTempGericht in weeksGerichte){
                        if(mTempGericht.gerichtName == tempGericht.gerichtName){
                            generateRandomGericht(weeksGerichte, wochenPlanerDao, false)
                            wochenPlanerDao.delete(tempGericht)
                            Log.d("GerichteEditierenAct", "deleteMeal() wochenplaner gericht wurde entfernt")
                        }
                    }

                    for(mTempGericht in veggieWeeksGerichte){
                        if(mTempGericht.gerichtName == tempGericht.gerichtName){
                            generateRandomGericht(veggieWeeksGerichte, wochenPlanerVeggieDao, true)
                            wochenPlanerVeggieDao.delete(tempGericht)
                            Log.d("GerichteEditierenAct", "deleteMeal() wochenplaner gericht wurde entfernt")
                        }

                    }


                    if(tempDao.getAll().size < 7){
                        Toast.makeText(this, "TODO()007 Not enough meals for 7 days...", Toast.LENGTH_SHORT).show()
                    }
                    if(wochenPlanerVeggieDao.getAll().size < 7){
                        Toast.makeText(this, "TODO()007 Not enough veggie meals for 7 days...", Toast.LENGTH_SHORT).show()
                    }

                    Toast.makeText(this, "TODO()000 Gericht was successfully deleted", Toast.LENGTH_SHORT).show()
                    waitForToastShortThread.start()
                }
                .setNegativeButton(R.string.cancel) { dialog: DialogInterface, _: Int ->
                    dialog.cancel()
                }
        alert.create()
        alert.show()
    }

    private fun generateRandomGericht(weeksGerichte: ArrayList<Gericht>, wochenPlaner: WochenplanerI, isVeggie: Boolean) = runBlocking {
        var tempGericht: Gericht
        val tempDao = AppDatabase.getDatabase(applicationContext).gerichtDao()
        val gerichteListe = tempDao.getAll()

        var rnd = Random.nextInt(0, gerichteListe.lastIndex + 1)

        tempGericht = gerichteListe[rnd]


        for(mGericht in weeksGerichte){
            if(mGericht.gerichtName == tempGericht.gerichtName){
                rnd = Random.nextInt(0, gerichteListe.lastIndex + 1)
                tempGericht = gerichteListe[rnd]
            } else {
                if(!isVeggie){
                    wochenPlaner.insert(tempGericht)
                } else {
                    if(tempGericht.isVegetarisch){
                        wochenPlaner.insert(tempGericht)
                    } else {
                        rnd = Random.nextInt(0, gerichteListe.lastIndex + 1)
                        tempGericht = gerichteListe[rnd]
                    }
                }
            }
        }


        Log.d("WOCHENPLANER", "Ein neues Gericht wurde hinzugefügt, anstelle des Gelöschten Gerichtes")

    }


}