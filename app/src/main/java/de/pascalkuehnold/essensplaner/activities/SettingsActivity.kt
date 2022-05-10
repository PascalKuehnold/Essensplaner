package de.pascalkuehnold.essensplaner.activities

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SwitchCompat
import androidx.preference.PreferenceManager
import de.pascalkuehnold.essensplaner.R

class SettingsActivity : AppCompatActivity(){
    private lateinit var switchWelcomeMessageShow: SwitchCompat
    private lateinit var btnWochenplanerEinstellungen: AppCompatButton
    private lateinit var mPrefs: SharedPreferences
    private val welcomeScreenShownPref = "welcomeScreenShown"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_layout)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.action_settings)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#266799")))

        switchWelcomeMessageShow = findViewById(R.id.showWelcomeMessage)
        btnWochenplanerEinstellungen = findViewById(R.id.btnWochenplanerEinstellungen)

        btnWochenplanerEinstellungen.setOnClickListener {
            val intent = Intent(this, WeekSettingsActivity::class.java)
            startActivity(intent)
        }


        mPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        // second argument is the default to use if the preference can't be found
        val welcomeScreenShown = mPrefs.getBoolean(welcomeScreenShownPref, false)

        switchWelcomeMessageShow.isChecked = !welcomeScreenShown



        switchWelcomeMessageShow.setOnCheckedChangeListener{ _: CompoundButton, b: Boolean ->
            val editor = mPrefs.edit()
            editor.putBoolean(welcomeScreenShownPref, !b)
            editor.apply()

            val message: String = if(b){
                "Die Willkommensnachricht wird nun wieder angezeigt."
            } else {
                "Die Willkommensnachricht wird nun nicht mehr angezeigt."
            }

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

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
}