package de.pascalkuehnold.essensplaner.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
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


//TODO Search algorithm
class GerichteListeActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var listView: ListView
    private lateinit var searchView: SearchView

    private var alertBuilder: AlertDialog.Builder? = null
    private lateinit var alert: AlertDialog

    lateinit var sortedGerichte: MutableList<Gericht>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gerichte_liste)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.deine_gerichte)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#266799")))

        searchView = findViewById(R.id.sbGerichteListe)
        listView = findViewById(R.id.gerichteAnzeige)

        val btnAddGerichteButton = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        btnAddGerichteButton.setOnClickListener{
            alertBuilder = AlertDialog.Builder(this)
            alert = alertBuilder?.create()!!
            alert.setView(layoutInflater.inflate(R.layout.gericht_hinzufuegen_dialog, null))
            alert.setTitle("Neues Gericht hinzufügen")
            alert.setMessage("Neues Gericht selbst eintragen oder über Chefkoch.de anlegen.")
            alert.setCancelable(true)
            alert.setIcon(R.drawable.ic_add_to_shoppinglist)
            alert.show()



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
        spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                sortedGerichte = getGerichteListe().toMutableList()
                when(position){
                    0 -> {
                        refreshGerichteListe()
                    }
                    1 -> {
                        sortedGerichte.sortBy { it.gerichtName }
                        sortGerichteListe(sortedGerichte)
                    }
                    2 -> {
                        sortedGerichte.sortByDescending { it.gerichtName }
                        sortGerichteListe(sortedGerichte)
                    }
                    3 -> {
                        sortedGerichte.sortByDescending { it.isVegetarisch }
                        sortGerichteListe(sortedGerichte)
                    }
                }

            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
        refreshGerichteListe()
    }

    override fun onResume() {
        super.onResume()
        refreshGerichteListe()
    }

    private fun refreshGerichteListe(){
        val gerichteListe = getGerichteListe()

        val adapter = CustomAdapter(gerichteListe, this, this)


        sortedGerichte = getGerichteListe().toMutableList()
        listView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun sortGerichteListe(tempGerichteListe: MutableList<Gericht>){
        val adapter = CustomAdapter(tempGerichteListe, this, this)

        listView.adapter = adapter
        adapter.notifyDataSetChanged()
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

    private fun getGerichteListe(): List<Gericht> {
        val gerichtDao = AppDatabase.getDatabase(applicationContext).gerichtDao()

        return gerichtDao.getAll()
    }



    override fun onClick(v: View?) {
        val gerichte = sortedGerichte
        val gericht = gerichte[v?.tag as Int]
        val gerichtName = gericht.gerichtName
        var gerichtZutaten = gericht.zutaten
        val multipleDays = gericht.mehrereTage
        val shortPrepareTime = gericht.schnellesGericht

        val alleZutatenList = gerichtZutaten.split(",")
        var prevZutat = ""

        for(zutat: String in alleZutatenList){
            prevZutat += if(zutat == alleZutatenList.last()){
                zutat
            } else {
                "$zutat, "
            }
            gerichtZutaten = prevZutat
        }


        AlertDialog.Builder(this)
                .setMessage(
                    (
                            getString(R.string.gerichtNameInfo) + " " + gerichtName + "\n\n" +
                                    getString(R.string.zutatenInfo) + " " + gerichtZutaten + "\n\n" +
                                    getString(R.string.f_r_mehr_als_einen_tag) + ": " + (if (multipleDays) getString(
                                R.string.yes
                            ) else getString(R.string.no)) + "\n\n" +
                                    getString(R.string.schnelle_zubereitung) + ": " + (if (shortPrepareTime) getString(
                                R.string.yes
                            ) else getString(R.string.no))
                            )

                )
                .setPositiveButton(getString(R.string.findRecipe)) { _, _ ->
                    AlertDialog.Builder(this)
                            .setTitle("TODO017() Weiterleitende Verlinkung")
                            .setMessage("TODO18() Beim Klicken auf den Button wirst du auf eine externe Seite weitergeleitet.")
                            .setPositiveButton("Zu Chefkoch.de") {_, _ ->
                                val browserIntent =
                                        Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse("https://www.chefkoch.de/rs/s0/${gericht.gerichtName}/Rezepte.html")
                                        )
                                startActivity(browserIntent)
                            }
                            .setNegativeButton("Lieber nicht"){dialog, _ ->
                                dialog.dismiss()
                            }
                            .setIcon(R.drawable.ic_info)
                            .create()
                            .show()
                }
                .setNegativeButton(getString(R.string.gerichtAnzeigen)){_, _ ->
                    val gerichtIntent =
                        Intent(this,GerichtActivity::class.java)
                    startActivity(gerichtIntent)
                }

                .setCancelable(true)
                .setTitle(getString(R.string.information))
                .setIcon(R.drawable.ic_info)
                .create()
                .show()
    }

    fun addMealByUser(view: View) {
        alert.dismiss()
        val intent = Intent(this, GerichtHinzufuegenActivity::class.java)
        startActivity(intent)
    }

}