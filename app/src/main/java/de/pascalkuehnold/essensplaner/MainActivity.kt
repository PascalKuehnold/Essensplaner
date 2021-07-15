package de.pascalkuehnold.essensplaner

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import de.pascalkuehnold.essensplaner.activities.Wochenplaner
import de.pascalkuehnold.essensplaner.activities.GerichteListeActivity
import de.pascalkuehnold.essensplaner.database.AppDatabase
import de.pascalkuehnold.essensplaner.database.WochenplanerDatabase

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //setSupportActionBar(findViewById(R.id.toolbar))

        val btnAlleGerichteAnzeigen = findViewById<Button>(R.id.btnAlleGerichteAnzeigen)
        btnAlleGerichteAnzeigen.setOnClickListener{
            val intent = Intent(this, GerichteListeActivity::class.java)
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

    //Method for deleting the content on the phone (database of meals)
    private fun deleteDatabase(){
        println("Datenbank löschen gedrückt")


        val alert = AlertDialog.Builder(this)
        alert.setTitle(getString(R.string.deleteDataFromPhone))
        alert.setMessage(getString(R.string.deleteDataFromPhoneMessage))
        alert.setPositiveButton(getString(R.string.delete)) { _: DialogInterface, _: Int ->
            AppDatabase.getDatabase(applicationContext).gerichtDao().delete()
            WochenplanerDatabase.getDatabase(applicationContext).wochenGerichteDao().delete()
            Toast.makeText(this, getString(R.string.allDataDeletedText), Toast.LENGTH_LONG).show()
        }
        alert.setNegativeButton(getString(R.string.cancel)) { dialog: DialogInterface, _: Int ->
            dialog.cancel()
        }
        alert.show()



        println("Datenbank wurde gelöscht")
    }
}