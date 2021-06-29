package de.pascalkuehnold.essensplaner.database

import android.content.Context
import androidx.room.Room
import de.pascalkuehnold.essensplaner.interfaces.GerichtDao

class DatabaseCon(databaseNameNew: String){
    private var databaseName = databaseNameNew
    var db: AppDatabase? = null


    fun createConnection(applicationContext: Context){
        db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, databaseName
        ).allowMainThreadQueries().build()
        println("DatabaseCon >> database connection established")
    }

    fun getGerichtDao(): GerichtDao? {
        if(db != null){
            return db!!.gerichtDao()
        }
        return null
    }

    fun close(){
        db?.close()
    }

    fun deleteCompleteDatabase(){
        //TODO sure to delete the database?
        getGerichtDao()!!.delete()
        println("DatabaseCon >> Database was successfully deleted")
        db!!.close()
    }


}