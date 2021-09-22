package de.pascalkuehnold.essensplaner.activities

import android.app.Activity
import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import de.pascalkuehnold.essensplaner.R

class SearchableActivity: Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search)

        /*
        // Verify the action and get the query
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                //doMySearch(query)
            }
        }
        */

    }

    /*
    private fun doMySearch(query: String) {

    }
    */
}