package de.pascalkuehnold.essensplaner.activities.ui.gerichtzubereitung

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import de.pascalkuehnold.essensplaner.R
import de.pascalkuehnold.essensplaner.activities.GerichtActivity
import de.pascalkuehnold.essensplaner.activities.GerichtActivity.Companion.chefkochUrl
import de.pascalkuehnold.essensplaner.activities.GerichtActivity.Companion.gerichtId
import de.pascalkuehnold.essensplaner.activities.GerichtActivity.Companion.zubereitungsText
import de.pascalkuehnold.essensplaner.activities.GerichtEditierenActivity
import de.pascalkuehnold.essensplaner.database.AppDatabase
import de.pascalkuehnold.essensplaner.interfaces.GerichtDao
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.runBlocking

class GerichtZubereitungFragment : Fragment() {

    private lateinit var gerichtZubereitungViewModel: GerichtZubereitungViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        gerichtZubereitungViewModel = ViewModelProvider(this).get(GerichtZubereitungViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_meal_recipe, container, false)
        val editText: EditText = root.findViewById(R.id.editText_meal_recipe)
        editText.isFocusableInTouchMode = false
        editText.clearFocus()
        gerichtZubereitungViewModel.text.observe(viewLifecycleOwner, {
            if(it.isNotEmpty()){
                editText.setText(it)
                editText.hint = ""
            } else {
                editText.setText("Fenster öffnen und Zubereitungstext eingeben")
            }

        })

        val lockRecipe: ImageView = root.findViewById(R.id.imageView_LockRecipeText)
        lockRecipe.setOnClickListener(){
            if(chefkochUrl.isEmpty()){
                if(editText.isFocusableInTouchMode){
                    lockRecipe.setImageResource(R.drawable.ic_lock_closed_white)
                    editText.isFocusableInTouchMode = false
                    editText.clearFocus()
                } else {
                    lockRecipe.setImageResource(R.drawable.ic_lock_opened_white)
                    editText.isFocusableInTouchMode = true
                    editText.clearFocus()
                }
                zubereitungsText = editText.text.toString()
                changeMeal(root.context, zubereitungsText)
            } else {
                Toast.makeText(root.context, "Chefkoch Rezepte können nicht geändert werden.", Toast.LENGTH_SHORT).show()
            }
        }


        (activity as AppCompatActivity?)!!.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity?)!!.supportActionBar?.title = "Gericht Zubereitung"

        return root
    }

    private fun changeMeal(mContext: Context, mZubereitungsText: String) = runBlocking{
        val tempGericht = AppDatabase.getDatabase(mContext).gerichtDao().loadByID(gerichtId)

        if (tempGericht != null) {
            tempGericht.gerichtRezept = mZubereitungsText
            AppDatabase.getDatabase(mContext).gerichtDao().update(tempGericht)
        }
    }
}