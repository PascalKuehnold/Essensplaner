package de.pascalkuehnold.essensplaner.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import de.pascalkuehnold.essensplaner.R
import de.pascalkuehnold.essensplaner.database.EinkaufslisteDatabase
import de.pascalkuehnold.essensplaner.dataclasses.Zutat
import de.pascalkuehnold.essensplaner.interfaces.EinkaufslisteDao
import java.util.*
import kotlin.collections.ArrayList


class EinkaufslisteActivity : AppCompatActivity(), View.OnClickListener, AbsListView.OnScrollListener{
    private lateinit var listEinkaufsliste: ListView
    private lateinit var einkaufsliste: ArrayList<Zutat>
    private lateinit var btnListDelete: Button
    private lateinit var btnAddItem: Button
    private lateinit var textViewLeftPositions: TextView
    private lateinit var addNewPositionTextField: EditText

    private var firstVisibleRow: Int = 0
    private var lastVisibleRow: Int = 0

    private var leftItems: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_einkaufsliste)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.einkaufsliste)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#266799")))

        listEinkaufsliste = findViewById(R.id.listViewEinkaufsliste)
        listEinkaufsliste.setOnScrollListener(this)

        btnListDelete = findViewById(R.id.btnDeleteList)
        btnAddItem = findViewById(R.id.btnAddItem)
        addNewPositionTextField = findViewById(R.id.newPositionEditText)
        textViewLeftPositions = findViewById(R.id.leftPositions)

        addNewPositionTextField.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                // If the event is a key-down event on the "enter" button
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    addNewItem()
                    if (v != null) {
                        hideSoftKeyboard(v)
                    }
                    return true
                }
                return false
            }
        })


        btnListDelete.setOnClickListener{
            val alert = AlertDialog.Builder(this)
            alert.setTitle(getString(R.string.delete))
            alert.setMessage("TODO()010 -> Einkaufsliste wird gelöscht. Fortfahren?")
            alert.setPositiveButton(getString(R.string.yes)) { _: DialogInterface, _: Int ->
                createConnection().delete()
                loadEinkaufsliste()
                generateListOnScreen()
            }
            alert.setNegativeButton(getString(R.string.no)){ dialog: DialogInterface, _: Int ->
                dialog.cancel()
            }
            alert.create()
            alert.show()


        }

        btnAddItem.setOnClickListener{
            addNewItem()
            hideSoftKeyboard(it)
        }

        loadEinkaufsliste()
        generateListOnScreen()
    }

    private fun hideSoftKeyboard(view: View) {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun createConnection(): EinkaufslisteDao {
        return EinkaufslisteDatabase.getDatabase(applicationContext).einkaufslisteDao()
    }

    private fun addNewItem(){
        if(!addNewPositionTextField.text.isNullOrEmpty()){

            val reg = Regex("\\s*,\\s*")
            val inputText = addNewPositionTextField.text.toString().trim().replace(reg, "\n")

            //val items = inputText.split("\\s*,\\s*")
            val items = inputText.lines()

            for(item in items){
                val capItem = item.capitalize(Locale.getDefault())
                val tempZutat = Zutat(0, capItem, false)
                addItem(tempZutat)
                addNewPositionTextField.text!!.clear()
                Toast.makeText(this, "Item" + " " + capItem + " " + getString(R.string.addedSuccessfully), Toast.LENGTH_SHORT).show()
            }


        } else {
            Toast.makeText(this, "TODO()005 Keine Eingabe...", Toast.LENGTH_SHORT).show()
        }

        addNewPositionTextField.clearFocus()

    }

    private fun loadEinkaufsliste(){
        val einkaufslisteDao = createConnection()

        einkaufsliste = einkaufslisteDao.getAll() as ArrayList<Zutat>


        for(pos: Zutat in einkaufsliste){
            if(!pos.isChecked){
                leftItems++
            }
        }
        val tempString = String.format(getString(R.string.leftPositions), leftItems)


        textViewLeftPositions.text = tempString

        leftItems = 0
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

    private fun addItem(zutat: Zutat){
        val einkaufsliste = createConnection()
        einkaufsliste.insertAll(zutat)

        loadEinkaufsliste()
        generateListOnScreen()

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
            generateListOnScreen()

            listEinkaufsliste.setSelection(firstVisibleRow)
        }
    }


    inner class CustomZutatenAdapterEinkaufsliste(context: Context, zutaten: ArrayList<Zutat>, callback: View.OnClickListener): BaseAdapter(), ListAdapter {
        private val mZutaten = zutaten
        private val mContext = context
        private val mCallback = callback

        private val typeFace = ResourcesCompat.getFont(context, R.font.architects_daughter_regular)

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

            //Changing the height of the single positions in the shopping list
            val zutatLayout = view?.findViewById<LinearLayout>(R.id.zutatLayout)
            if (zutatLayout != null){
                zutatLayout.layoutParams.height = resources.getDimensionPixelSize(R.dimen.positionShoppinglistSize)
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
                zutatenName.typeface = typeFace
                zutatenName.maxLines = 2
                zutatenName.text = mZutaten[position].zutatenName
                if(mZutaten[position].isChecked){
                    zutatenName.paintFlags = zutatenName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    zutatenName.paintFlags = 0
                }
            }


            val btnDeleteZutat = view?.findViewById<Button>(R.id.btnDeleteZutat)
            btnDeleteZutat?.setOnClickListener{
                if(mContext is EinkaufslisteActivity){
                    deleteZutat(position)
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

        private fun deleteZutat(position: Int){
            val tempZutat = mZutaten[position]
            val einkaufslisteDao = createConnection()

            val alert = AlertDialog.Builder(mContext)
            alert.setMessage("TODO()001 Delete?")
            alert.setPositiveButton(R.string.yes){ _: DialogInterface, _: Int ->
                mZutaten.removeAt(position)
                einkaufslisteDao.delete(tempZutat)
                notifyDataSetChanged()

                Toast.makeText(mContext, ("TODO()002 $tempZutat was deleted successfully."), Toast.LENGTH_SHORT).show()
            }
            alert.setNegativeButton(R.string.no){ dialog: DialogInterface, _: Int ->
                dialog.cancel()
            }
            alert.create()
            alert.show()
        }


    }

    override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {

    }

    override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
        firstVisibleRow = listEinkaufsliste.firstVisiblePosition
        lastVisibleRow = listEinkaufsliste.lastVisiblePosition
    }

}