package de.pascalkuehnold.essensplaner.activities.ui.gerichtinformationen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import de.pascalkuehnold.essensplaner.R

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
        val textView: TextView = root.findViewById(R.id.mealName)
        gerichtInformationViewModel.text.observe(viewLifecycleOwner, {
            textView.text = it
        })
        (activity as AppCompatActivity?)!!.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity?)!!.supportActionBar?.title = "Gericht Informationen"
        return root
    }

}