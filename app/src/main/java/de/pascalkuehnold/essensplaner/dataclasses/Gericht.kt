package de.pascalkuehnold.essensplaner.dataclasses

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Gericht(
        @PrimaryKey
        @ColumnInfo(name = "gericht_name") var gerichtName: String,
        @ColumnInfo(name = "zutaten_liste") val zutaten: String,
        @ColumnInfo(name = "vegetarisch") val isVegetarisch: Boolean
        )



