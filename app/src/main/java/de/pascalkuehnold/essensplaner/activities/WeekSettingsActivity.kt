package de.pascalkuehnold.essensplaner.activities

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import de.pascalkuehnold.essensplaner.R

/**
 * Created by Kalle on 5/10/2022.
 */
class WeekSettingsActivity : AppCompatActivity() {
    private lateinit var textViewMonday: TextView
    private lateinit var toggleButtonMonday: ToggleButton
    private lateinit var editTextTimeMonday: EditText

    private lateinit var textViewTuesday: TextView
    private lateinit var toggleButtonTuesday: ToggleButton
    private lateinit var editTextTimeTuesday: EditText

    private lateinit var textViewWednesday: TextView
    private lateinit var toggleButtonWednesday: ToggleButton
    private lateinit var editTextTimeWednesday: EditText

    private lateinit var textViewThursday: TextView
    private lateinit var toggleButtonThursday: ToggleButton
    private lateinit var editTextTimeThursday: EditText

    private lateinit var textViewFriday: TextView
    private lateinit var toggleButtonFriday: ToggleButton
    private lateinit var editTextTimeFriday: EditText

    private lateinit var textViewSaturday: TextView
    private lateinit var toggleButtonSaturday: ToggleButton
    private lateinit var editTextTimeSaturday: EditText

    private lateinit var textViewSunday: TextView
    private lateinit var toggleButtonSunday: ToggleButton
    private lateinit var editTextTimeSunday: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_week_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.wocheneinstellung)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#266799")))

        initWeekdays()

        toggleButtonMonday.setOnCheckedChangeListener { buttonView, isChecked ->

            Log.d("TOGGLEBUTTONMONDAY:", toggleButtonMonday.text as String)

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


    private fun initWeekdays(){
        //MONDAY
        textViewMonday = findViewById(R.id.textViewMonday)
        toggleButtonMonday = findViewById(R.id.toggleButtonMon)
        editTextTimeMonday = findViewById(R.id.editTextTimeMonday)
        //TUESDAY
        textViewTuesday = findViewById(R.id.textViewTuesday)
        toggleButtonTuesday = findViewById(R.id.toggleButtonTue)
        editTextTimeTuesday = findViewById(R.id.editTextTimeTuesday)
        //WEDNESDAY
        textViewWednesday = findViewById(R.id.textViewWednesday)
        toggleButtonWednesday = findViewById(R.id.toggleButtonWed)
        editTextTimeWednesday = findViewById(R.id.editTextTimeWednesday)
        //THURSDAY
        textViewThursday = findViewById(R.id.textViewThursday)
        toggleButtonThursday = findViewById(R.id.toggleButtonThu)
        editTextTimeThursday = findViewById(R.id.editTextTimeThursday)
        //FRIDAY
        textViewFriday = findViewById(R.id.textViewFriday)
        toggleButtonFriday = findViewById(R.id.toggleButtonFr)
        editTextTimeFriday = findViewById(R.id.editTextTimeFriday)
        //SATURDAY
        textViewSaturday = findViewById(R.id.textViewSaturday)
        toggleButtonSaturday = findViewById(R.id.toggleButtonSat)
        editTextTimeSaturday = findViewById(R.id.editTextTimeSaturday)
        //SUNDAY
        textViewSunday = findViewById(R.id.textViewSunday)
        toggleButtonSunday = findViewById(R.id.toggleButtonSun)
        editTextTimeSunday = findViewById(R.id.editTextTimeSunday)

    }
}