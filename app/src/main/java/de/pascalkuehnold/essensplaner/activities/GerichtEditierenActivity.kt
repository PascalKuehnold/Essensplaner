package de.pascalkuehnold.essensplaner.activities


import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import de.pascalkuehnold.essensplaner.R
import de.pascalkuehnold.essensplaner.database.AppDatabase
import de.pascalkuehnold.essensplaner.database.WochenplanerDatabase
import de.pascalkuehnold.essensplaner.dataclasses.Gericht
import kotlinx.coroutines.runBlocking


//TODO() GerichtNamen editieren möglich machen
class GerichtEditierenActivity : AppCompatActivity() {
    private lateinit var inputFieldGericht: TextInputEditText
    private lateinit var oldGerichtName: TextView
    private lateinit var listViewZutaten: ListView
    private lateinit var switchVegetarisch: CheckBox
    private lateinit var btnSubmit: Button
    private lateinit var btnDeleteGericht: AppCompatButton
    private lateinit var btnZutatHinzufuegen: FloatingActionButton


    private var gerichtName =""
    private var zutatenListe = ""
    private var isVegetarisch = false
    private var newGericht: Gericht? = null
    private lateinit var adapter: CustomZutatenAdapter

    private lateinit var zutaten: ArrayList<String>

    private var isSaved = false
    private var gerichtID: Long = -1

