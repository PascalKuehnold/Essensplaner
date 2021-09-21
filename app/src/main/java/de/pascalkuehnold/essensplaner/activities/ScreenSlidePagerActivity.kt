package de.pascalkuehnold.essensplaner.activities

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
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
                        tvGerichtHinzufuegenHeader.text = "Gericht Namen hinzufügen"
                    }
                    1 -> {
                        tvGerichtHinzufuegenHeader.text = "Zutaten hinzufügen"
                    }
                    2 -> tvGerichtHinzufuegenHeader.text = "Kochzeit hinzufügen"
                    3 -> tvGerichtHinzufuegenHeader.text = "Vegetarisch/Vegan"
                    4 -> tvGerichtHinzufuegenHeader.text = "Für mehrere Tage geeignet"
                    5 -> tvGerichtHinzufuegenHeader.text = "Zubereitungstext eingeben"
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