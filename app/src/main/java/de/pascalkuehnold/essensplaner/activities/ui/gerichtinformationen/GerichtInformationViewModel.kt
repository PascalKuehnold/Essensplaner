package de.pascalkuehnold.essensplaner.activities.ui.gerichtinformationen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.pascalkuehnold.essensplaner.activities.GerichtActivity.Companion.gerichtName

class GerichtInformationViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = gerichtName
    }
    val text: LiveData<String> = _text
}