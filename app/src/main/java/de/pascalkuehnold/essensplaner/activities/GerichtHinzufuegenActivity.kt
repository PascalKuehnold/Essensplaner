package de.pascalkuehnold.essensplaner.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import de.pascalkuehnold.essensplaner.R
import de.pascalkuehnold.essensplaner.database.AppDatabase
import de.pascalkuehnold.essensplaner.database.DatabaseCon
import de.pascalkuehnold.essensplaner.dataclasses.Gericht
import de.pascalkuehnold.essensplaner.interfaces.GerichtDao

class GerichtHinzufuegenActivity : AppCompatActivity(){
    private var currentIndex = 1
    private var gerichtName = ""
    private var textZutaten = ""
    private var isVegetarisch = false

    lateinit var textInputGericht: TextInputEditText
    lateinit var textInputZutat: TextInputEditText
    lateinit var switchVegetarisch: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gericht_hinzufuegen)

        val btnHinzufuegen = findViewById<Button>(R.id.btnHinzufuegenGericht)
        textInputGericht = findViewById(R.id.textInputTextGericht)
        textInputZutat = findViewById(R.id.textInputTextZutat)
        switchVegetarisch = findViewById(R.id.switchVegetarisch)



        btnHinzufuegen.setOnClickListener {
            this.gerichtName = textInputGericht.text.toString()
            this.textZutaten = textInputZutat.text.toString()
            this.isVegetarisch = switchVegetarisch.isChecked


            if(this.gerichtName.isNotEmpty() && this.textZutaten.isNotEmpty()){
                println("GerichteHinzufuegenActivity >> " + this.gerichtName + " Zutaten: " + this.textZutaten  + " Vegetarisch: " + this.isVegetarisch)
                Toast.makeText(this, this.gerichtName + " wurde hinzugefügt", Toast.LENGTH_SHORT).show()
                addGericht()

                cleanInput()

            } else {
                println("Eine Eingabe ist fehlerhaft.")
                println("Das Gericht wurde nicht hinzugefügt")
            }

        }
    }

    fun cleanInput(){
        textInputGericht.text?.clear()

        textInputZutat.text?.clear()
        !switchVegetarisch.isChecked
        textInputGericht.requestFocus()
    }

    fun deleteDatabase() {
        createConnection().delete()

        println("Wochenplaner >> deleteWeekgerichte() -> Daten wurden gelöscht")

    }

    private fun createConnection(): GerichtDao {
        return AppDatabase.getDatabase(applicationContext).gerichtDao()
    }



    //add a new Gericht
    fun addGericht(){

        val gerichtDao = createConnection()
        val newGericht = Gericht(gerichtName, textZutaten, isVegetarisch)

        gerichtDao.insertAll(newGericht);
        println("GerichtHandler >> " + newGericht.gerichtName + " was added successfully");
    }


    fun deleteGericht(gericht: Gericht){
        val gerichtDao = createConnection()

        gerichtDao.delete(gericht);
        println("GerichtHandler >> " + gericht.gerichtName + " was deleted successfully");

    }


    fun deleteGericht(gerichtName: String?){
        val gerichtDao = createConnection()

        val gerichtToDelete = gerichtName?.let { gerichtDao.findByName(it) }
        if (gerichtToDelete != null) {
            gerichtDao.delete(gerichtToDelete)
            println("GerichtHandler >> $gerichtName was deleted successfully");
        } else {
            println("GerichtHandler >> $gerichtName could not be deleted");
        }


    }



}