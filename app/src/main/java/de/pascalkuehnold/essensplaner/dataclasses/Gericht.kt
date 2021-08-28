package de.pascalkuehnold.essensplaner.dataclasses

import android.content.Context
import androidx.room.*
import de.pascalkuehnold.essensplaner.database.AppDatabase
import de.pascalkuehnold.essensplaner.database.WochenplanerDatabase
import de.pascalkuehnold.essensplaner.database.WochenplanerVeggieDatabase
import kotlinx.coroutines.runBlocking


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
    }
}






