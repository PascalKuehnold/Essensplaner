package de.pascalkuehnold.essensplaner.activities.ui.gerichtinformationen

import android.content.Intent
import android.net.Uri
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
import de.pascalkuehnold.essensplaner.activities.GerichtActivity.Companion.chefkochUrl
import de.pascalkuehnold.essensplaner.activities.GerichtActivity.Companion.gerichtId
import de.pascalkuehnold.essensplaner.activities.GerichtActivity.Companion.gerichtVonChefkoch
import de.pascalkuehnold.essensplaner.activities.GerichtActivity.Companion.gerichtZutaten
import de.pascalkuehnold.essensplaner.activities.GerichteListeActivity
import de.pascalkuehnold.essensplaner.database.AppDatabase
import de.pascalkuehnold.essensplaner.dataclasses.ChefkochMeal
import de.pascalkuehnold.essensplaner.dataclasses.Gericht
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

        val zutatenArray = AppDatabase.getDatabase(requireContext()).gerichtDao().loadByID(gerichtId)!!.zutatenList
        val zutatenAsString = ArrayList<String>()

        for(zutat: Zutat in zutatenArray){
            zutatenAsString.add(zutat.zutatenName)
        }

        val btnOriginalRecipe: TextView = root.findViewById(R.id.originalRecipeWebsite)
        btnOriginalRecipe.apply {
            setOnClickListener{
                println(chefkochUrl)
                val openURL = Intent(Intent.ACTION_VIEW)
                openURL.data = Uri.parse(chefkochUrl)
                startActivity(openURL)
            }
        }


        //creates the custom adapter
        val adapter = CustomZutatenAdapter(container!!.context, zutatenAsString, null)

        //setting the adapter for the listview of ingredients
        lvZutaten.adapter = adapter


        (activity as AppCompatActivity?)!!.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity?)!!.supportActionBar?.title = "Gericht Informationen"


        return root
    }



}