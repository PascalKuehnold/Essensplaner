package de.pascalkuehnold.essensplaner.dataclasses

import android.content.Context
import android.widget.Toast
import androidx.room.*
import de.pascalkuehnold.essensplaner.R
import de.pascalkuehnold.essensplaner.database.AppDatabase
import de.pascalkuehnold.essensplaner.database.WochenplanerDatabase
import de.pascalkuehnold.essensplaner.database.WochenplanerVeggieDatabase
import de.pascalkuehnold.essensplaner.interfaces.GerichtDao
import kotlinx.coroutines.runBlocking
import java.util.*


@Entity
class Gericht(

        @PrimaryKey(autoGenerate = true) var id: Long,
        @ColumnInfo(name = "gericht_name") var gerichtName: String,
        @ColumnInfo(name = "zutaten_liste") val zutaten: String,
        @ColumnInfo(name = "vegetarisch") val isVegetarisch: Boolean,
        @ColumnInfo(name = "mehrere_tage") val mehrereTage: Boolean,
        @ColumnInfo(name = "schnelles_gericht") val schnellesGericht: Boolean,
        @ColumnInfo(name = "rezept") val gerichtRezept: String
){
    companion object{
        //method for saving the meal
        fun saveEditedGericht(context: Context, newGericht: Gericht) = runBlocking {
            AppDatabase.getDatabase(context).gerichtDao().update(gericht = newGericht)
            WochenplanerDatabase.getDatabase(context).wochenGerichteDao().update(gericht = newGericht)
            WochenplanerVeggieDatabase.getDatabase(context).wochenGerichteVeggieDao().update(gericht = newGericht)

        }

        fun addGericht(applicationContext: Context, mealName: String, zutaten: List<String>, mealIsVeggie: Boolean, mealIsForMultipleDays: Boolean, mealIsFastPrepared: Boolean, mealReceipt: String){
            val tempZutaten = Zutat.createNewZutatenString(zutaten)

            val gerichtDao = createConnection(applicationContext)
            val newGericht = Gericht(
                    0,
                    mealName.capitalize(Locale.getDefault()),
                    tempZutaten,
                    mealIsVeggie,
                    mealIsForMultipleDays,
                    mealIsFastPrepared,
                    mealReceipt
            )

            gerichtDao.insertAll(newGericht)
            println("GerichtHandler >> " + newGericht.gerichtName + " was added successfully")
            Toast.makeText(applicationContext, mealName + " " + (R.string.wasAddedText), Toast.LENGTH_SHORT).show()
        }

        private fun createConnection(applicationContext: Context): GerichtDao {
            return AppDatabase.getDatabase(applicationContext).gerichtDao()
        }

    }
}






