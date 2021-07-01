package de.pascalkuehnold.essensplaner

import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import de.pascalkuehnold.essensplaner.activities.GerichtHinzufuegenActivity
import de.pascalkuehnold.essensplaner.activities.Wochenplaner
import de.pascalkuehnold.essensplaner.creatorclasses.GerichteListe
import de.pascalkuehnold.essensplaner.database.AppDatabase

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        val btnAlleGerichteAnzeigen = findViewById<Button>(R.id.btnAlleGerichteAnzeigen)
        btnAlleGerichteAnzeigen.setOnClickListener{
            val intent = Intent(this, GerichteListe::class.java)
            startActivity(intent)
        }

        val btnWochenplaner = findViewById<Button>(R.id.btnWochenplaner)
        btnWochenplaner.setOnClickListener{
            val intent = Intent(this, Wochenplaner::class.java)
            startActivity(intent)
        }



    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        when(item.itemId){
            R.id.datenbankLoeschen -> deleteDatabase()

        }

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun deleteDatabase(){
        println("Datenbank löschen gedrückt")

        AppDatabase.getDatabase(applicationContext).gerichtDao().delete()

        Toast.makeText(this, "Daten wurden gelöscht...", Toast.LENGTH_LONG).show()

        println("Datenbank wurde gelöscht")
    }
}