package de.pascalkuehnold.essensplaner.dataclasses

import android.content.Context
import android.content.DialogInterface
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.room.*
import de.pascalkuehnold.essensplaner.R
import de.pascalkuehnold.essensplaner.activities.GerichtEditierenActivity
import de.pascalkuehnold.essensplaner.layout.CustomZutatenAdapter


@Entity(indices = [Index(value = [ "zutaten_name" ], unique = true)])
class Zutat(
    @PrimaryKey(autoGenerate = true) var id: Long,
    @ColumnInfo(name ="zutaten_name") var zutatenName: String,
    @ColumnInfo(name ="checked") var isChecked: Boolean = false


){
    companion object{
        fun createNewZutatenString(zutaten: List<String>): String {
            val newZutaten = zutaten.toMutableList()

            val stringBuilder = StringBuilder()
            for (element: String in newZutaten) {
                if(element.startsWith("`") && element.endsWith("´")){
                    stringBuilder.append(element)
                }

            }
            if (stringBuilder.endsWith(",")) {
                stringBuilder.deleteCharAt(stringBuilder.length - 1)
            }
            return stringBuilder.toString()
        }

        fun allIngredientsAsList(alleZutaten: String): List<String> {
            return if (alleZutaten.startsWith("`")) {
                alleZutaten.split("´")
            } else {
                alleZutaten.split(",")
            }
        }

        //Method for updating the ingredient and refresh the ingredient listview
        private fun updateZutat(mContext: Context, inputText: String, zutaten: ArrayList<Zutat>, position: Int){
            val tempZutat = zutaten[position]

            zutaten[position].zutatenName = inputText

            (mContext as GerichtEditierenActivity).changeGericht(zutaten)

            Toast.makeText(mContext, ("TODO()004 $tempZutat wurde erfolgreich zu $inputText bearbeitet."), Toast.LENGTH_SHORT).show()
        }

        fun deleteZutat(mContext: Context, mZutaten: ArrayList<Zutat>, position: Int, customZutatenAdapter: CustomZutatenAdapter){
            val tempZutat = mZutaten[position]

            val alert = AlertDialog.Builder(mContext)
            alert.setMessage("TODO()001 Delete?")
            alert.setPositiveButton(R.string.yes){ _: DialogInterface, _: Int ->
                mZutaten.removeAt(position)
                (mContext as GerichtEditierenActivity).changeGericht(mZutaten)
                Toast.makeText(mContext, ("TODO()002 $tempZutat was deleted successfully."), Toast.LENGTH_SHORT).show()
                customZutatenAdapter.notifyDataSetChanged()
            }
            alert.setNegativeButton(R.string.no){ dialog: DialogInterface, _: Int ->
                dialog.cancel()
            }
            alert.create()
            alert.show()

        }

        //Method that creates a dialog for changing an ingredient
        fun createChangeZutatDialog(mContext: Context, zutatenNew: ArrayList<Zutat>, position: Int, customZutatenAdapter: CustomZutatenAdapter) {
            // Use the Builder class for convenient dialog construction
            var inputText: String

            val builder = AlertDialog.Builder(mContext)
            builder.setTitle(mContext.getString(R.string.textZutatBearbeiten))

            val input = EditText(mContext)
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(input)

            builder.setPositiveButton(R.string.aendern) { _, _ ->
                inputText = input.text.toString()
                updateZutat(mContext, inputText, zutatenNew, position)
                customZutatenAdapter.notifyDataSetChanged()
            }

            builder.setNegativeButton(R.string.abbrechen) { dialog, _ ->
                dialog.cancel()
            }
            // Create the AlertDialog object and return it
            builder.create()
            builder.show()
        }

        fun generateIngredientsList(mealIngredients: String): ArrayList<String> {
            var zutaten: ArrayList<String>
            try {
                zutaten = if (mealIngredients.startsWith("`")) {
                    mealIngredients.split("´") as ArrayList<String>
                } else {
                    mealIngredients.split(",") as ArrayList<String>
                }

                zutaten.removeAll(listOf(null, ""))
            } catch (e: Exception) {
                zutaten = ArrayList()
                if (mealIngredients.isNotEmpty()) {
                    zutaten.add(mealIngredients)
                }
            }
            return zutaten
        }

    }

    override fun toString(): String {
        return zutatenName
    }


}


