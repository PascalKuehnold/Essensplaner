package de.pascalkuehnold.essensplaner.activities.ui.gerichtzubereitung

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import de.pascalkuehnold.essensplaner.R

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
            }

        })



        (activity as AppCompatActivity?)!!.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity?)!!.supportActionBar?.title = "Gericht Zubereitung"

        return root
    }
}