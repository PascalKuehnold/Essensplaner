package de.pascalkuehnold.essensplaner.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import de.pascalkuehnold.essensplaner.dataclasses.Gericht
import de.pascalkuehnold.essensplaner.interfaces.WochenplanerVeggieDao

@Database(entities = [Gericht::class], version = 1, exportSchema = false)
abstract class WochenplanerVeggieDatabase : RoomDatabase() {
    abstract fun wochenGerichteVeggieDao(): WochenplanerVeggieDao


    companion object {
        @Volatile
        private var INSTANCE: WochenplanerVeggieDatabase? = null

        fun getDatabase(context: Context) : WochenplanerVeggieDatabase{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        WochenplanerVeggieDatabase::class.java,
                        "wochenplanVeggie.db"
                ).allowMainThreadQueries().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}