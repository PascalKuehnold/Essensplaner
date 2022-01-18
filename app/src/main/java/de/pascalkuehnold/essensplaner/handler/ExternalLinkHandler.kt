package de.pascalkuehnold.essensplaner.handler


import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.EditText
import android.widget.RemoteViews
import androidx.appcompat.app.AlertDialog
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import de.pascalkuehnold.essensplaner.R
import de.pascalkuehnold.essensplaner.activities.GerichteListeActivity
import de.pascalkuehnold.essensplaner.activities.WebViewActivity


class ExternalLinkHandler(_mContext: Context, _mGerichtPos: Long?) {
    private val mContext = _mContext
    private val mGerichtPos = _mGerichtPos


    fun showWarningExternalLink(url: String, input: Boolean) {
        var inputText: EditText? = null

        val alertDialogBuilder = AlertDialog.Builder(
            mContext,
            R.style.Theme_Essensplaner_DialogTheme
        )
        if(input){
            inputText = EditText(mContext)
            inputText.hint = mContext.getString(R.string.enterKeyword)
            inputText.setHintTextColor(ContextCompat.getColor(mContext, R.color.lightGreyAlpha75))
            inputText.textAlignment = View.TEXT_ALIGNMENT_CENTER
            inputText.contentDescription = mContext.getString(R.string.inputFieldKeyword)
        }
        alertDialogBuilder.setPositiveButton("Zu Chefkoch.de") { _, _ ->
            val recipeString: String = if (inputText != null) {
                if(inputText.text.isNotEmpty()){
                    url + inputText.text + "/Rezepte.html"
                } else {
                    "https://www.chefkoch.de/rezepte/"
                }
            } else {
                url
            }

            val builder = CustomTabsIntent.Builder()

            val remoteViews = RemoteViews(
                mContext.packageName,
                R.layout.customtab_add_meal_bottom_layout
            )
            remoteViews.setImageViewResource(
                R.id.btn_custom_tab_add_meal,
                R.drawable.ic_add_to_shoppinglist
            )
            val clickableIDs = intArrayOf(R.id.layout_btn_custom_tab_add_meal)

            if(mContext is GerichteListeActivity){
                val sendLinkIntent = Intent(
                    mContext,
                    GerichteListeActivity.ActionBroadcastReceiver::class.java
                )
                sendLinkIntent.putExtra(Intent.EXTRA_SUBJECT, "This is the link you were exploring")
                sendLinkIntent.putExtra("gerichtPos", mGerichtPos)
                val pendingIntent = PendingIntent.getBroadcast(
                    mContext,
                    0,
                    sendLinkIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

                // Set the action button
                builder.setSecondaryToolbarViews(remoteViews, clickableIDs, pendingIntent)
                builder.addMenuItem(mContext.getString(R.string.mealAdd), pendingIntent)
            }

            val colorParams = CustomTabColorSchemeParams.Builder()
                .setNavigationBarColor(ContextCompat.getColor(mContext, R.color.newBackgroundColor))
                .setToolbarColor(ContextCompat.getColor(mContext, R.color.newBackgroundColor))
                .build()


            builder.setDefaultColorSchemeParams(colorParams)
            builder.setShowTitle(true)
            builder.setUrlBarHidingEnabled(false)


            val customTabsIntent: CustomTabsIntent = builder.build()
            customTabsIntent.intent.setPackage("com.android.chrome")

            try {
                customTabsIntent.launchUrl(mContext, Uri.parse(recipeString))
            } catch (e: Exception){
                e.stackTraceToString()
                //Launch custom webview
                val intent = Intent(mContext, WebViewActivity::class.java)
                intent.putExtra("recipeString", recipeString)
                intent.putExtra("gerichtPos", mGerichtPos)

                mContext.startActivity(intent)
            }


        }
        alertDialogBuilder.setNegativeButton("Lieber nicht") { dialog, _ ->
            dialog.dismiss()
        }

        val message = if(!input){ mContext.getString(R.string.externalLinkInfo)} else { mContext.getString(
            R.string.externalLinkInfo
        ) + mContext.getString(
            R.string.desiredMeal
        )}

        val alert = alertDialogBuilder.create()


        alert.setTitle(mContext.getString(R.string.externalLinkTitle))
        alert.setMessage(message)
        alert.setIcon(R.drawable.ic_info)
        alert.setView(inputText)
        alert.show()
    }





}