package de.pascalkuehnold.essensplaner.activities

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.button.MaterialButton
import de.pascalkuehnold.essensplaner.R
import de.pascalkuehnold.essensplaner.fragments.ScreenSlidePageFragment


/**
 * The number of pages (wizard steps) to show in this demo.
 */
private const val NUM_PAGES = 6

class ScreenSlidePagerActivity : AppCompatActivity() {

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private lateinit var viewPager: ViewPager2
    private lateinit var progressBar: ProgressBar
    private lateinit var listView: ListView
    private lateinit var btnWeiter: MaterialButton

    private lateinit var tvGerichtHinzufuegenHeader: TextView


    private var mealName: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_slide)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.mealAdd)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#266799")))

        mealName = savedInstanceState?.getString("MEAL_NAME")



        // Instantiate a ViewPager2 and a PagerAdapter.
        viewPager = findViewById(R.id.pager)
        progressBar = findViewById(R.id.progressBar)
        tvGerichtHinzufuegenHeader = findViewById(R.id.tvGerichtHinzufuegenHeader)

        listView = findViewById(R.id.listViewScreenSlideAddMeal)
        btnWeiter = findViewById(R.id.btnWeiter)

        // The pager adapter, which provides the pages to the view pager widget.
        val pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager.adapter = pagerAdapter



        viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.e("Selected_Page", position.toString())
                progressBar.progress = position + 1



                when(position){
                    0 -> {
                        tvGerichtHinzufuegenHeader.text = getString(R.string.fragment_one_add_meal_name_title)
                    }
                    1 -> {
                        tvGerichtHinzufuegenHeader.text = getString(R.string.fragment_two_add_ingredient_title)
                    }
                    2 -> tvGerichtHinzufuegenHeader.text = getString(R.string.fragment_three_add_cooktime_title)
                    3 -> tvGerichtHinzufuegenHeader.text = getString(R.string.fragment_four_add_veggieOrNot_title)
                    4 -> tvGerichtHinzufuegenHeader.text = getString(R.string.fragment_five_add_multiple_days_title)
                    5 -> tvGerichtHinzufuegenHeader.text = getString(R.string.fragment_six_add_preperation_text_title)
                }
            }

        })

        progressBar.max = NUM_PAGES


    }

    override fun onBackPressed() {
        if (viewPager.currentItem == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed()
        } else {
            // Otherwise, select the previous step.
            viewPager.currentItem = viewPager.currentItem - 1
        }
    }


    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = NUM_PAGES

        override fun createFragment(position: Int): Fragment = ScreenSlidePageFragment()

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


}