package de.pascalkuehnold.essensplaner.interfaces

import androidx.room.*
import de.pascalkuehnold.essensplaner.dataclasses.Zutat

@Dao
interface EinkaufslisteDao {
    @Query("SELECT * FROM zutat")
    fun getAll(): List<Zutat>

    @Query("SELECT * FROM zutat WHERE zutaten_name LIKE :zutatName")
    fun findByName(zutatName: String): Zutat

    @Query("DELETE FROM zutat")
    fun delete()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg zutaten: Zutat)

    @Delete
    fun delete(zutat: Zutat)

    @Insert
    suspend fun insert(zutat: Zutat)

    @Update
    fun update(zutat: Zutat)

}