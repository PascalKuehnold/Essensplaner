package de.pascalkuehnold.essensplaner.activities

import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import de.pascalkuehnold.essensplaner.R
import de.pascalkuehnold.essensplaner.database.AppDatabase
import de.pascalkuehnold.essensplaner.dataclasses.Gericht
import de.pascalkuehnold.essensplaner.interfaces.GerichtDao


class GerichtHinzufuegenActivity : AppCompatActivity(){
    private var currentIndex = 1
    private var gerichtName = ""
    private var textZutaten = ""
    private var isVegetarisch = false

    lateinit var textInputGericht: TextInputEditText
    lateinit var textInputZutat: TextInputEditText
    lateinit var switchVegetarisch: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gericht_hinzufuegen)

        val btnHinzufuegen = findViewById<Button>(R.id.btnHinzufuegenGericht)
        textInputGericht = findViewById(R.id.textInputTextGericht)
        textInputZutat = findViewById(R.id.textInputTextZutat)
        switchVegetarisch = findViewById(R.id.switchVegetarisch)

        setButtonListener(btnHinzufuegen)
        setEditTextFocusListener()


    }

    private fun setEditTextFocusListener() {
        //for the Input of the meal field
        textInputGericht.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideSoftKeyboard(v)
            }
        }

        //for the input of the ingredients field
        textInputZutat.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideSoftKeyboard(v)
            }
        }
    }

    private fun setButtonListener(btnHinzufuegen: Button) {
        btnHinzufuegen.setOnClickListener {
            this.gerichtName = textInputGericht.text.toString()
            this.textZutaten = textInputZutat.text.toString()
            this.isVegetarisch = switchVegetarisch.isChecked

            if (this.gerichtName.isNotEmpty() && this.textZutaten.isNotEmpty()) {
                println("GerichteHinzufuegenActivity >> " + this.gerichtName + " Zutaten: " + this.textZutaten + " Vegetarisch: " + this.isVegetarisch)
                try {
                    addGericht()
                    Toast.makeText(this, this.gerichtName + " wurde hinzugefügt.", Toast.LENGTH_SHORT).show()
                } catch (e: SQLiteConstraintException) {
                    Toast.makeText(this, this.gerichtName + " ist bereits in der Liste.", Toast.LENGTH_SHORT).show()
                }
                cleanInput()
            } else {
                println("Eine Eingabe ist fehlerhaft.")
                println("Das Gericht wurde nicht hinzugefügt")
            }
        }
    }

    private fun hideSoftKeyboard(view: View) {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun cleanInput(){
        textInputGericht.text?.clear()
        textInputZutat.text?.clear()
        switchVegetarisch.isChecked = false
        textInputGericht.requestFocus()
    }

    private fun createConnection(): GerichtDao {
        return AppDatabase.getDatabase(applicationContext).gerichtDao()
    }



    //add a new Gericht
    private fun addGericht(){

        val gerichtDao = createConnection()
        val newGericht = Gericht(gerichtName, textZutaten, isVegetarisch)

        gerichtDao.insertAll(newGericht);
        println("GerichtHandler >> " + newGericht.gerichtName + " was added successfully");
    }


    fun deleteGericht(gericht: Gericht){
        val gerichtDao = createConnection()

        gerichtDao.delete(gericht);
        println("GerichtHandler >> " + gericht.gerichtName + " was deleted successfully");

    }


    fun deleteGericht(gerichtName: String?){
        val gerichtDao = createConnection()

        val gerichtToDelete = gerichtName?.let { gerichtDao.findByName(it) }
        if (gerichtToDelete != null) {
            gerichtDao.delete(gerichtToDelete)
            println("GerichtHandler >> $gerichtName was deleted successfully");
        } else {
            println("GerichtHandler >> $gerichtName could not be deleted");
        }


    }



}