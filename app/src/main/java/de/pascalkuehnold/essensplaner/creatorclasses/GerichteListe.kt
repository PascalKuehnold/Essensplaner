package de.pascalkuehnold.essensplaner.creatorclasses

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import de.pascalkuehnold.essensplaner.R
import de.pascalkuehnold.essensplaner.activities.GerichtEditierenActivity
import de.pascalkuehnold.essensplaner.database.AppDatabase
import de.pascalkuehnold.essensplaner.layout.CustomAdapter

class GerichteListe : AppCompatActivity() {
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gerichte_liste)

        listView = findViewById(R.id.gerichteAnzeige)

        refreshGerichteListe()
    }


    fun refreshGerichteListe() {

        val gerichtDao = AppDatabase.getDatabase(applicationContext).gerichtDao()
        val gerichteListe = gerichtDao.getAll()
        val listItems = arrayOfNulls<String>(gerichteListe.size)


        for(i in gerichteListe.indices){
            val gericht = gerichteListe[i]
            listItems[i] = gericht.gerichtName
        }

        val adapter = CustomAdapter(gerichteListe, this)

        listView.adapter = adapter

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            Toast.makeText(this, "Clicked item :"+" "+ position, Toast.LENGTH_SHORT).show()
            val intent = Intent(this, GerichtEditierenActivity::class.java).apply {
                putExtra("GERICHT_NAME", gerichteListe[position].gerichtName)
                putExtra("ZUTATEN_LISTE", gerichteListe[position].zutaten)
                putExtra("IS_VEGETARISCH", gerichteListe[position].isVegetarisch)

            }
            startActivity(intent)
        }

        //databaseConnection?.close()
    }




}