package de.pascalkuehnold.essensplaner.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import de.pascalkuehnold.essensplaner.R
import de.pascalkuehnold.essensplaner.dataclasses.Gericht

class GerichtEditierenActivity : AppCompatActivity() {
    lateinit var inputFieldGericht: TextView
    lateinit var listViewZutaten: ListView
    lateinit var switchVegetarisch: androidx.appcompat.widget.SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gericht_editieren)

        inputFieldGericht = findViewById(R.id.textGerichtEditierenName)
        listViewZutaten = findViewById(R.id.listViewZutatenlisteGerichtAendern)
        switchVegetarisch = findViewById(R.id.switchGerichteEditierenVegetarisch)

        val gericht: Bundle? = intent.extras
        var gerichtName = ""
        var zutatenListe = ""
        var isVegetarisch = false

        if(gericht != null){
            gerichtName = gericht.getString("GERICHT_NAME").toString()
            zutatenListe = gericht.getString("ZUTATEN_LISTE").toString()
            isVegetarisch = gericht.getBoolean("IS_VEGETARISCH")
        }

        inputFieldGericht.text = gerichtName

        //TODO zutatenListe splitten und in die liste einfügen
        //TODO vegetarisch boolean setzen


    }
}