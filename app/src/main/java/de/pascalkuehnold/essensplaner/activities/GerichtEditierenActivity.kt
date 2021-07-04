package de.pascalkuehnold.essensplaner.activities


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.ListView
import android.widget.TextView
import de.pascalkuehnold.essensplaner.R


class GerichtEditierenActivity : AppCompatActivity() {
    lateinit var inputFieldGericht: TextView
    lateinit var oldGerichtName: TextView
    lateinit var listViewZutaten: ListView
    lateinit var switchVegetarisch: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gericht_editieren)

        inputFieldGericht = findViewById(R.id.textGerichtEditierenName)
        oldGerichtName = findViewById(R.id.gerichtAlterName)
        listViewZutaten = findViewById(R.id.listViewZutatenlisteGerichtAendern)
        switchVegetarisch = findViewById(R.id.switchGerichteEditierenVegetarisch)

        val gericht: Bundle? = intent.extras
        val gerichtName = ""
        var zutatenListe = ""
        var isVegetarisch = false

        if(gericht != null){
            oldGerichtName.text = gericht.getString("GERICHT_NAME").toString()

            zutatenListe = gericht.getString("ZUTATEN_LISTE").toString()
            isVegetarisch = gericht.getBoolean("IS_VEGETARISCH")
        }

        inputFieldGericht.text = gerichtName

        val zutaten = zutatenListe.split(", ")

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, zutaten)
        listViewZutaten.adapter = adapter

        inputFieldGericht.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideSoftKeyboard(v)
            }
        }



        //TODO zutatenListe splitten und in die liste einfügen
        //TODO vegetarisch boolean setzen


    }
    //Method for hiding the keyboard after pressing on an empty space on screen
    private fun hideSoftKeyboard(view: View) {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}