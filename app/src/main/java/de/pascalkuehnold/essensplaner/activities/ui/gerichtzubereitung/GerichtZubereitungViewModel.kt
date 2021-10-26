package de.pascalkuehnold.essensplaner.activities.ui.gerichtzubereitung

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.pascalkuehnold.essensplaner.activities.GerichtActivity.Companion.zubereitungsText

class GerichtZubereitungViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = zubereitungsText
    }
    val text: LiveData<String> = _text

}