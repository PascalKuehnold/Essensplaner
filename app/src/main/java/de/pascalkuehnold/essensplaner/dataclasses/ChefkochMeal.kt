package de.pascalkuehnold.essensplaner.dataclasses

import android.content.Context
import android.os.StrictMode
import android.widget.Toast
import de.pascalkuehnold.essensplaner.R
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

class ChefkochMeal(_context: Context, _url: String) {
    private var url = _url
    private var mContext = _context

    private lateinit var doc: Document
    private lateinit var newsHeadlines: Elements
    private lateinit var hasVegetarischText: Elements
    private lateinit var hasVeganText: Elements
    private lateinit var zutatenEinheiten: Elements
    private lateinit var zubereitung: Elements
    private lateinit var zubereitungsZeit: Elements
    private lateinit var rezeptErsteller: Elements
    private lateinit var zutatenNamen: Elements

    private var isVegetarisch: Boolean = false
    private lateinit var ingredientsAsList: ArrayList<String>
    private var mengenArray: ArrayList<Double> = ArrayList()
    private var einheitenArray: ArrayList<String> = ArrayList()
    private var zutatenNamenArray: ArrayList<String> = ArrayList()
    private var zubereitungText: String = ""

    fun getChefkochGericht(){
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        fetchDocSelection()
        processFetchedData()
        addMealByChefkoch()
    }

    private fun processFetchedData() {
        isVegetarisch = hasVegetarischText.isNotEmpty() || hasVeganText.isNotEmpty()
        getAmountsAndUnitsAsList()
        ingredientsAsList = getIngriedientsAsList()
        zubereitungText = zubereitung.html().replace("<br>", "\n")

        //Zusammenbringen der einzelnen Listen
        for(i in 0 until zutatenEinheiten.size){
            println("Menge: ${mengenArray[i]}; \tEinheit: ${einheitenArray[i]} \t\t-> Zutat: ${ingredientsAsList[i]}")
        }
    }

    private fun fetchDocSelection() {
        doc = Jsoup.connect(url).get()
        newsHeadlines = doc.select("h1")
        hasVegetarischText = doc.select(".ds-tag:contains(Vegetarisch)")
        hasVeganText = doc.select(".ds-tag:contains(Vegan)")
        zutatenNamen = doc.select(".td-right span")
        zubereitung =
            doc.select("article.ds-box.ds-grid-float.ds-col-12.ds-col-m-8.ds-or-3 > div:nth-child(3)")
        zubereitungsZeit = doc.select(".rds-recipe-meta__badge:contains(Gesamtzeit)")
        rezeptErsteller = doc.select("div.ds-mb-right > a")
    }

    private fun getAmountsAndUnitsAsList(){
        var menge = 0.0
        var einheit = ""

        //Zutatenmenge und Einheit
        zutatenEinheiten = doc.select(".td-left")
        for (zutatEinheit in zutatenEinheiten) {
            val regex = Regex(" ")
            var text = zutatEinheit.text()

            when {
                text.isEmpty() -> {
                    menge = 0.0
                }
                text.contains("½") -> {
                    menge = 0.5
                    text = text.replace("½", " ")
                }
                text.contains("¾") -> {
                    menge = 0.75
                    text = text.replace("¾", " ")
                }
            }

            val zutat = text.trim().split(regex, 2)


            try {
                menge += zutat[0].toDouble()
            } catch (e: Exception) {
                einheit = zutat[0]
            }


            if (zutat.size > 1) {
                einheit = zutat[1]
            }

            mengenArray.add(menge)
            einheitenArray.add(einheit)

            menge = 0.0
            einheit = ""


        }
    }

    private fun getIngriedientsAsList(): ArrayList<String> {
        for (zutatenName in zutatenNamen) {
            val text = "`${zutatenName.text()}´"
            zutatenNamenArray.add(text)

        }
        return zutatenNamenArray
    }

    private fun addMealByChefkoch(){
        val zutatList = java.util.ArrayList<Zutat>()
        zutatList.add(Zutat(0, "neuezutatlulchefkochöuö"))

        try {
            Gericht.addGericht(
                mContext,
                newsHeadlines.text(),
                zutatenNamenArray,
                isVegetarisch,
                mealIsForMultipleDays = false,
                mealIsFastPrepared = false,
                mealIsChefkochGericht = true,
                mealOverallCooktime = zubereitungsZeit.text(),
                mealAuthor = rezeptErsteller.text(),
                mealReceipt = zubereitungText,
                chefkochUrl = url,
                zutatenList = zutatList
            )
        } catch(e: Exception){
            Toast.makeText(mContext, R.string.chefkochMealCouldNotBeAdded, Toast.LENGTH_LONG).show()
        }
    }


}