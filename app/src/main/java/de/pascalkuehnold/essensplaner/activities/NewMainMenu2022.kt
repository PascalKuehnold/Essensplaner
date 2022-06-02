package de.pascalkuehnold.essensplaner.activities

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.preference.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import de.pascalkuehnold.essensplaner.R
import de.pascalkuehnold.essensplaner.database.AppDatabase
import de.pascalkuehnold.essensplaner.database.EinkaufslisteDatabase
import de.pascalkuehnold.essensplaner.database.WochenplanerDatabase
import de.pascalkuehnold.essensplaner.database.WochenplanerVeggieDatabase
import de.pascalkuehnold.essensplaner.handler.BottomNavHandler
import de.pascalkuehnold.essensplaner.handler.DatabaseHandler

class NewMainMenu2022 : AppCompatActivity(){
    private lateinit var layoutMainMealList: LinearLayout
    private lateinit var layoutMainWeeklyplanner: LinearLayout
    private lateinit var layoutMainShoppingList: LinearLayout
    private lateinit var layoutMainGroup: LinearLayout
    private lateinit var layoutMainMenu: BottomNavigationView

    private var introMessage: RelativeLayout? = null
    private var appContent: LinearLayout? = null
    private var welcomeText: TextView? = null


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

        window.statusBarColor = ContextCompat.getColor(this, R.color.newBackgroundColor)

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
        introMessage!!.visibility = View.INVISIBLE
        appContent!!.visibility = View.VISIBLE
        showMainLayout()
    }


    private fun showMainLayout() {
        setContentView(R.layout.activity_new_main_menu2022)

        createMainMenuOnClickListeners()
    }

    private fun createMainMenuOnClickListeners() {
        layoutMainMenu = findViewById(R.id.mainMenuBottomNav)
        layoutMainMenu.itemIconTintList = null

        BottomNavHandler(this, layoutMainMenu).createBottomNavHandler()

        layoutMainMealList = findViewById(R.id.layoutMainMealList)
        layoutMainMealList.setOnClickListener {
            val intent = Intent(this, GerichteListeActivity::class.java)
            startActivity(intent)
        }


        layoutMainWeeklyplanner = findViewById(R.id.layoutMainWeeklyplanner)
        layoutMainWeeklyplanner.setOnClickListener {
            val intent = Intent(this, Wochenplaner::class.java)
            startActivity(intent)
        }


        layoutMainShoppingList = findViewById(R.id.layoutMainShoppingList)
        layoutMainShoppingList.setOnClickListener {
            val intent = Intent(this, EinkaufslisteActivity::class.java)
            startActivity(intent)
        }


        layoutMainGroup = findViewById(R.id.layoutMainGroup)
        layoutMainGroup.setOnClickListener {
            //TODO CREATE GROUP ACTIVITY
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



        //Datenschutzerklärung und Nutzungsbedingungen
//        when(item.itemId){
//            R.id.menu_privacy_policy -> {
//                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/essensplaner-privacy-policy/deutsch"))
//                startActivity(browserIntent)
//            }
//        }
//
//        when(item.itemId){
//            R.id.menu_terms_of_service -> {
//                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/essensplaner-terms-conditions/deutsch"))
//                startActivity(browserIntent)
//            }
//        }


        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    //Method for deleting the content on the phone (database of meals)
    private fun deleteDatabase(){
        println("Datenbank löschen gedrückt")

        DatabaseHandler(this, applicationContext).deleteAllDatabases()

        println("Datenbank wurde gelöscht")
    }

    fun dontShowAgain(view: View) {
        val editor = mPrefs.edit()
        editor.putBoolean(welcomeScreenShownPref, true)
        editor.apply() // Very important to save the preference

        Toast.makeText(
            this,
            "Die Nachricht wird nun nicht mehr beim Start angezeigt.",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onBackPressed() {
        finish()
    }


    }

