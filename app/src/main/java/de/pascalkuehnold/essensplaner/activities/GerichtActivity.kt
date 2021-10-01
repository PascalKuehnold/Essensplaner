package de.pascalkuehnold.essensplaner.activities

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import de.pascalkuehnold.essensplaner.R


class GerichtActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gericht)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#266799")))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        gerichtName = intent.getStringExtra("mealName").toString()
        gerichtZutaten = intent.getStringExtra("mealIngredients").toString()
        zubereitungsText = intent.getStringExtra("mealRecipe").toString()
        gerichtAuthor = intent.getStringExtra("mealAuthor").toString()
        gerichtZubereitungsZeit = intent.getStringExtra("mealCookTime").toString()
        gerichtVonChefkoch = intent.getBooleanExtra("mealByChefkoch", false)
        chefkochUrl = intent.getStringExtra("chefkochUrl").toString()


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
                setOf(
                        R.id.navigation_home, R.id.navigation_notifications
                )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


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

    companion object{
        var gerichtName = ""
        var gerichtZutaten = ""
        var zubereitungsText = ""
        var gerichtAuthor = ""
        var gerichtZubereitungsZeit = ""
        var gerichtVonChefkoch = false
        var chefkochUrl = ""
    }


}