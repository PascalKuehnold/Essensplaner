package de.pascalkuehnold.essensplaner.interfaces

import androidx.room.*
import de.pascalkuehnold.essensplaner.dataclasses.Gericht

@Dao
interface WochenplanerVeggieDao {
    @Query("SELECT * FROM gericht")
    fun getAll(): List<Gericht>

    @Query("SELECT * FROM gericht WHERE gericht_name LIKE :gerichtName")
    fun findByName(gerichtName: String): Gericht

    @Query("DELETE FROM gericht")
    fun delete()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg gerichte: Gericht)

    @Delete
    fun delete(gericht: Gericht)

    @Insert
    suspend fun insert(gericht: Gericht)

    @Update
    suspend fun update(gericht: Gericht)

}