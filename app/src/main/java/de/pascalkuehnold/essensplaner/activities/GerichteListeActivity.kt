package de.pascalkuehnold.essensplaner.activities

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.View.TEXT_ALIGNMENT_CENTER
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.graphics.drawable.toBitmap
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.pascalkuehnold.essensplaner.R
import de.pascalkuehnold.essensplaner.database.AppDatabase
import de.pascalkuehnold.essensplaner.dataclasses.Gericht
import de.pascalkuehnold.essensplaner.layout.CustomAdapter
import org.jsoup.Jsoup
import org.jsoup.select.Elements


//TODO Search algorithm
class GerichteListeActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var listView: ListView
    private lateinit var searchView: SearchView

    private var alertBuilder: AlertDialog.Builder? = null

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
            alertBuilder = AlertDialog.Builder(this, R.style.Theme_Essensplaner_DialogTheme)
            alert = alertBuilder?.create()!!
            alert!!.setView(layoutInflater.inflate(R.layout.gericht_hinzufuegen_dialog, null))
            alert!!.setTitle("Neues Gericht hinzufügen")
            alert!!.setMessage("Neues Gericht selbst eintragen oder über Chefkoch.de anlegen.")
            alert!!.setCancelable(true)
            alert!!.setIcon(R.drawable.ic_add_to_shoppinglist)
            alert!!.show()



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
        if(urlList.isNotEmpty()){
            for(url in urlList){
                getChefkochGericht(url)
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



    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onClick(v: View?) {
        val gerichte = sortedGerichte
        val gericht = gerichte[v?.tag as Int]
        val gerichtName = gericht.gerichtName
        var gerichtZutaten = gericht.zutaten
        val multipleDays = gericht.mehrereTage
        val shortPrepareTime = gericht.schnellesGericht

        val alleZutatenList = if(gerichtZutaten.startsWith("`")){
            gerichtZutaten.split("´")
        } else {
            gerichtZutaten.split(",")
        }
        var prevZutat = ""

        for(zutat: String in alleZutatenList){
            val zutatClean = zutat.removePrefix("`")
            prevZutat += if(zutatClean == alleZutatenList.last()){
                zutatClean
            } else {
                "$zutatClean, "
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
                .setPositiveButton(getString(R.string.findRecipe)) { dialog, _ ->
                    showWarningExternalLink(
                            "https://www.chefkoch.de/rs/s0/${gericht.gerichtName}/Rezepte.html",
                            false
                    )
                    dialog.dismiss()
                }
                .setNegativeButton(getString(R.string.gerichtAnzeigen)){ _, _ ->
                    val gerichtIntent =
                        Intent(this, GerichtActivity::class.java)
                    startActivity(gerichtIntent)
                }

                .setCancelable(true)
                .setTitle(getString(R.string.information))
                .setIcon(R.drawable.ic_info)
                .create()
                .show()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun showWarningExternalLink(url: String, input: Boolean) {
        var inputText: EditText? = null

        val alertDialogBuilder = AlertDialog.Builder(this, R.style.Theme_Essensplaner_DialogTheme)
        if(input){
            inputText = EditText(this)
            inputText.hint = "Gericht eingeben"
            inputText.setHintTextColor(resources.getColor(R.color.lightGreyAlpha75))
            inputText.textAlignment = TEXT_ALIGNMENT_CENTER

        }
        alertDialogBuilder.setPositiveButton("Zu Chefkoch.de") { dialog, _ ->
            var recipeString = ""
            if (inputText != null) {
                recipeString = if(inputText.text.isNotEmpty()){
                    url + inputText.text + "/Rezepte.html"
                } else {
                    "https://www.chefkoch.de/rezepte/"
                }
            }

            val builder = CustomTabsIntent.Builder()

            val sendLinkIntent = Intent(this@GerichteListeActivity, ActionBroadcastReceiver::class.java)
            sendLinkIntent.putExtra(Intent.EXTRA_SUBJECT, "This is the link you were exploring")
            val pendingIntent = PendingIntent.getBroadcast(this, 0, sendLinkIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            // Set the action button
            AppCompatResources.getDrawable(this, R.drawable.ic_add_to_shoppinglist)?.let {
                builder.setActionButton(it.toBitmap(), "Add this link to your dig", pendingIntent, false)
            }
            val customTabsIntent: CustomTabsIntent = builder.build()
            customTabsIntent.launchUrl(this, Uri.parse(recipeString))
        }
        alertDialogBuilder.setNegativeButton("Lieber nicht") { dialog, _ ->
            dialog.dismiss()
        }
        val alert = alertDialogBuilder.create()
            alert.setTitle("TODO017() Weiterleitende Verlinkung")
            alert.setMessage("TODO18() Hier kannst du dein gewünschtes Gericht eintragen und dann über \"Zu Chefkoch.de\" nach einem Rezept suchen.")
            alert.setIcon(R.drawable.ic_info)
            alert.setView(inputText)
            alert.show()
    }

    fun addMealByUser(view: View) {
        alert?.dismiss()
        val intent = Intent(this, GerichtHinzufuegenActivity::class.java)
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun addMealByChefkoch(view: View){
        showWarningExternalLink("https://www.chefkoch.de/rs/s0/", true)
    }

    private fun getChefkochGericht(url: String){
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()

        StrictMode.setThreadPolicy(policy)

        val doc = Jsoup.connect(url).get()
        val html = doc.outerHtml()

        print(doc.title())
        val newsHeadlines: Elements = doc.select("h1")
        for (headline in newsHeadlines) {
            println(headline.text())
        }

        val hasVegetarischText = doc.select(".ds-tag:contains(Vegetarisch)")
        val hasVeganText = doc.select(".ds-tag:contains(Vegan)")

        val isVegetarisch = hasVegetarischText.isNotEmpty() || hasVeganText.isNotEmpty()

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
        val zutatenNamenArray: ArrayList<String> = ArrayList()

        val zutatenNamen: Elements = doc.select(".td-right span")
        for(zutatenName in zutatenNamen){
            val text = "`${zutatenName.text()}´"
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

        Gericht.addGericht(this, doc.title(), zutatenNamenArray, isVegetarisch, false, false, zubereitungText)
    }



    class ActionBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val uri: Uri? = intent.data
            if (uri != null) {
                alert!!.dismiss()
                Log.d("Broadcast URL", uri.toString())
                Toast.makeText(context, uri.toString(), Toast.LENGTH_SHORT).show()

                urlList.add(uri.toString())
                Log.d("URL", uri.toString())

            }
        }

    }

    companion object{
        var urlList: ArrayList<String> = ArrayList()
        var alert: AlertDialog? = null
    }

}