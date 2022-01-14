package de.pascalkuehnold.essensplaner.activities

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.MenuItem
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import de.pascalkuehnold.essensplaner.R
import de.pascalkuehnold.essensplaner.database.AppDatabase

/**
 * Created by Kalle on 1/14/2022.
 */
class WebViewActivity: AppCompatActivity() {
    private lateinit var mRecipeString: String
    private var currentUrl: String? = ""
    private var mGerichtPos: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_webview)

        supportActionBar?.setTitle(R.string.hinzuf_gen)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#266799")))

        mRecipeString = intent.getStringExtra("recipeString")!!
        mGerichtPos = intent.getLongExtra("mGerichtPos", -1)


        val addMealToUrlListBtn = findViewById<LinearLayout>(R.id.layout_btn_custom_tab_add_meal)
        val webView: WebView = findViewById(R.id.webView)

        webView.webViewClient = WebViewClient()

        webView.loadUrl(mRecipeString)

        addMealToUrlListBtn.setOnClickListener{
            currentUrl = webView.url
            addMealByUrl(currentUrl, mGerichtPos)
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addMealByUrl(recipeUrlString: String?, mGerichtPos: Long = -1){
        val regex = Regex("https:\\/\\/www.chefkoch.de\\/rezepte\\/\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)")

        val uri: Uri = recipeUrlString!!.toUri()
        val gerichtPosition: Long = mGerichtPos

        val url = uri.toString()

        if(gerichtPosition > 0){
            val gerichtDao = AppDatabase.getDatabase(this).gerichtDao()
            val gericht = gerichtDao.loadByID(gerichtPosition)

            if (gericht != null) {
                gerichtDao.delete(gericht)
            }
        }

        if(url.matches(regex)) {
            if(GerichteListeActivity.urlList.contains(url)){
                Toast.makeText(this, uri.toString() + " is already in the list", Toast.LENGTH_SHORT).show()
            } else {
                GerichteListeActivity.alert!!.dismiss()
                Log.d("Broadcast URL", uri.toString())
                Toast.makeText(this, uri.toString(), Toast.LENGTH_SHORT).show()

                GerichteListeActivity.urlList.add(uri.toString())
                Log.d("URL", uri.toString())
            }

        } else {
            Toast.makeText(this, R.string.chefkochMealAddError, Toast.LENGTH_SHORT).show()
        }

    }

}