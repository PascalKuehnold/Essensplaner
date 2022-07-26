package de.pascalkuehnold.essensplaner.activities

import android.app.SearchManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.pascalkuehnold.essensplaner.R
import de.pascalkuehnold.essensplaner.database.AppDatabase
import de.pascalkuehnold.essensplaner.dataclasses.ChefkochMeal
import de.pascalkuehnold.essensplaner.dataclasses.Gericht
import de.pascalkuehnold.essensplaner.handler.ExternalLinkHandler
import de.pascalkuehnold.essensplaner.layout.CustomAdapter

class GerichteListeActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var listView: ListView
    private lateinit var searchView: SearchView

    private var alertBuilder: AlertDialog.Builder? = null

    lateinit var sortedGerichte: MutableList<Gericht>

    lateinit var mContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gerichte_liste)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.deine_gerichte)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#266799")))

        searchView = findViewById(R.id.sbGerichteListe)

        mContext = applicationContext

        listView = findViewById(R.id.gerichteAnzeige)


        val btnAddGerichteButton = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        btnAddGerichteButton.setOnClickListener{
            alertBuilder = AlertDialog.Builder(this, R.style.Theme_Essensplaner_DialogTheme)
            alert = alertBuilder?.create()!!
            alert!!.setView(layoutInflater.inflate(R.layout.gericht_hinzufuegen_dialog, null))
            alert!!.setTitle(getString(R.string.mealAddNew))
            alert!!.setMessage(getString(R.string.mealAddNewMessage))
            alert!!.setCancelable(true)
            alert!!.setIcon(R.drawable.ic_add_to_shoppinglist)
            alert!!.show()



            refreshGerichteListe()
        }

        // Get the SearchView and set the searchable configuration
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.apply {
            var adapter: CustomAdapter? = null
            // Assumes current activity is the searchable activity
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            isIconifiedByDefault = true // Do not iconify the widget; expand it by default
            setOnQueryTextListener(object: SearchView.OnQueryTextListener{

                override fun onQueryTextSubmit(query: String?): Boolean {
                    //arraylist to hold selected cosmic bodies
                    val data = ArrayList<Gericht>()
                    if (!searchView.isEnabled) {
                        refreshGerichteListe()
                    } else {
                        //filter by id
                        for (gericht in getGerichteListe()) {

                            val tempZutatenList = gericht.zutatenList.toString()

                            if (tempZutatenList.contains(query.toString(), true) || gericht.gerichtName.contains(query.toString(), true)) {
                                data.add(gericht)
                            }

                        }
                        //instatiate adapter a
                        adapter = CustomAdapter(data, mContext,null)
                    }
                    //set the adapter to GridView
                    listView.adapter = adapter
                    adapter?.notifyDataSetChanged()

                    searchView.clearFocus()
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    //arraylist to hold selected cosmic bodies
                    val data = ArrayList<Gericht>()
                    if (!searchView.isEnabled) {
                        refreshGerichteListe()
                    } else {
                        //filter by id
                        for (gericht in getGerichteListe()) {

                            val tempZutatenList = gericht.zutatenList.toString()

                            if (tempZutatenList.contains(newText.toString(), true) || gericht.gerichtName.contains(newText.toString(), true)) {
                                data.add(gericht)
                            }

                        }
                        //instatiate adapter a
                        adapter = CustomAdapter(data, mContext,null)
                    }
                    //set the adapter to GridView
                    listView.adapter = adapter
                    adapter?.notifyDataSetChanged()
                    return false
                }

            })
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
                refreshGerichteListe()
            }
        }
        refreshGerichteListe()

    }

    override fun onResume() {
        super.onResume()

        if(urlList.isNotEmpty()){
            for(url in urlList){
                ChefkochMeal(mContext, url).getChefkochGericht()
            }
        }
        refreshGerichteListe()

        urlList.clear()
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
        val gerichtId = gericht.id
        val gerichtName = gericht.gerichtName
        val multipleDays = gericht.mehrereTage
        val shortPrepareTime = gericht.schnellesGericht
        val zubereitungsText = gericht.gerichtRezept
        val gerichtAuthor = gericht.gerichtAuthor
        val zubereitungsZeit = gericht.gesamtKochzeit
        val isChefkochGericht = gericht.isChefkochGericht
        val chefkochUrl = gericht.chefkochUrl
        val isVegetarian = gericht.isVegetarisch


        AlertDialog.Builder(this)
                .setMessage(
                        if(gerichtAuthor.isNotEmpty()){
                            String.format(getString(R.string.authorOnChefkoch), gerichtAuthor)
                        } else {
                            ""
                        }
                )
                .setPositiveButton(getString(R.string.findRecipe)) { dialog, _ ->
                    ExternalLinkHandler(this, gerichtId).showWarningExternalLink(
                            "https://www.chefkoch.de/rs/s0/${gericht.gerichtName}/Rezepte.html",
                            false
                    )
                    dialog.dismiss()
                }
                .setNegativeButton(getString(R.string.gerichtAnzeigen)){ _, _ ->

                    val gerichtIntent =
                        Intent(this, GerichtActivity::class.java)
                    gerichtIntent.putExtra("gerichtId", gerichtId)
                    gerichtIntent.putExtra("mealName", gerichtName)
                    gerichtIntent.putExtra("mealRecipe", zubereitungsText)
                    gerichtIntent.putExtra("mealAuthor", gerichtAuthor)
                    gerichtIntent.putExtra("mealCookTime", zubereitungsZeit)
                    gerichtIntent.putExtra("mealByChefkoch", isChefkochGericht)
                    gerichtIntent.putExtra("chefkochUrl", chefkochUrl)
                    gerichtIntent.putExtra("isVegetarian", isVegetarian)
                    startActivity(gerichtIntent)
                }
                .setCancelable(true)
                .setTitle(gerichtName)
                .setIcon(R.drawable.ic_info)
                .create()
                .show()
    }



    fun addMealByUser(view: View) {
        alert?.dismiss()
        val intent = Intent(this, GerichtHinzufuegenActivity::class.java)
        startActivity(intent)
    }


    fun addMealByChefkoch(view: View){
        alert?.dismiss()
        ExternalLinkHandler(this, null).showWarningExternalLink("https://www.chefkoch.de/rs/s0/", true)
    }

    class ActionBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            val regex = Regex("https:\\/\\/www.chefkoch.de\\/rezepte\\/\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)")

            val uri: Uri? = intent.data
            val gerichtPosition: Long = intent.getLongExtra("gerichtPos", -1)

            val url = uri.toString()
            if (uri != null) {

                if(gerichtPosition > 0){
                    val gerichtDao = AppDatabase.getDatabase(context).gerichtDao()
                    val gericht = gerichtDao.loadByID(gerichtPosition)

                    if (gericht != null) {
                        gerichtDao.delete(gericht)
                    }
                }

                    if(url.matches(regex)) {
                        if(urlList.contains(url)){
                            Toast.makeText(context, uri.toString() + " is already in the list", Toast.LENGTH_SHORT).show()
                        } else {
                            alert!!.dismiss()
                            Log.d("Broadcast URL", uri.toString())
                            Toast.makeText(context, uri.toString(), Toast.LENGTH_SHORT).show()

                            urlList.add(uri.toString())
                            Log.d("URL", uri.toString())
                        }

                    } else {
                        Toast.makeText(context, R.string.chefkochMealAddError, Toast.LENGTH_SHORT).show()
                    }

            }
        }



    }


    companion object{
        var urlList: ArrayList<String> = ArrayList()
        var alert: AlertDialog? = null
    }


}