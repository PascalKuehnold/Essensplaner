package de.pascalkuehnold.essensplaner.dataclasses

import android.content.Context
import android.os.Parcelable
import android.widget.Toast
import androidx.room.*
import androidx.versionedparcelable.ParcelField
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import de.pascalkuehnold.essensplaner.R
import de.pascalkuehnold.essensplaner.database.AppDatabase
import de.pascalkuehnold.essensplaner.database.WochenplanerDatabase
import de.pascalkuehnold.essensplaner.database.WochenplanerVeggieDatabase
import de.pascalkuehnold.essensplaner.interfaces.GerichtDao
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.collections.ArrayList


@Entity
class Gericht(

        @PrimaryKey(autoGenerate = true) var id: Long,
        @ColumnInfo(name = "gericht_name") var gerichtName: String,
        @ColumnInfo(name = "vegetarisch") val isVegetarisch: Boolean,
        @ColumnInfo(name = "mehrere_tage") val mehrereTage: Boolean,
        @ColumnInfo(name = "schnelles_gericht") val schnellesGericht: Boolean,
        @ColumnInfo(name = "ist_chefkoch_gericht") val isChefkochGericht: Boolean,
        @ColumnInfo(name = "gesamt_kochzeit") val gesamtKochzeit: String,
        @ColumnInfo(name = "gericht_author") val gerichtAuthor: String,
        @ColumnInfo(name = "rezept") val gerichtRezept: String,
        @ColumnInfo(name = "chefkoch_url") val chefkochUrl: String,
        @ColumnInfo(name = "zutaten") val zutatenList: List<Zutat>
){
    companion object{
        //method for saving the meal
        fun saveEditedGericht(context: Context, newGericht: Gericht) = runBlocking {
            AppDatabase.getDatabase(context).gerichtDao().update(gericht = newGericht)
            WochenplanerDatabase.getDatabase(context).wochenGerichteDao().update(gericht = newGericht)
            WochenplanerVeggieDatabase.getDatabase(context).wochenGerichteVeggieDao().update(gericht = newGericht)

        }

        fun addGericht(applicationContext: Context,
                       mealName: String,
                       mealIsVeggie: Boolean,
                       mealIsForMultipleDays: Boolean,
                       mealIsFastPrepared: Boolean,
                       mealIsChefkochGericht: Boolean,
                       mealOverallCooktime: String,
                       mealAuthor: String,
                       mealReceipt: String,
                       chefkochUrl: String,
                       zutatenList: ArrayList<Zutat>
                        )
        {

            val gerichtDao = createConnection(applicationContext)
            val newGericht = Gericht(
                    0,
                    mealName.capitalize(Locale.getDefault()),
                    mealIsVeggie,
                    mealIsForMultipleDays,
                    mealIsFastPrepared,
                    mealIsChefkochGericht,
                    mealOverallCooktime,
                    mealAuthor,
                    mealReceipt,
                    chefkochUrl,
                    zutatenList
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

class ZutatTypeConverter {
    @TypeConverter
    fun listToJson(value: List<Zutat>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String) = ArrayList(Gson().fromJson(value, Array<Zutat>::class.java).toList())
}






