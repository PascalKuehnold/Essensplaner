package de.pascalkuehnold.essensplaner.creatorclasses

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.pascalkuehnold.essensplaner.R
import de.pascalkuehnold.essensplaner.activities.GerichtEditierenActivity
import de.pascalkuehnold.essensplaner.activities.GerichtHinzufuegenActivity
import de.pascalkuehnold.essensplaner.database.AppDatabase
import de.pascalkuehnold.essensplaner.dataclasses.Gericht
import de.pascalkuehnold.essensplaner.layout.CustomAdapter

class GerichteListe : AppCompatActivity() {
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gerichte_liste)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        listView = findViewById(R.id.gerichteAnzeige)

        val btnAddGerichteButton = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        btnAddGerichteButton.setOnClickListener{
            val intent = Intent(this, GerichtHinzufuegenActivity::class.java)
            startActivity(intent)
            refreshGerichteListe()
        }

        val spinner: Spinner = findViewById(R.id.spinner)
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
                this,
                R.array.sortBarValues_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }
        //TODO sort algorithm

        refreshGerichteListe()
    }


    private fun refreshGerichteListe() {

        val gerichtDao = AppDatabase.getDatabase(applicationContext).gerichtDao()
        val gerichteListe = gerichtDao.getAll()
        val listItems = arrayOfNulls<String>(gerichteListe.size)


        for(i in gerichteListe.indices){
            val gericht = gerichteListe[i]
            listItems[i] = gericht.gerichtName
        }

        val adapter = CustomAdapter(gerichteListe, this)
        adapter.notifyDataSetChanged()

        listView.adapter = adapter

        //databaseConnection?.close()
    }



}