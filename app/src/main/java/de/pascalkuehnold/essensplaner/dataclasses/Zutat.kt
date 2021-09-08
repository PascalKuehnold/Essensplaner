package de.pascalkuehnold.essensplaner.dataclasses

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(indices = [Index(value = [ "zutaten_name" ], unique = true)])
class Zutat(
    @PrimaryKey(autoGenerate = true) var id: Long,
    @ColumnInfo(name ="zutaten_name") var zutatenName: String,
    @ColumnInfo(name ="checked") var isChecked: Boolean


){
    companion object{
        fun createNewZutatenString(zutaten: List<String>): String {
            val newZutaten = zutaten.toMutableList()

            val stringBuilder = StringBuilder()
            for (element: String in newZutaten) {
                if(element.startsWith("`") && element.endsWith("´")){
                    stringBuilder.append(element)
                } else {
                    stringBuilder.append("$element,")
                }

            }
            if (stringBuilder.endsWith(",")) {
                stringBuilder.deleteCharAt(stringBuilder.length - 1)
            }
            return stringBuilder.toString()
        }

    }
}
