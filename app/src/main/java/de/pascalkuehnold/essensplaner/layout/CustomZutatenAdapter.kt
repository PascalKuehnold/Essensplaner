package de.pascalkuehnold.essensplaner.layout

import android.annotation.SuppressLint
import android.content.Context
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import de.pascalkuehnold.essensplaner.R
import de.pascalkuehnold.essensplaner.activities.GerichtEditierenActivity

class CustomZutatenAdapter(context: Context, zutaten: ArrayList<String>): BaseAdapter(), ListAdapter {
    private val mZutaten = zutaten
    private val mContext = context
    private var isSaved = false

    override fun getCount(): Int {
        return mZutaten.size
    }

    override fun getItem(position: Int): Any {
        return mZutaten[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    @SuppressLint("InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var view = convertView
        if(view == null){
            val inflater: LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.custom_list_item_zutat, null)
        }

        val zutatenName = view?.findViewById<TextView>(R.id.zutatenName)
        if (zutatenName != null) {
            zutatenName.text = mZutaten[position]
        }


        val btnZutatBearbeiten = view?.findViewById<Button>(R.id.btnZutatBearbeiten)
        btnZutatBearbeiten?.setOnClickListener {
            if(mContext is GerichtEditierenActivity){
                createChangeZutatDialog(mZutaten, position)
            }

        }

        val btnDeleteZutat = view?.findViewById<Button>(R.id.btnDeleteZutat)
        btnDeleteZutat?.setOnClickListener{
            if(mContext is GerichtEditierenActivity){
                GerichtEditierenActivity().deleteZutat(mZutaten, position)
            }
        }

        return view
    }

    //Method that creates a dialog for changing an ingredient
    private fun createChangeZutatDialog(zutatenNew: ArrayList<String>, position: Int) {
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
            updateZutat(inputText, zutatenNew, position)
        }

        builder.setNegativeButton(R.string.abbrechen) { dialog, _ ->
            dialog.cancel()
        }
        // Create the AlertDialog object and return it
        builder.create()
        builder.show()
    }

    //Method for updating the ingredient and refresh the ingredient listview
    private fun updateZutat(inputText: String, zutaten: ArrayList<String>, position: Int){
        val tempZutat = zutaten[position]

        zutaten[position] = inputText

        val tempZutatenString = createNewZutatenString(zutaten)
        (mContext as GerichtEditierenActivity).changeGericht(tempZutatenString)

        Toast.makeText(mContext, ("TODO()004 $tempZutat wurde erfolgreich zu $inputText bearbeitet."), Toast.LENGTH_SHORT).show()
        notifyDataSetChanged()
    }

    //Method for creating ingredient string, after it was edited by the user
    private fun createNewZutatenString(zutaten: List<String>): String {
        val newZutaten = zutaten.toMutableList()

        val stringBuilder = StringBuilder()
        for (element: String in newZutaten) {
            stringBuilder.append("$element,")
        }
        if (stringBuilder.endsWith(",")) {
            stringBuilder.deleteCharAt(stringBuilder.length - 1)
        }
        return stringBuilder.toString()
    }



}