package de.pascalkuehnold.essensplaner.activities.ui.gerichtinformationen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.pascalkuehnold.essensplaner.activities.GerichtActivity.Companion.gerichtAuthor
import de.pascalkuehnold.essensplaner.activities.GerichtActivity.Companion.gerichtName
import de.pascalkuehnold.essensplaner.activities.GerichtActivity.Companion.gerichtVonChefkoch
import de.pascalkuehnold.essensplaner.activities.GerichtActivity.Companion.gerichtZubereitungsZeit

class GerichtInformationViewModel : ViewModel() {

    private val _mealName = MutableLiveData<String>().apply {
        value = gerichtName
    }
    val mealName: LiveData<String> = _mealName

    private val _mealAuthor = MutableLiveData<String>().apply {
        value = gerichtAuthor
    }
    val mealAuthor: LiveData<String> = _mealAuthor

    private val _mealCookTime = MutableLiveData<String>().apply {
        value = gerichtZubereitungsZeit
    }
    val mealCookTime: LiveData<String> = _mealCookTime
}