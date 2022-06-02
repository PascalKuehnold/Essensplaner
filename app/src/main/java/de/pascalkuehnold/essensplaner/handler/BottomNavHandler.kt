package de.pascalkuehnold.essensplaner.handler

import android.content.Context
import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView
import de.pascalkuehnold.essensplaner.R
import de.pascalkuehnold.essensplaner.activities.EinkaufslisteActivity
import de.pascalkuehnold.essensplaner.activities.GerichteListeActivity
import de.pascalkuehnold.essensplaner.activities.Wochenplaner

/**
 * Created by Kalle on 6/2/2022.
 */
class BottomNavHandler(context: Context, v: BottomNavigationView){
    var bottomNavigationView = v
    var mContext = context


    fun createBottomNavHandler(){
        bottomNavigationView.setOnItemSelectedListener { v ->
            when(v.itemId){
                R.id.navMealList -> {
                    mContext.startActivity(Intent(mContext, GerichteListeActivity::class.java))
                    true
                }
            }

            when(v.itemId){
                R.id.navWeeklyPlanner -> {
                    mContext.startActivity(Intent(mContext, Wochenplaner::class.java))
                    true
                }
            }

            when(v.itemId){
                R.id.navShoppingList -> {
                    mContext.startActivity(Intent(mContext, EinkaufslisteActivity::class.java))
                    true
                }
            }

            when(v.itemId){
                R.id.navGroup -> {
                    true
                    //TODO CREATE GROUP CLASS
                }
                else -> false
            }
        }
    }




}