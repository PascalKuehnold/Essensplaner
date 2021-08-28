package de.pascalkuehnold.essensplaner.activities

import android.database.sqlite.SQLiteConstraintException
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputType
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.textfield.TextInputEditText
import de.pascalkuehnold.essensplaner.R
import de.pascalkuehnold.essensplaner.database.AppDatabase
import de.pascalkuehnold.essensplaner.dataclasses.Gericht
import de.pascalkuehnold.essensplaner.dataclasses.Zutat
import de.pascalkuehnold.essensplaner.interfaces.GerichtDao
import java.util.*
import kotlin.collections.ArrayList


class GerichtHinzufuegenActivity : AppCompatActivity(){
    private var mealName = ""
    private var mealIsVeggie = false
    private var mealIsForMultipleDays = false
    private var mealIsFastPrepared = false
    private var mealReceipt = ""

    private var zutaten: ArrayList<String> = ArrayList()

    private lateinit var textInputGericht: TextInputEditText
    private lateinit var btnZutatHinzufuegen: Button
    private lateinit var switchVegetarisch: SwitchCompat
    private lateinit var switchMultipleDays: SwitchCompat
    private lateinit var switchFastPreperation: SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gericht_hinzufuegen)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.gericht_hinzuf_gen)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#266799")))

        textInputGericht = findViewById(R.id.textInputTextGericht)
        textInputGericht.maxLines = 2

        val btnHinzufuegen = findViewById<Button>(R.id.btnHinzufuegenGericht)

        btnHinzufuegen.setOnClickListener {
            btnHinzufuegen.requestFocus()

            this.mealName = textInputGericht.text.toString()
            this.mealIsVeggie = switchVegetarisch.isChecked
            this.mealIsFastPrepared = switchFastPreperation.isChecked
            this.mealIsForMultipleDays = switchMultipleDays.isChecked

            if (this.mealName.isNotEmpty()) {
                println("GerichteHinzufuegenActivity >> " + this.mealName +
                        " Zutaten: " + Zutat.createNewZutatenString(zutaten) +
                        " Vegetarisch: " + this.mealIsVeggie +
                        " Schnelles Gericht: " + this.mealIsFastPrepared +
                        " Mehrere Tage: " + this.mealIsForMultipleDays +
                        " Rezept: " + this.mealReceipt)
                try {
                    addGericht()
                } catch (e: SQLiteConstraintException) {
                    Toast.makeText(this, this.mealName + " " + getString(R.string.textAlreadyInList), Toast.LENGTH_SHORT).show()
                }
                cleanInput()
            } else {
                Toast.makeText(this, getString(R.string.textErrorAtMealAdd), Toast.LENGTH_SHORT).show()
            }
        }


        btnZutatHinzufuegen = findViewById(R.id.btnZutatHinzufügen)
        btnZutatHinzufuegen.setOnClickListener{
            zutatHinzufuegen()
        }

        switchVegetarisch = findViewById(R.id.switchVegetarisch)
        switchMultipleDays = findViewById(R.id.switchMultipleDays)
        switchFastPreperation = findViewById(R.id.switchFastPreperation)

        textInputGericht.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideSoftKeyboard(v)
            }
        }


    }

    private fun EditText.showSoftKeyboard(){
        (this.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                .showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun zutatHinzufuegen(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.textZutatHinzufuegen))

        val input = EditText(this)
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)
        input.requestFocus()
        textInputGericht.clearFocus()

        builder.setPositiveButton(R.string.hinzuf_gen) { _, _ ->
            if(!input.text.isNullOrEmpty()){
                val reg = Regex("\\s*,\\s*")
                val inputText = input.text.toString().trim().replace(reg, "\n")

                //val items = inputText.split("\\s*,\\s*")
                val items = inputText.lines()

                for(item in items){
                    if(zutaten.contains(item.capitalize(Locale.getDefault()))){
                        Toast.makeText(this, "TODO014 $item ist schon vorhanden", Toast.LENGTH_SHORT).show()
                    } else {
                        val capItem = item.capitalize(Locale.getDefault())

                        zutaten.add(capItem)

                        Toast.makeText(this, "Item" + " " + capItem + " " + getString(R.string.addedSuccessfully), Toast.LENGTH_SHORT).show()
                    }
                }

                zutatHinzufuegen()
            } else {
                Toast.makeText(this, "TODO()005 Keine Eingabe...", Toast.LENGTH_SHORT).show()
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

    //Method for hiding the keyboard after pressing on an empty space on screen
    private fun hideSoftKeyboard(view: View) {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }




    //method to clear all the input fields for the user
    private fun cleanInput(){
        textInputGericht.text?.clear()
        switchVegetarisch.isChecked = false
        switchMultipleDays.isChecked = false
        switchFastPreperation.isChecked = false

        textInputGericht.requestFocus()
        textInputGericht.showSoftKeyboard()
    }

    //method to create a connection to the database
    private fun createConnection(): GerichtDao {
        return AppDatabase.getDatabase(applicationContext).gerichtDao()
    }



    //method for adding a new meal
    private fun addGericht(){
        val tempZutaten = Zutat.createNewZutatenString(zutaten)

        val gerichtDao = createConnection()
        val newGericht = Gericht(
                0,
                mealName.capitalize(Locale.getDefault()),
                tempZutaten,
                mealIsVeggie,
                mealIsForMultipleDays,
                mealIsFastPrepared,
                mealReceipt
        )

        gerichtDao.insertAll(newGericht)
        println("GerichtHandler >> " + newGericht.gerichtName + " was added successfully")
        Toast.makeText(this, this.mealName + " " + getString(R.string.wasAddedText), Toast.LENGTH_SHORT).show()
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