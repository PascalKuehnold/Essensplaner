package de.pascalkuehnold.essensplaner.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.pascalkuehnold.essensplaner.dataclasses.Gericht
import de.pascalkuehnold.essensplaner.dataclasses.ZutatTypeConverter
import de.pascalkuehnold.essensplaner.interfaces.GerichtDao

@Database(entities = [Gericht::class], version = 1, exportSchema = false)
@TypeConverters(ZutatTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gerichtDao(): GerichtDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context) : AppDatabase{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "database-name"
                ).allowMainThreadQueries().build()
                INSTANCE = instance
                return instance
            }
        }

    }
}