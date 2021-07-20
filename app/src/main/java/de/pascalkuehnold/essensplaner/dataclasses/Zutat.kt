package de.pascalkuehnold.essensplaner.dataclasses

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Zutat(
    @PrimaryKey(autoGenerate = true) var id: Long,
    @ColumnInfo(name ="zutaten_name") var zutatenName: String


)
