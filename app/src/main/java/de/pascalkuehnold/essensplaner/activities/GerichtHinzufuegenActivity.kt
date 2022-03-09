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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import de.pascalkuehnold.essensplaner.R
import de.pascalkuehnold.essensplaner.dataclasses.Gericht
import de.pascalkuehnold.essensplaner.dataclasses.Zutat
import de.pascalkuehnold.essensplaner.layout.CustomZutatenAdapter
import java.util.*
import kotlin.collections.ArrayList


class GerichtHinzufuegenActivity : AppCompatActivity(), View.OnClickListener{
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
    private lateinit var btnZutatHinzufuegen: FloatingActionButton
    private lateinit var switchVegetarisch: SwitchCompat
    private lateinit var switchMultipleDays: SwitchCompat
    private lateinit var switchFastPreperation: SwitchCompat
    private lateinit var listViewZutaten: ListView
    private lateinit var adapter: CustomZutatenAdapter
    private lateinit var addIngredientHeader: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gericht_hinzufuegen)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.mealAdd)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#266799")))


        listViewZutaten = findViewById(R.id.listViewZutatenlisteGerichtAendern)
        textInputGericht = findViewById(R.id.textInputTextGericht)
        textInputGericht.maxLines = 2

        val btnHinzufuegen = findViewById<Button>(R.id.btnSubmit)


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
                //debug purpose
                var tempMealName = "Gericht"
                val tempZutatenList = this.zutaten
                for(i in 0 until 200){
                    Gericht.addGericht(applicationContext, tempMealName + i, false, false, false, false, "","","","",tempZutatenList,1)

                }


                Toast.makeText(this, getString(R.string.textErrorAtMealAdd), Toast.LENGTH_SHORT).show()
            }
        }


        addIngredientHeader = findViewById(R.id.gerichtBearbeitenZutaten)
        addIngredientHeader.setOnClickListener{
            zutatHinzufuegen()
        }

        btnZutatHinzufuegen = findViewById(R.id.btnAddZutat)
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

        //creates the custom adapter
        adapter = CustomZutatenAdapter(this, zutaten, this)

        //setting the adapter for the listview of ingredients
        listViewZutaten.adapter = adapter
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

        builder.setPositiveButton(R.string.hinzuf_gen) { _, _ ->
            //val inputText = input.text.toString().replace(',', ' ').trim()
            //inputText.split("\\s*,\\s*")

            val reg = Regex("\\s*,\\s*")
            val inputText = input.text.toString().trim().replace(reg, "\n")
            val items = inputText.lines()

            for(item in items){
                try {
                    zutaten.add(Zutat(0, item.capitalize(Locale.getDefault())))
                    adapter.notifyDataSetChanged()
                } catch (e: Exception) {
                    Toast.makeText(this,item + " " + getString(R.string.textAlreadyInList), Toast.LENGTH_SHORT).show()
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

    //TODO Click auf Zutat -> soll was passieren?
    override fun onClick(v: View?) {
        //Toast.makeText(this, "TODO()009 Nicht implementiert", Toast.LENGTH_SHORT).show()
    }

}