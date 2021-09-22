package de.pascalkuehnold.essensplaner

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import de.pascalkuehnold.essensplaner.activities.*
import de.pascalkuehnold.essensplaner.database.AppDatabase
import de.pascalkuehnold.essensplaner.database.EinkaufslisteDatabase
import de.pascalkuehnold.essensplaner.database.WochenplanerDatabase
import de.pascalkuehnold.essensplaner.database.WochenplanerVeggieDatabase


class MainActivity : AppCompatActivity() {
    private var introMessage: RelativeLayout? = null
    private var appContent: LinearLayout? = null
    private var welcomeText: TextView? = null
    private var clicked = false
    private val welcomeScreenShownPref = "welcomeScreenShown"
    private lateinit var mPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        val intent = intent
        val inAppWelcScreen = intent.getBooleanExtra("inAppWelcScreen", false)
        // second argument is the default to use if the preference can't be found
        val welcomeScreenShown = mPrefs.getBoolean(welcomeScreenShownPref, false)


        val window = this.window

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.newBackgroundColor)
        }

        if(!welcomeScreenShown && !inAppWelcScreen){
            showWelcomeText()
        } else {
            showMainLayout()
        }
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#13344D")))
        //setSupportActionBar(findViewById(R.id.toolbar))
    }

    private fun showWelcomeText() {
        setContentView(R.layout.welcome_message_android)
        introMessage = findViewById<View>(R.id.welcome_message_layout) as RelativeLayout
        appContent = findViewById<View>(R.id.app_content_layout) as LinearLayout
        welcomeText = findViewById<View>(R.id.welcome_message) as TextView

        welcomeText!!.movementMethod = ScrollingMovementMethod()
    }

    fun dismissWelcomeMessageBox(view: View?) {

        if(!clicked){
            clicked = true
            welcomeText!!.text = getString(R.string.whatsNew)
        } else {
            introMessage!!.visibility = View.INVISIBLE
            appContent!!.visibility = View.VISIBLE

            showMainLayout()
            clicked = false
        }
    }

    private fun showMainLayout() {
        setContentView(R.layout.activity_main)

        val btnAlleGerichteAnzeigen = findViewById<Button>(R.id.btnAlleGerichteAnzeigen)
        btnAlleGerichteAnzeigen.setOnClickListener {
            val intent = Intent(this, GerichteListeActivity::class.java)
            startActivity(intent)
        }

        val btnWochenplaner = findViewById<Button>(R.id.btnWochenplaner)
        btnWochenplaner.setOnClickListener {
            val intent = Intent(this, Wochenplaner::class.java)
            startActivity(intent)
        }

        val btnEinkaufsliste = findViewById<Button>(R.id.btnEinkaufsliste)
        btnEinkaufsliste.setOnClickListener{
            val intent = Intent(this, EinkaufslisteActivity::class.java)
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

        when(item.itemId){
            R.id.about -> {
                startActivity(Intent(this, AboutActivity::class.java))
            }
        }

        when(item.itemId){
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
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
            WochenplanerVeggieDatabase.getDatabase(applicationContext).wochenGerichteVeggieDao().delete()
            EinkaufslisteDatabase.getDatabase(applicationContext).einkaufslisteDao().delete()
            Toast.makeText(this, getString(R.string.allDataDeletedText), Toast.LENGTH_LONG).show()
        }
        alert.setNegativeButton(getString(R.string.cancel)) { dialog: DialogInterface, _: Int ->
            dialog.cancel()
        }
        alert.show()



        println("Datenbank wurde gelöscht")
    }

    fun dontShowAgain(view: View) {
        val editor = mPrefs.edit()
        editor.putBoolean(welcomeScreenShownPref, true)
        editor.apply() // Very important to save the preference

        Toast.makeText(this, "Die Nachricht wird nun nicht mehr beim Start angezeigt.", Toast.LENGTH_LONG).show()
    }

    override fun onBackPressed() {
        finish()
    }
}