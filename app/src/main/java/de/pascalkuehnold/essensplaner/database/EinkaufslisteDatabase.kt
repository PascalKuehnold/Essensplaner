package de.pascalkuehnold.essensplaner.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import de.pascalkuehnold.essensplaner.dataclasses.Gericht
import de.pascalkuehnold.essensplaner.dataclasses.Zutat
import de.pascalkuehnold.essensplaner.interfaces.EinkaufslisteDao
import de.pascalkuehnold.essensplaner.interfaces.GerichtDao

@Database(entities = [Zutat::class], version = 1, exportSchema = false)
abstract class EinkaufslisteDatabase : RoomDatabase() {
    abstract fun einkaufslisteDao(): EinkaufslisteDao


    companion object {
        @Volatile
        private var INSTANCE: EinkaufslisteDatabase? = null

        fun getDatabase(context: Context) : EinkaufslisteDatabase{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        EinkaufslisteDatabase::class.java,
                        "einkaufslisteDB"
                ).allowMainThreadQueries().build()
                INSTANCE = instance
                return instance
            }
        }

        fun getInstance(): EinkaufslisteDatabase? {
            return INSTANCE
        }
    }
}