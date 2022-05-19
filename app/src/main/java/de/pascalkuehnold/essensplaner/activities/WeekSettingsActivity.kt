package de.pascalkuehnold.essensplaner.activities

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import de.pascalkuehnold.essensplaner.R
import java.util.*
import kotlin.collections.HashMap


/**
 * Created by Kalle on 5/10/2022.
 */
class WeekSettingsActivity : AppCompatActivity(), View.OnClickListener {
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

    private lateinit var layout: TableLayout

    private val settings = HashMap<Any, Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_week_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.wocheneinstellung)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#266799")))

        layout = findViewById(R.id.tableLayout)

        initWeekdays()

        loopToggleButtons(layout)
        Log.d("TOGGLEBUTTON:", settings.toString())
    }

    private fun loopToggleButtons(view: ViewGroup) {
        for (i in 0 until view.childCount) {
            val v: View = view.getChildAt(i)
            if (v is ToggleButton) {
                changeSettings(v)
            } else if (v is ViewGroup) {
                this.loopToggleButtons(v)
            }
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
        toggleButtonMonday.setOnClickListener(this)
        editTextTimeMonday = findViewById(R.id.editTextTimeMonday)
        //TUESDAY
        textViewTuesday = findViewById(R.id.textViewTuesday)
        toggleButtonTuesday = findViewById(R.id.toggleButtonTue)
        toggleButtonTuesday.setOnClickListener(this)
        editTextTimeTuesday = findViewById(R.id.editTextTimeTuesday)
        //WEDNESDAY
        textViewWednesday = findViewById(R.id.textViewWednesday)
        toggleButtonWednesday = findViewById(R.id.toggleButtonWed)
        toggleButtonWednesday.setOnClickListener(this)
        editTextTimeWednesday = findViewById(R.id.editTextTimeWednesday)
        //THURSDAY
        textViewThursday = findViewById(R.id.textViewThursday)
        toggleButtonThursday = findViewById(R.id.toggleButtonThu)
        toggleButtonThursday.setOnClickListener(this)
        editTextTimeThursday = findViewById(R.id.editTextTimeThursday)
        //FRIDAY
        textViewFriday = findViewById(R.id.textViewFriday)
        toggleButtonFriday = findViewById(R.id.toggleButtonFr)
        toggleButtonFriday.setOnClickListener(this)
        editTextTimeFriday = findViewById(R.id.editTextTimeFriday)
        //SATURDAY
        textViewSaturday = findViewById(R.id.textViewSaturday)
        toggleButtonSaturday = findViewById(R.id.toggleButtonSat)
        toggleButtonSaturday.setOnClickListener(this)
        editTextTimeSaturday = findViewById(R.id.editTextTimeSaturday)
        //SUNDAY
        textViewSunday = findViewById(R.id.textViewSunday)
        toggleButtonSunday = findViewById(R.id.toggleButtonSun)
        toggleButtonSunday.setOnClickListener(this)
        editTextTimeSunday = findViewById(R.id.editTextTimeSunday)

    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onClick(v: View?) {
        if(v is ToggleButton){
            changeSettings(v)
            Log.d("TOGGLEBUTTON:", settings.toString())
        }
    }

    private fun changeSettings(v: ToggleButton) {
        val settingDayTag = v.tag
        val settingDayOn = v.isChecked


        settings[settingDayTag] = settingDayOn

    }
}