package de.pascalkuehnold.essensplaner.dataclasses

import androidx.room.*
import java.lang.reflect.Type


@Entity
data class Gericht(

        @PrimaryKey(autoGenerate = true) var id: Long,
        @ColumnInfo(name = "gericht_name") var gerichtName: String,
        @ColumnInfo(name = "zutaten_liste") val zutaten: String,
        @ColumnInfo(name = "vegetarisch") val isVegetarisch: Boolean
)






