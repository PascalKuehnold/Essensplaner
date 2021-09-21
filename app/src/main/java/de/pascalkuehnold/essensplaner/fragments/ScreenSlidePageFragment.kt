package de.pascalkuehnold.essensplaner.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import de.pascalkuehnold.essensplaner.R


class ScreenSlidePageFragment : Fragment() {
    lateinit var inputField: EditText

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_screen_slide_page, container, false)

        inputField = root.findViewById<EditText>(R.id.inputField)


        return root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("MEAL_NAME", inputField.text.toString())
        println(inputField.text.toString())
    }



}