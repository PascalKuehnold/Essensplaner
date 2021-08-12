package de.pascalkuehnold.essensplaner.dataclasses

import androidx.room.*


@Entity
data class Gericht(

        @PrimaryKey(autoGenerate = true) var id: Long,
        @ColumnInfo(name = "gericht_name") var gerichtName: String,
        @ColumnInfo(name = "zutaten_liste") val zutaten: String,
        @ColumnInfo(name = "vegetarisch") val isVegetarisch: Boolean,
        @ColumnInfo(name = "mehrere_tage") val mehrereTage: Boolean,
        @ColumnInfo(name = "schnelles_gericht") val schnellesGericht: Boolean
)






