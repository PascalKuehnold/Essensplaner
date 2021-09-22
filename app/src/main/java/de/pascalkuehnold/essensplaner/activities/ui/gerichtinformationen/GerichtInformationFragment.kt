package de.pascalkuehnold.essensplaner.activities.ui.gerichtinformationen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import de.pascalkuehnold.essensplaner.R
import de.pascalkuehnold.essensplaner.activities.GerichtActivity.Companion.gerichtVonChefkoch
import de.pascalkuehnold.essensplaner.activities.GerichtActivity.Companion.gerichtZutaten
import de.pascalkuehnold.essensplaner.dataclasses.Zutat
import de.pascalkuehnold.essensplaner.layout.CustomZutatenAdapter

class GerichtInformationFragment : Fragment() {

    private lateinit var gerichtInformationViewModel: GerichtInformationViewModel


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        gerichtInformationViewModel =
            ViewModelProvider(this).get(GerichtInformationViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_meal_information, container, false)

        val tvMealName: TextView = root.findViewById(R.id.mealName)
        gerichtInformationViewModel.mealName.observe(viewLifecycleOwner, {
            tvMealName.text = it
        })

        val chefkochLogo: ImageView = root.findViewById(R.id.chefkochLogo)
        chefkochLogo.isVisible = gerichtVonChefkoch

        val tvMealAuthor: TextView = root.findViewById(R.id.mealAuthor)
        gerichtInformationViewModel.mealAuthor.observe(viewLifecycleOwner, {
            tvMealAuthor.text = it
        })

        val tvCookTime: TextView = root.findViewById(R.id.tvMealCookTime)
        gerichtInformationViewModel.mealCookTime.observe(viewLifecycleOwner, {
            tvCookTime.text = it
        })

        val lvZutaten: ListView = root.findViewById(R.id.zutatenAnzeige)
        val zutaten = Zutat.generateIngredientsList(gerichtZutaten)


        //creates the custom adapter
        val adapter = CustomZutatenAdapter(container!!.context, zutaten, null)

        //setting the adapter for the listview of ingredients
        lvZutaten.adapter = adapter


        (activity as AppCompatActivity?)!!.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity?)!!.supportActionBar?.title = "Gericht Informationen"
        return root
    }



}