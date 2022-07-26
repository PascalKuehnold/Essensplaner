package de.pascalkuehnold.essensplaner.activities.ui.gerichtinformationen

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import de.pascalkuehnold.essensplaner.R
import de.pascalkuehnold.essensplaner.activities.GerichtActivity.Companion.chefkochUrl
import de.pascalkuehnold.essensplaner.activities.GerichtActivity.Companion.gerichtId
import de.pascalkuehnold.essensplaner.activities.GerichtActivity.Companion.gerichtVonChefkoch
import de.pascalkuehnold.essensplaner.activities.GerichtActivity.Companion.isVegeterian
import de.pascalkuehnold.essensplaner.database.AppDatabase
import de.pascalkuehnold.essensplaner.dataclasses.Zutat
import de.pascalkuehnold.essensplaner.handler.ExternalLinkHandler
import de.pascalkuehnold.essensplaner.layout.CustomZutatenAdapter

class GerichtInformationFragment : Fragment() {

    private lateinit var gerichtInformationViewModel: GerichtInformationViewModel
    private var moreInfosAreShown: Boolean = false


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
            tvCookTime.gravity = Gravity.CENTER
        })

        val ivIsVegetarian: ImageView = root.findViewById(R.id.imageViewVegetarian)
        ivIsVegetarian.isVisible = isVegeterian

        val lvZutaten: ListView = root.findViewById(R.id.zutatenAnzeige)

        val zutatenArray = AppDatabase.getDatabase(requireContext()).gerichtDao().loadByID(gerichtId)!!.zutatenList
        val zutatenAsString = ArrayList<String>()

        for(zutat: Zutat in zutatenArray){
            zutatenAsString.add(zutat.zutatenName)
        }

        val btnOriginalRecipe: TextView = root.findViewById(R.id.originalRecipeWebsite)
        btnOriginalRecipe.apply {
            setOnClickListener{
                ExternalLinkHandler(container!!.context, null).showWarningExternalLink(chefkochUrl, false)
            }
        }

        val moreInfoLayout: View = root.findViewById(R.id.layout_more_meal_information)



        val btnShowMoreMealInfo: AppCompatImageButton = root.findViewById(R.id.btn_showMoreMealInfos)
        btnShowMoreMealInfo.setOnClickListener {
            onShowMoreMealInfoButtonClicked(it, moreInfoLayout)
        }




        //creates the custom adapter
        val adapter = CustomZutatenAdapter(container!!.context, zutatenArray as ArrayList<Zutat>, null)

        //setting the adapter for the listview of ingredients
        lvZutaten.adapter = adapter


        (activity as AppCompatActivity?)!!.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity?)!!.supportActionBar?.title = "Gericht Informationen"


        return root
    }

    private fun onShowMoreMealInfoButtonClicked(btnShowMoreMealInfo: View, moreInfoLayout: View) {
        if(moreInfosAreShown){
            slideOut(btnShowMoreMealInfo, moreInfoLayout)
        } else {
            slideIn(btnShowMoreMealInfo, moreInfoLayout)
        }

        moreInfosAreShown = !moreInfosAreShown
    }

    private fun slideIn(btnShowMoreMealInfo: View, moreInfoLayout: View){
        btnShowMoreMealInfo.animate()
            .translationXBy(-moreInfoLayout.width.toFloat())
        moreInfoLayout.animate()
            .translationXBy(-moreInfoLayout.width.toFloat())
    }

    private fun slideOut(btnShowMoreMealInfo: View, moreInfoLayout: View){
        btnShowMoreMealInfo.animate()
            .translationXBy(moreInfoLayout.width.toFloat())
        moreInfoLayout.animate()
            .translationXBy(moreInfoLayout.width.toFloat())
    }


}