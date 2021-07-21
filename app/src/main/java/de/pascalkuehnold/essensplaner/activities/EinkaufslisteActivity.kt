package de.pascalkuehnold.essensplaner.activities

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import de.pascalkuehnold.essensplaner.R
import de.pascalkuehnold.essensplaner.database.EinkaufslisteDatabase
import de.pascalkuehnold.essensplaner.database.WochenplanerDatabase
import de.pascalkuehnold.essensplaner.dataclasses.Gericht
import de.pascalkuehnold.essensplaner.dataclasses.Zutat
import de.pascalkuehnold.essensplaner.interfaces.EinkaufslisteDao
import de.pascalkuehnold.essensplaner.interfaces.WochenplanerDao
import de.pascalkuehnold.essensplaner.layout.CustomAdapter
import de.pascalkuehnold.essensplaner.layout.CustomZutatenAdapter

class EinkaufslisteActivity : AppCompatActivity() {
    lateinit var listEinkaufsliste: ListView
    lateinit var einkaufsliste: ArrayList<Zutat>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_einkaufsliste)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.einkaufsliste)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#266799")))

        listEinkaufsliste = findViewById(R.id.listViewEinkaufsliste)

        loadEinkaufsliste()
        generateListOnScreen()
    }

    private fun createConnection(): EinkaufslisteDao {
        return EinkaufslisteDatabase.getDatabase(applicationContext).einkaufslisteDao()
    }

    fun loadEinkaufsliste(){
        val einkaufslisteDao = createConnection()

        einkaufsliste = einkaufslisteDao.getAll() as ArrayList<Zutat>
        println("Wochenplaner >> loadWeekgerichte() -> Daten wurden geladen")

    }


    private fun generateListOnScreen(){
        val einkaufslisteString: ArrayList<String> = ArrayList()

        for(zutat in einkaufsliste){
            einkaufslisteString.add(zutat.zutatenName)
        }



        val adapter = CustomZutatenAdapter(this, einkaufslisteString)
        listEinkaufsliste.adapter = adapter

        adapter.notifyDataSetChanged()
        println("Wochenplaner >> generateListOnScreen() -> Daten wurden an den Screen übergeben")
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