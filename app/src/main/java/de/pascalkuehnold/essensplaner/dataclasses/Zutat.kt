package de.pascalkuehnold.essensplaner.dataclasses

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(indices = [Index(value = [ "zutaten_name" ], unique = true)])
data class Zutat(
    @PrimaryKey(autoGenerate = true) var id: Long,
    @ColumnInfo(name ="zutaten_name") var zutatenName: String,
    @ColumnInfo(name ="checked") var isChecked: Boolean


)
