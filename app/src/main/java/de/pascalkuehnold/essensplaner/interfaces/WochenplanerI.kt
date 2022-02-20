package de.pascalkuehnold.essensplaner.interfaces

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import de.pascalkuehnold.essensplaner.dataclasses.Gericht

/**
 * Created by Kalle on 2/20/2022.
 */
interface WochenplanerI {
    @Update
    suspend fun update(gericht: Gericht)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(gericht: Gericht)
}