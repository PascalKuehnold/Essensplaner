package de.pascalkuehnold.essensplaner.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import de.pascalkuehnold.essensplaner.dataclasses.Gericht
import de.pascalkuehnold.essensplaner.interfaces.WochenplanerDao

@Database(entities = [Gericht::class], version = 1, exportSchema = false)
abstract class WochenplanerDatabase : RoomDatabase() {
    abstract fun wochenGerichteDao(): WochenplanerDao


    companion object {
        @Volatile
        private var INSTANCE: WochenplanerDatabase? = null

        fun getDatabase(context: Context) : WochenplanerDatabase{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        WochenplanerDatabase::class.java,
                        "wochenplan.db"
                ).allowMainThreadQueries().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}