    lateinit var mContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gericht_editieren)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.gericht_bearbeiten)

        mContext = this

        inputFieldGericht = findViewById(R.id.textGerichtEditierenName)
        oldGerichtName = findViewById(R.id.gerichtAlterName)
        listViewZutaten = findViewById(R.id.listViewZutatenlisteGerichtAendern)
        switchVegetarisch = findViewById(R.id.switchGerichteEditierenVegetarisch)
        btnSubmit = findViewById(R.id.btnSubmit)
        btnDeleteGericht = findViewById(R.id.btnDeleteGericht)
        btnZutatHinzufuegen = findViewById(R.id.btnAddZutat)


        btnSubmit.setOnClickListener{
            if(newGericht == null){
                Toast.makeText(this, ("Es wurden keine Änderungen vorgenommen."), Toast.LENGTH_SHORT).show()
            } else {
                saveEditedGericht()
                Toast.makeText(this, (newGericht!!.gerichtName + " wurde erfolgreich bearbeitet."), Toast.LENGTH_SHORT).show()
            }
        }

        btnZutatHinzufuegen.setOnClickListener{
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.textZutatHinzufuegen))

            val input = EditText(this)
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(input)
            input.requestFocus()

            builder.setPositiveButton(R.string.hinzuf_gen) { _, _ ->
                val inputText = input.text.toString().replace(',', ' ').trim()
                inputText.split("\\s*,\\s*")

                zutaten.add(inputText)
                changeGericht(zutaten)
                isSaved = false

                adapter.notifyDataSetChanged()
                Toast.makeText(this, getString(R.string.zutat) + " " + inputText + " " + getString(R.string.addedSuccessfully), Toast.LENGTH_SHORT).show()
            }

            builder.setNegativeButton(R.string.abbrechen) { dialog, _ ->
                dialog.cancel()
            }
            // Create the AlertDialog object and return it


            builder.create()
            val alert = builder.show()
            alert.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }


        val gericht: Bundle? = intent.extras

        if(gericht != null){
            gerichtID = gericht.getLong("ID")

            val tempGericht = AppDatabase.getDatabase(applicationContext).gerichtDao().loadByID(gerichtID)

            if (tempGericht != null) {
                oldGerichtName.text = tempGericht.gerichtName
                gerichtName = tempGericht.gerichtName
                zutatenListe = tempGericht.zutaten
                isVegetarisch = tempGericht.isVegetarisch
            }

            switchVegetarisch.isChecked = isVegetarisch
        }

        //TODO Method for changing the meal name

        //sets the hint text of the meal name
        inputFieldGericht.hint = gerichtName

        //fills the array of ingredients by seperating it
        //if there is nothing to seperate, the array is filled with one item
        try{
            zutaten = zutatenListe.split(",") as ArrayList<String>
        } catch (e: Exception){
            zutaten = ArrayList()
            zutaten.add(zutatenListe)
        }

        //for the checkbox if the meal is vegetarian or not
        switchVegetarisch.setOnCheckedChangeListener{ _, isChecked ->
            isVegetarisch = isChecked
            isSaved = false
            changeGericht(zutaten)
        }

        //creates the custom adapter
        adapter = CustomZutatenAdapter(zutaten)

        //setting the adapter for the listview of ingredients
        listViewZutaten.adapter = adapter

        //Creates an FocusChangeListener and hides the keyboard is the inputfield is no longer focused
        inputFieldGericht.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideSoftKeyboard(v)
                gerichtName = inputFieldGericht.text.toString()
                isSaved = false
                changeGericht(zutaten)
            }
        }

        //OnClickListener for the Delete Meal Button
        //Creates a Dialog so that the user can be sure if he wants to delete the entire meal
        btnDeleteGericht.setOnClickListener{
            val alert = AlertDialog.Builder(this)
            alert.setTitle(getString(R.string.deleteGericht))
            alert.setIcon(R.drawable.ic_delete)

            alert.setMessage(getString(R.string.deleteGerichtText))
                    .setPositiveButton(R.string.delete) { _: DialogInterface, _: Int ->
                        val tempDao = AppDatabase.getDatabase(applicationContext).gerichtDao()
                        val tempGericht = tempDao.findByName(gerichtName)

                        val wochenPlanerDao = WochenplanerDatabase.getDatabase(applicationContext).wochenGerichteDao()

                        tempDao.delete(tempGericht)
                        wochenPlanerDao.delete(tempGericht)

                        if(wochenPlanerDao.getAll().size <= 7){
                            Toast.makeText(this, "TODO()007 Not enough meals for 7 days...", Toast.LENGTH_SHORT).show()
                        }

                        Toast.makeText(this, "TODO()000 Gericht was successfully deleted", Toast.LENGTH_SHORT).show()
                        waitForToastShortThread.start()
                    }
                    .setNegativeButton(R.string.cancel) { dialog: DialogInterface, _: Int ->
                        dialog.cancel()
                    }
            alert.create()
            alert.show()
        }

    }

    //Method for changing the entire meal
    private fun changeGericht(inZutaten: ArrayList<String>) {
        val tempZutaten = createNewZutatenString(inZutaten)

        newGericht = Gericht(gerichtID, gerichtName, tempZutaten, isVegetarisch)
    }

    //Method for waiting an amount of Toast.LENGTH_SHORT, before leaving the activity
    private var waitForToastShortThread: Thread = object : Thread() {
        override fun run() {
            try {
                sleep(Toast.LENGTH_SHORT.toLong())
                finish()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    //Method for hiding the keyboard after pressing on an empty space on screen
    private fun hideSoftKeyboard(view: View) {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    //method for saving the meal
    private fun saveEditedGericht() = runBlocking {
        AppDatabase.getDatabase(applicationContext).gerichtDao().update(gericht = newGericht!!)
        WochenplanerDatabase.getDatabase(applicationContext).wochenGerichteDao().update(gericht = newGericht!!)
        isSaved = true
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

    //Method that creates a dialog for changing an ingredient
    private fun createChangeZutatDialog(zutatenNew: ArrayList<String>, position: Int) {
        // Use the Builder class for convenient dialog construction
        var inputText: String
       

        val builder = AlertDialog.Builder(mContext)
        builder.setTitle(getString(R.string.textZutatBearbeiten))

        val input = EditText(mContext)
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton(R.string.aendern) { _, _ ->
            inputText = input.text.toString()
            updateZutat(inputText, zutatenNew, position)
            isSaved = false
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

        createNewZutatenString(zutaten)
        changeGericht(zutaten)

        Toast.makeText(this, ("TODO()004 $tempZutat wurde erfolgreich zu $inputText bearbeitet."), Toast.LENGTH_SHORT).show()
        adapter.notifyDataSetChanged()
    }

    //Method was modified to alert the user if the changes were not saved
    override fun onBackPressed() {
        if(!inputFieldGericht.text.isNullOrBlank()){
            gerichtName = inputFieldGericht.text.toString()
            changeGericht(zutaten)
        }


        if (newGericht != null && !isSaved){
            val builder = AlertDialog.Builder(this)
            builder.setTitle("TODO()003 Eingabe wurde nicht gespeichert.")
            builder.setMessage("TODO()004 Sollen die Änderungen gespeichert werden?")
            builder.setIcon(android.R.drawable.ic_dialog_alert)

            builder.setPositiveButton(R.string.yes) { _, _ ->

                saveEditedGericht()
                super.onBackPressed()
            }

            builder.setNegativeButton(R.string.no) { _, _ ->
                super.onBackPressed()
            }
            // Create the AlertDialog object and return it
            builder.create()
            builder.show()
        } else {
            super.onBackPressed()
        }

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

    //Inner class for the CustomArrayAdapter for the listItems for the ingredients
    inner class CustomZutatenAdapter(zutaten: ArrayList<String>): BaseAdapter(), ListAdapter{
        private val mZutaten = zutaten

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
                createChangeZutatDialog(mZutaten, position)
            }

            val btnDeleteZutat = view?.findViewById<Button>(R.id.btnDeleteZutat)
            btnDeleteZutat?.setOnClickListener{
                deleteZutat(position)
            }

            return view
        }

        private fun deleteZutat(position: Int){
            val tempZutat = mZutaten[position]

            val alert = AlertDialog.Builder(mContext)
            alert.setMessage("TODO()001 Delete?")
            alert.setPositiveButton(R.string.yes){ _: DialogInterface, _: Int ->
                mZutaten.removeAt(position)
                adapter.notifyDataSetChanged()
                isSaved = false
                changeGericht(mZutaten)

                Toast.makeText(mContext, ("TODO()002 $tempZutat was deleted successfully."), Toast.LENGTH_SHORT).show()
            }
            alert.setNegativeButton(R.string.no){ dialog: DialogInterface, _: Int ->
                dialog.cancel()
            }
            alert.create()
            alert.show()
        }
    }





}