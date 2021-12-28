package de.pascalkuehnold.essensplaner.dataclasses

import android.content.Context
import android.content.DialogInterface
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import de.pascalkuehnold.essensplaner.R
import de.pascalkuehnold.essensplaner.activities.GerichtEditierenActivity
import de.pascalkuehnold.essensplaner.layout.CustomZutatenAdapter


@Entity(indices = [Index(value = ["zutaten_name"], unique = true)])
class Zutat(
    @PrimaryKey(autoGenerate = true) var id: Long,
    @ColumnInfo(name = "zutaten_name") var zutatenName: String,
    @ColumnInfo(name = "checked") var isChecked: Boolean = false,
    @ColumnInfo(name = "zutaten_einheit") var zutatenMengenEinheit: String = "",
    @ColumnInfo(name = "zutaten_menge") var zutatenMenge: Double = 0.0


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
        private fun updateZutat(
            mContext: Context,
            inputText: String,
            zutaten: ArrayList<Zutat>,
            position: Int,
            mengenEinheit: String,
            menge: Int
        ){
            val tempZutat = zutaten[position]

            zutaten[position].zutatenName = inputText
            zutaten[position].zutatenMengenEinheit = mengenEinheit
            zutaten[position].zutatenMenge = menge.toDouble()

            (mContext as GerichtEditierenActivity).changeGericht(zutaten)

            Toast.makeText(
                mContext,
                ("TODO()004 $tempZutat wurde erfolgreich zu $inputText bearbeitet."),
                Toast.LENGTH_SHORT
            ).show()
        }

        fun deleteZutat(
            mContext: Context,
            mZutaten: ArrayList<Zutat>,
            position: Int,
            customZutatenAdapter: CustomZutatenAdapter
        ){
            val tempZutat = mZutaten[position]

            val alert = AlertDialog.Builder(mContext)
            alert.setMessage("TODO()001 Delete?")
            alert.setPositiveButton(R.string.yes){ _: DialogInterface, _: Int ->
                mZutaten.removeAt(position)
                (mContext as GerichtEditierenActivity).changeGericht(mZutaten)
                Toast.makeText(
                    mContext,
                    ("TODO()002 $tempZutat was deleted successfully."),
                    Toast.LENGTH_SHORT
                ).show()
                customZutatenAdapter.notifyDataSetChanged()
            }
            alert.setNegativeButton(R.string.no){ dialog: DialogInterface, _: Int ->
                dialog.cancel()
            }
            alert.create()
            alert.show()

        }

        //Method that creates a dialog for changing an ingredient
        fun createChangeZutatDialog(
            mContext: Context,
            zutatenNew: ArrayList<Zutat>,
            position: Int,
            customZutatenAdapter: CustomZutatenAdapter
        ) {

            val builder = AlertDialog.Builder(mContext)

            val v: View = View.inflate(mContext,R.layout.edit_ingredient_layout, null)

            builder.setView(v)

            // Create the AlertDialog object and return it
            val alert = builder.create()

            val editTextZutatenName = v.findViewById<EditText>(R.id.editTextZutatenName)
            editTextZutatenName?.hint = zutatenNew[position].zutatenName

            val editTextZutatenMenge = v.findViewById<EditText>(R.id.editTextMenge)
            editTextZutatenMenge?.hint = zutatenNew[position].zutatenMenge.toInt().toString()

            val editTextZutatenMengenEinheit = v.findViewById<EditText>(R.id.editTextZutatenEinheit)
            editTextZutatenMengenEinheit?.hint = zutatenNew[position].zutatenMengenEinheit

            val btnChangeIngredient = v.findViewById<Button>(R.id.btnChangeIngredient)
            btnChangeIngredient?.setOnClickListener {
                val zutatenName = if(editTextZutatenName?.text.isNullOrEmpty()){
                    zutatenNew[position].zutatenName
                } else {
                    editTextZutatenName?.text.toString()
                }

                val zutatenMenge = if(editTextZutatenMenge?.text.isNullOrEmpty()){
                    zutatenNew[position].zutatenMenge.toInt().toString()
                } else {
                    editTextZutatenMenge?.text.toString()
                }

                val zutatenMengenEinheit = if(editTextZutatenMengenEinheit?.text.isNullOrEmpty()){
                    zutatenNew[position].zutatenMengenEinheit
                } else {
                    editTextZutatenMengenEinheit?.text.toString()
                }

                updateZutat(
                    mContext,
                    zutatenName,
                    zutatenNew,
                    position,
                    zutatenMengenEinheit,
                    Integer.parseInt(
                        zutatenMenge
                    )
                )
                customZutatenAdapter.notifyDataSetChanged()

            }

            val btnDeleteIngredient = v.findViewById<Button>(R.id.btnDeleteZutat)
            btnDeleteIngredient?.setOnClickListener{
                deleteZutat(mContext, zutatenNew, position, customZutatenAdapter)
            }

            val btnCancel = v.findViewById<Button>(R.id.cancel_button)
            btnCancel?.setOnClickListener{
                alert.cancel()
            }

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


