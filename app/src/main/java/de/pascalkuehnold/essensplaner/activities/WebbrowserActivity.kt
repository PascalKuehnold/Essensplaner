package de.pascalkuehnold.essensplaner.activities

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import de.pascalkuehnold.essensplaner.R
import de.pascalkuehnold.essensplaner.manager.MyWebViewClient
import de.pascalkuehnold.essensplaner.manager.NetworkState


class WebbrowserActivity : AppCompatActivity() {
    var webView: WebView? = null
    var editText: EditText? = null
    var progressBar: ProgressBar? = null
    private var back: ImageButton? = null
    private var forward:ImageButton? = null
    private var stop: ImageButton? = null
    private var refresh:ImageButton? = null
    private var homeButton:ImageButton? = null
    private var goButton: Button? = null

    private var recipeString: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webbrowser)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Mit Chefkoch.de suchen"
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#266799")))

        recipeString = intent.getStringExtra("recipeString").toString()

        editText = findViewById<View>(R.id.web_address_edit_text) as EditText
        back = findViewById<View>(R.id.back_arrow) as ImageButton
        forward = findViewById<View>(R.id.forward_arrow) as ImageButton
        stop = findViewById<View>(R.id.stop) as ImageButton
        goButton = findViewById<View>(R.id.go_button) as Button
        refresh = findViewById<View>(R.id.refresh) as ImageButton
        homeButton = findViewById<View>(R.id.home) as ImageButton
        progressBar = findViewById<View>(R.id.progress_bar) as ProgressBar
        progressBar!!.max = 100
        progressBar!!.visibility = View.VISIBLE
        webView = findViewById<View>(R.id.web_view) as WebView
        if (savedInstanceState != null) {
            webView!!.restoreState(savedInstanceState)
        } else {
            webView!!.settings.javaScriptEnabled = true
            webView!!.settings.useWideViewPort = true
            webView!!.settings.loadWithOverviewMode = true
            webView!!.settings.setSupportZoom(true)
            webView!!.settings.setSupportMultipleWindows(true)
            webView!!.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
            webView!!.setBackgroundColor(resources.getColor(R.color.white))
            webView!!.webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    progressBar!!.progress = newProgress
                    if (newProgress < 100 && progressBar!!.visibility == ProgressBar.GONE) {
                        progressBar!!.visibility = ProgressBar.VISIBLE
                    }
                    if (newProgress == 100) {
                        progressBar!!.visibility = ProgressBar.GONE
                    } else {
                        progressBar!!.visibility = ProgressBar.VISIBLE
                    }
                }
            }
        }
        webView!!.webViewClient = MyWebViewClient()

        webView!!.loadUrl(recipeString)

        goButton!!.setOnClickListener {
            try {
                if (!NetworkState.connectionAvailable(this@WebbrowserActivity)) {
                    Toast.makeText(
                        this@WebbrowserActivity,
                        R.string.check_connection,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val inputMethodManager: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(editText!!.windowToken, 0)
                    webView!!.loadUrl("https://www.chefkoch.de/rs/s0/" + editText!!.text.toString() + "/Rezepte.html")
                    editText!!.setText("")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        back!!.setOnClickListener {
            if (webView!!.canGoBack()) {
                webView!!.goBack()
            }
        }
        forward!!.setOnClickListener{
            if (webView!!.canGoForward()) {
                webView!!.goForward()
            }
        }
        stop!!.setOnClickListener { webView!!.stopLoading() }

        refresh!!.setOnClickListener { webView!!.reload() }

        homeButton!!.setOnClickListener { webView!!.loadUrl("https://www.chefkoch.de/rezepte/") }
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

    companion object{

    }
}