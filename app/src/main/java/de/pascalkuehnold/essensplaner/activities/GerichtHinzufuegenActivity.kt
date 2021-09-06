package de.pascalkuehnold.essensplaner.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteConstraintException
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
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
import de.pascalkuehnold.essensplaner.dataclasses.Gericht
import de.pascalkuehnold.essensplaner.dataclasses.Zutat
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.util.*
import kotlin.collections.ArrayList


class GerichtHinzufuegenActivity : AppCompatActivity(){
    private var mealName = ""
    private var mealIsVeggie = false
    private var mealIsForMultipleDays = false
    private var mealIsFastPrepared = false
    private var mealReceipt = ""

    private var url = ""

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
                    Gericht.addGericht(applicationContext, this.mealName, this.zutaten, this.mealIsVeggie, this.mealIsForMultipleDays, this.mealIsFastPrepared, this.mealReceipt)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun getChefkochGericht(url: String){
        val doc = Jsoup.connect(url).get()
        val html = doc.outerHtml()

        print(doc.title())
        val newsHeadlines: Elements = doc.select("h1")
        for (headline in newsHeadlines) {
            println(headline.text())
        }

        var menge: Double = 0.0
        var einheit: String = ""


        val mengenArray: ArrayList<Double> = ArrayList()
        val einheitenArray: ArrayList<String> = ArrayList()

        //Zutatenmenge und Einheit
        val zutatenEinheiten: Elements = doc.select(".td-left")
        for(zutatEinheit in zutatenEinheiten){
            val regex = Regex(" ")
            var text = zutatEinheit.text()


            if(text.isEmpty()){
                menge = 0.0
            } else if(text.contains("½")){
                menge = 0.5
                text = text.replace("½", " ")
            } else if(text.contains("¾")){
                menge = 0.75
                text = text.replace("¾", " ")
            }

            val zutat = text.trim().split(regex, 2)


            try {
                menge += zutat[0].toDouble()
            } catch (e: Exception){
                einheit = zutat[0]
            }


            if(zutat.size > 1){
                einheit = zutat[1]
            }

            mengenArray.add(menge)
            einheitenArray.add(einheit)

            menge = 0.0
            einheit = ""
        }

        //Zutatennamen
        var zutatenNamenArray: ArrayList<String> = ArrayList()

        val zutatenNamen: Elements = doc.select(".td-right span")
        for(zutatenName in zutatenNamen){
            var text = zutatenName.text()
            zutatenNamenArray.add(text)

        }

        //Zusammenbringen der einzelnen Listen
        for(i in 0 until zutatenEinheiten.size){
            println("Menge: ${mengenArray[i]}; \tEinheit: ${einheitenArray[i]} \t\t-> Zutat: ${zutatenNamenArray[i]}")
        }

        //Zubereitungstext
        val zubereitung: Elements = doc.select("article.ds-box.ds-grid-float.ds-col-12.ds-col-m-8.ds-or-3 > div:nth-child(3)")
        val zubereitungText = zubereitung.html().replace("<br>", "\n")

        print(zubereitungText)


        //Ersteller des Rezeptes
        val rezeptErsteller: Elements = doc.select("div.ds-mb-right > a")
        print(rezeptErsteller.text())
    }


}