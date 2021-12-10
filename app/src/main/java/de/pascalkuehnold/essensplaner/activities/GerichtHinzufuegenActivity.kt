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
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.textfield.TextInputEditText
import de.pascalkuehnold.essensplaner.R
import de.pascalkuehnold.essensplaner.dataclasses.Gericht
import de.pascalkuehnold.essensplaner.dataclasses.Zutat
import java.util.*
import kotlin.collections.ArrayList


class GerichtHinzufuegenActivity : AppCompatActivity(){
    private var mealName = ""
    private var mealIsVeggie = false
    private var mealIsForMultipleDays = false
    private var mealIsFastPrepared = false
    private var mealIsByChefkoch = false
    private var mealCooktime = ""
    private var mealAuthor = ""
    private var mealReceipt = ""
    private var mealChefkochUrl = ""

    private val tempZutatenStringArray: ArrayList<String> = ArrayList()
    private var zutaten: ArrayList<Zutat> = ArrayList()

    private lateinit var textInputGericht: TextInputEditText
    private lateinit var btnZutatHinzufuegen: Button
    private lateinit var switchVegetarisch: SwitchCompat
    private lateinit var switchMultipleDays: SwitchCompat
    private lateinit var switchFastPreperation: SwitchCompat
    private lateinit var listViewZutaten: ListView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gericht_hinzufuegen)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.mealAdd)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#266799")))


        listViewZutaten = findViewById(R.id.listViewZutatenlisteGerichtAendern)
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

                try {
                    Gericht.addGericht(
                            applicationContext,
                            this.mealName,
                            this.mealIsVeggie,
                            this.mealIsForMultipleDays,
                            this.mealIsFastPrepared,
                            this.mealIsByChefkoch ,
                            this.mealCooktime,
                            this.mealAuthor,
                            this.mealReceipt,
                            this.mealChefkochUrl,
                            this.zutaten
                    )

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
                val items = inputText.lines()


                for(item in items){
                    try {
                        val capItem = item.capitalize(Locale.getDefault())
                        if(tempZutatenStringArray.contains(capItem)){
                            Toast.makeText(this, String.format(getString(R.string.eintragItem), capItem)  + getString(R.string.textAlreadyInList), Toast.LENGTH_SHORT).show()
                        } else {
                            tempZutatenStringArray.add(capItem)
                            zutaten.add(Zutat(0, capItem))
                            Toast.makeText(this, String.format(getString(R.string.eintragItem), capItem)  + getString(R.string.addedSuccessfully), Toast.LENGTH_SHORT).show()
                        }


                    } catch (e: Exception){
                        e.printStackTrace()
                    }
                }
                zutatHinzufuegen()
            } else {
                Toast.makeText(this, R.string.noInputFound, Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton(R.string.abbrechen) { dialog, _ ->
            dialog.cancel()
        }

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
        tempZutatenStringArray.removeAll(ArrayList())

        textInputGericht.requestFocus()
        textInputGericht.showSoftKeyboard()
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