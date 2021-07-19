package de.pascalkuehnold.essensplaner.interfaces

import androidx.appcompat.app.AppCompatActivity


abstract class Wochenplan : AppCompatActivity(){
    abstract fun deleteDatabase()
    abstract fun saveWeekgerichte()
    abstract fun loadWeekgerichte()

}