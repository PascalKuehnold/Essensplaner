package de.pascalkuehnold.essensplaner.activities

import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.text.InputType
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import de.pascalkuehnold.essensplaner.R
import de.pascalkuehnold.essensplaner.database.AppDatabase
import de.pascalkuehnold.essensplaner.dataclasses.Gericht
import de.pascalkuehnold.essensplaner.interfaces.GerichtDao


class GerichtHinzufuegenActivity : AppCompatActivity(){
    private var gerichtName = ""
    private var textZutaten = ""
    private var isVegetarisch = false

    private lateinit var textInputGericht: TextInputEditText
    private lateinit var btnZutatHinzufuegen: Button
    private lateinit var switchVegetarisch: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gericht_hinzufuegen)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.gericht_hinzuf_gen)

        val btnHinzufuegen = findViewById<Button>(R.id.btnHinzufuegenGericht)
        textInputGericht = findViewById(R.id.textInputTextGericht)
        btnZutatHinzufuegen = findViewById(R.id.btnZutatHinzufügen)
        btnZutatHinzufuegen.setOnClickListener{
            zutatHinzufuegen()
        }





        switchVegetarisch = findViewById(R.id.switchVegetarisch)

        btnHinzufuegen.setOnClickListener {
            btnHinzufuegen.requestFocus()

            this.gerichtName = textInputGericht.text.toString()
            this.isVegetarisch = switchVegetarisch.isChecked

            if (this.gerichtName.isNotEmpty() && this.textZutaten.isNotEmpty()) {
                println("GerichteHinzufuegenActivity >> " + this.gerichtName + " Zutaten: " + this.textZutaten + " Vegetarisch: " + this.isVegetarisch)
                try {
                    addGericht()
                } catch (e: SQLiteConstraintException) {
                    Toast.makeText(this, this.gerichtName + " " + getString(R.string.textAlreadyInList), Toast.LENGTH_SHORT).show()
                }
                cleanInput()
            } else {
                Toast.makeText(this, getString(R.string.textErrorAtMealAdd), Toast.LENGTH_SHORT).show()
            }
        }

        textInputGericht.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideSoftKeyboard(v)
            }
        }


    }

    fun EditText.showSoftKeyboard(){
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
                val inputText = input.text.toString().replace(',', ' ').trim()

                inputText.split("\\s*,\\s*")
                Toast.makeText(this, getString(R.string.zutat) + " " + inputText + " " + getString(R.string.addedSuccessfully), Toast.LENGTH_SHORT).show()

                textZutaten += "$inputText,"
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
        textZutaten = ""

        textInputGericht.requestFocus()
        textInputGericht.showSoftKeyboard()
    }

    //method to create a connection to the database
    private fun createConnection(): GerichtDao {
        return AppDatabase.getDatabase(applicationContext).gerichtDao()
    }



    //method for adding a new meal
    private fun addGericht(){
        var tempZutaten = ""
        if(textZutaten.endsWith(',')){
            tempZutaten = textZutaten.removeSuffix(','.toString())
        }


        val gerichtDao = createConnection()
        val newGericht = Gericht(0, gerichtName, tempZutaten, isVegetarisch)

        gerichtDao.insertAll(newGericht)
        println("GerichtHandler >> " + newGericht.gerichtName + " was added successfully")
        Toast.makeText(this, this.gerichtName + " " + getString(R.string.wasAddedText), Toast.LENGTH_SHORT).show()
    }

    //TODO delete?
    fun deleteGericht(gericht: Gericht){
        val gerichtDao = createConnection()

        gerichtDao.delete(gericht)
        println("GerichtHandler >> " + gericht.gerichtName + " was deleted successfully")

    }

    //TODO delete?
    fun deleteGericht(gerichtName: String?){
        val gerichtDao = createConnection()

        val gerichtToDelete = gerichtName?.let { gerichtDao.findByName(it) }
        if (gerichtToDelete != null) {
            gerichtDao.delete(gerichtToDelete)
            println("GerichtHandler >> $gerichtName was deleted successfully")
        } else {
            println("GerichtHandler >> $gerichtName could not be deleted")
        }
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