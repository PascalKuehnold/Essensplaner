package de.pascalkuehnold.essensplaner.activities

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import de.pascalkuehnold.essensplaner.R
import de.pascalkuehnold.essensplaner.database.EinkaufslisteDatabase
import de.pascalkuehnold.essensplaner.dataclasses.Zutat
import de.pascalkuehnold.essensplaner.interfaces.EinkaufslisteDao
import de.pascalkuehnold.essensplaner.layout.CustomZutatenAdapter


class EinkaufslisteActivity : AppCompatActivity(), View.OnClickListener{
    lateinit var listEinkaufsliste: ListView
    lateinit var einkaufsliste: ArrayList<Zutat>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_einkaufsliste)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.einkaufsliste)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#266799")))

        listEinkaufsliste = findViewById(R.id.listViewEinkaufsliste)

        loadEinkaufsliste()
        generateListOnScreen()


    }

    private fun createConnection(): EinkaufslisteDao {
        return EinkaufslisteDatabase.getDatabase(applicationContext).einkaufslisteDao()
    }

    fun loadEinkaufsliste(){
        val einkaufslisteDao = createConnection()

        einkaufsliste = einkaufslisteDao.getAll() as ArrayList<Zutat>
        println("Wochenplaner >> loadWeekgerichte() -> Daten wurden geladen")


    }


    private fun generateListOnScreen(){
        val einkaufslisteString: ArrayList<Zutat> = ArrayList()

        for(zutat in einkaufsliste){
            einkaufslisteString.add(zutat)
        }

        val adapter = CustomZutatenAdapterEinkaufsliste(this, einkaufslisteString, this)
        listEinkaufsliste.adapter = adapter

        adapter.notifyDataSetChanged()


        println("Wochenplaner >> generateListOnScreen() -> Daten wurden an den Screen übergeben")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        val zutatTextview = v?.findViewById<TextView>(R.id.zutatenName)
        val imageViewZutatChecked = v?.findViewById<ImageView>(R.id.imageViewCheckedZutat)

        val zutat =  einkaufsliste[v?.tag as Int]
        val tmpZutatName = zutat.zutatenName
        var mIsChecked = zutat.isChecked

        if (zutatTextview != null) {
            if(mIsChecked){
                zutatTextview.paintFlags = 0
                mIsChecked = false
                imageViewZutatChecked!!.visibility = View.INVISIBLE
            } else {
                zutatTextview.paintFlags = zutatTextview.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                mIsChecked = true
                imageViewZutatChecked!!.visibility = View.VISIBLE
            }


            val tmpZutat = Zutat(zutat.id, tmpZutatName, isChecked = mIsChecked)

            createConnection().update(zutat = tmpZutat)

            loadEinkaufsliste()
        }
    }

    fun saveZutat(){

    }

    override fun onBackPressed() {
        super.onBackPressed()

    }

    inner class CustomZutatenAdapterEinkaufsliste(context: Context, zutaten: ArrayList<Zutat>, callback: View.OnClickListener): BaseAdapter(), ListAdapter {
        private val mZutaten = zutaten
        private val mContext = context
        private val mCallback = callback
        private var isChecked = false

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

            val imageViewZutatChecked = view?.findViewById<ImageView>(R.id.imageViewCheckedZutat)
            if (imageViewZutatChecked != null) {
                if(mZutaten[position].isChecked){
                    imageViewZutatChecked.visibility = View.VISIBLE
                } else {
                    imageViewZutatChecked.visibility = View.INVISIBLE
                }
            }


            val row = view?.findViewById<LinearLayout>(R.id.rowClick)
            row?.setOnClickListener(mCallback)
            row?.tag = position

            val zutatenName = view?.findViewById<TextView>(R.id.zutatenName)
            if (zutatenName != null) {
                zutatenName.text = mZutaten[position].zutatenName
                if(mZutaten[position].isChecked){
                    zutatenName.paintFlags = zutatenName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                }
            }



            return view
        }

        //Method that creates a dialog for changing an ingredient
        private fun createChangeZutatDialog(zutatenNew: ArrayList<Zutat>, position: Int) {
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
        private fun updateZutat(inputText: String, zutaten: ArrayList<Zutat>, position: Int){
            val tempZutat = zutaten[position]

            zutaten[position].zutatenName = inputText

            val tempZutatenString = createNewZutatenString(zutaten)
            (mContext as GerichtEditierenActivity).changeGericht(tempZutatenString)

            Toast.makeText(mContext, ("TODO()004 $tempZutat wurde erfolgreich zu $inputText bearbeitet."), Toast.LENGTH_SHORT).show()
            notifyDataSetChanged()
        }

        //Method for creating ingredient string, after it was edited by the user
        private fun createNewZutatenString(zutaten: List<Zutat>): String {
            val newZutaten = zutaten.toMutableList()

            val stringBuilder = StringBuilder()
            for (element: Zutat in newZutaten) {
                stringBuilder.append("${element.zutatenName},")
            }
            if (stringBuilder.endsWith(",")) {
                stringBuilder.deleteCharAt(stringBuilder.length - 1)
            }
            return stringBuilder.toString()
        }



    }

}