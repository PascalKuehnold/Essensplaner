package de.pascalkuehnold.essensplaner.interfaces

import androidx.room.Insert
import de.pascalkuehnold.essensplaner.dataclasses.Gericht

/**
 * Created by Kalle on 2/20/2022.
 */
interface WochenplanerI {

    @Insert
    suspend fun insert(gericht: Gericht)
}