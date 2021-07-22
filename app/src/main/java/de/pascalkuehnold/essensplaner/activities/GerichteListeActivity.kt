package de.pascalkuehnold.essensplaner.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Looper
import android.os.MessageQueue.IdleHandler
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.pascalkuehnold.essensplaner.R
import de.pascalkuehnold.essensplaner.database.AppDatabase
import de.pascalkuehnold.essensplaner.dataclasses.Gericht
import de.pascalkuehnold.essensplaner.layout.CustomAdapter


//TODO sort algorithm
class GerichteListeActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var listView: ListView
    private lateinit var searchView: SearchView
    lateinit var gerichteListe: ArrayList<Gericht>
    lateinit var adapterLV: CustomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gerichte_liste)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.deine_gerichte)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#266799")))


        searchView = findViewById(R.id.sbGerichteListe)
        searchView.isFocusable = false

        listView = findViewById(R.id.gerichteAnzeige)

        gerichteListe = getGerichteListe() as ArrayList<Gericht>

        adapterLV = CustomAdapter(gerichteListe, this, this)
        listView.adapter = adapterLV



        val btnAddGerichteButton = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        btnAddGerichteButton.isFocusable = false
        btnAddGerichteButton.setOnClickListener{
            val intent = Intent(this, GerichtHinzufuegenActivity::class.java)
            startActivity(intent)
            refreshGerichteListe()
        }

        //TODO sort algorithm
        val spinner: Spinner = findViewById(R.id.spinner)
        spinner.isFocusable = false
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
        refreshGerichteListe()



    }


    override fun onResume() {
        super.onResume()
        refreshGerichteListe()
    }

    private fun refreshGerichteListe(){
        gerichteListe = getGerichteListe() as ArrayList<Gericht>


        //for(i in 1..100) {
        //    val gericht = Gericht(0, "Gericht $i", "Zutat $i", true)
        //   gerichtDao.insert(gericht)
        //}
//        val listItems = arrayOfNulls<String>(gerichteListe.size)
//
//        for(i in gerichteListe.indices){
//            val gericht = gerichteListe[i]
//            listItems[i] = gericht.gerichtName
//        }


        adapterLV.notifyDataSetChanged()





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

    fun getGerichteListe(): List<Gericht> {
        val gerichtDao = AppDatabase.getDatabase(applicationContext).gerichtDao()

        return gerichtDao.getAll()
    }

    override fun onClick(v: View?) {
        val gerichte = getGerichteListe()
        val gericht = gerichte[v?.tag as Int]
        val gerichtName = gericht.gerichtName
        val gerichtZutaten = gericht.zutaten


        AlertDialog.Builder(this)
                .setMessage("Gericht Name: $gerichtName\n\nZutaten: $gerichtZutaten")
                .setCancelable(true)
                .setIcon(R.drawable.ic_info)
                .create()
                .show()
    }

}