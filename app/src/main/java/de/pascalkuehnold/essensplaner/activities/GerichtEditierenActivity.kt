package de.pascalkuehnold.essensplaner.activities


import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import de.pascalkuehnold.essensplaner.R
import de.pascalkuehnold.essensplaner.database.AppDatabase
import de.pascalkuehnold.essensplaner.dataclasses.Gericht
import kotlinx.coroutines.runBlocking


class GerichtEditierenActivity : AppCompatActivity() {
    private lateinit var inputFieldGericht: TextView
    private lateinit var oldGerichtName: TextView
    private lateinit var listViewZutaten: ListView
    private lateinit var switchVegetarisch: CheckBox
    private lateinit var btnSubmit: Button
    private lateinit var btnDeleteGericht: AppCompatButton

    private var gerichtName =""
    private var zutatenListe = ""
    private var isVegetarisch = false
    private var newGericht: Gericht? = null
    private lateinit var adapter: ArrayAdapter<String>

    private lateinit var zutaten: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gericht_editieren)

        inputFieldGericht = findViewById(R.id.textGerichtEditierenName)
        oldGerichtName = findViewById(R.id.gerichtAlterName)
        listViewZutaten = findViewById(R.id.listViewZutatenlisteGerichtAendern)
        switchVegetarisch = findViewById(R.id.switchGerichteEditierenVegetarisch)
        btnSubmit = findViewById(R.id.btnSubmit)
        btnDeleteGericht = findViewById(R.id.btnDeleteGericht)

        btnSubmit.setOnClickListener{
            if(newGericht == null){
                Toast.makeText(this, ("Es wurden keine Änderungen vorgenommen."), Toast.LENGTH_SHORT).show()
            } else {
                saveEditedGericht(newGericht!!)
                Toast.makeText(this, (newGericht!!.gerichtName + " wurde erfolgreich bearbeitet."), Toast.LENGTH_SHORT).show()
            }
        }




        val gericht: Bundle? = intent.extras
        var inputText = ""

        if(gericht != null){
            oldGerichtName.text = gericht.getString("GERICHT_NAME").toString()
            gerichtName = gericht.getString("GERICHT_NAME").toString()
            zutatenListe = gericht.getString("ZUTATEN_LISTE").toString()
            isVegetarisch = gericht.getBoolean("IS_VEGETARISCH")
        }

        inputFieldGericht.hint = gerichtName

        try{
            zutaten = zutatenListe.split(",") as ArrayList<String>
        } catch (e: Exception){
            zutaten = ArrayList()
            zutaten.add(zutatenListe)
        }





        adapter = ArrayAdapter(this, R.layout.custom_list_item_zutat, R.id.gerichtName, zutaten)

        listViewZutaten.adapter = adapter

        listViewZutaten.setOnItemClickListener{ adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->
            println("GerichtEditierenActivity -> pressed item $i")
            println(listViewZutaten.getItemAtPosition(i).toString())

            val zutat = Bundle()
            zutat.putString("Zutat", listViewZutaten.getItemAtPosition(i).toString())

            createChangeZutatDialog(zutat, zutaten, i)

        }

        inputFieldGericht.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideSoftKeyboard(v)
            }
        }


        btnDeleteGericht.setOnClickListener{
            val alert = AlertDialog.Builder(this)
            alert.setTitle(getString(R.string.deleteGericht))
            alert.setIcon(R.drawable.ic_delete)

            alert.setMessage(getString(R.string.deleteGerichtText))
                    .setPositiveButton(R.string.delete, DialogInterface.OnClickListener() { _: DialogInterface, _: Int ->
                        val tempDao = AppDatabase.getDatabase(applicationContext).gerichtDao()
                        val tempGericht = tempDao.findByName(gerichtName)
                        tempDao.delete(tempGericht)
                        Toast.makeText(this, "TODO() Gericht was successfully deleted", Toast.LENGTH_SHORT).show()
                        waitForToastShortThread.start()
                    })
                    .setNegativeButton(R.string.cancel, DialogInterface.OnClickListener() { dialog: DialogInterface, _: Int ->
                        dialog.cancel()
                    })
            alert.create()
            alert.show()

        }


        //TODO zutatenListe splitten und in die liste einfügen
        //TODO vegetarisch boolean setzen


    }

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

    private fun createChangeZutatDialog(zutat: Bundle, zutatenNew: ArrayList<String>, position: Int) {
        // Use the Builder class for convenient dialog construction
        var inputText = ""
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.textZutatBearbeiten) + " \"${zutat.get("Zutat")}\"")

        val input = EditText(this)
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton(R.string.aendern, DialogInterface.OnClickListener { dialog, id ->
            inputText = input.text.toString()
            val gericht = AppDatabase.getDatabase(applicationContext).gerichtDao().findByName(gerichtName)
            updateZutat(gericht, inputText, zutatenNew, position)

        })

        builder.setNegativeButton(R.string.abbrechen,
                DialogInterface.OnClickListener { dialog, id ->
                    dialog.cancel()
                })
        // Create the AlertDialog object and return it
        builder.create()
        builder.show()
    }

    //Method for hiding the keyboard after pressing on an empty space on screen
    private fun hideSoftKeyboard(view: View) {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun updateZutat(gericht: Gericht, inputText: String, zutaten: ArrayList<String>, position: Int){

            val tempZutat = zutaten[position]
            val stringBuilder = createNewZutatenString(zutaten, position, inputText)

            println(stringBuilder)

            newGericht = Gericht(gerichtName, stringBuilder, isVegetarisch)
            println(newGericht!!.zutaten)

            zutaten[position] = inputText
            saveEditedGericht(newGericht!!)
            adapter.notifyDataSetChanged()

            Toast.makeText(this, ("$tempZutat wurde erfolgreich zu $inputText bearbeitet."), Toast.LENGTH_SHORT).show()

            println("ChangeZutatFragment Input -> " + inputText)

    }

    private fun saveEditedGericht(newGericht: Gericht) = runBlocking {
        AppDatabase.getDatabase(applicationContext).gerichtDao().update(gericht = newGericht)
    }


    private fun createNewZutatenString(zutaten: List<String>, position: Int, inputText: String): String {
        val newZutaten = zutaten.toMutableList()

        newZutaten[position] = inputText
        val stringBuilder = StringBuilder()
        for (element: String in newZutaten) {
            stringBuilder.append("$element, ")
        }
        if (stringBuilder.endsWith(", ")) {
            stringBuilder.deleteCharAt(stringBuilder.length - 1)
            stringBuilder.deleteCharAt(stringBuilder.length - 1)
        }
        return stringBuilder.toString()
    }

}