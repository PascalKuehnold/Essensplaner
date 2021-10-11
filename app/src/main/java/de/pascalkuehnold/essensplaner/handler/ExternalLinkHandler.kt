package de.pascalkuehnold.essensplaner.handler

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.View
import android.widget.EditText
import android.widget.RemoteViews
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.graphics.drawable.toBitmap
import de.pascalkuehnold.essensplaner.R
import de.pascalkuehnold.essensplaner.activities.GerichteListeActivity

class ExternalLinkHandler(_mContext: Context) {
    private val mContext = _mContext

    @SuppressLint("UseCompatLoadingForDrawables")
    fun showWarningExternalLink(url: String, input: Boolean) {
        var inputText: EditText? = null

        val alertDialogBuilder = AlertDialog.Builder(mContext, R.style.Theme_Essensplaner_DialogTheme)
        if(input){
            inputText = EditText(mContext)
            inputText.hint = "Gericht eingeben"
            inputText.setHintTextColor(mContext.resources.getColor(R.color.lightGreyAlpha75))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                inputText.textAlignment = View.TEXT_ALIGNMENT_CENTER
            }

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

            val remoteViews = RemoteViews(mContext.packageName, R.layout.customtab_add_meal_bottom_layout)
            remoteViews.setImageViewResource(R.id.btn_custom_tab_add_meal, R.drawable.ic_add_to_shoppinglist)
            val clickableIDs = intArrayOf(R.id.btn_custom_tab_add_meal)

            if(mContext is GerichteListeActivity){
                val sendLinkIntent = Intent(mContext, GerichteListeActivity.ActionBroadcastReceiver::class.java)
                sendLinkIntent.putExtra(Intent.EXTRA_SUBJECT, "This is the link you were exploring")
                val pendingIntent = PendingIntent.getBroadcast(mContext, 0, sendLinkIntent, PendingIntent.FLAG_UPDATE_CURRENT)

                builder.setToolbarColor(mContext.resources.getColor(R.color.newBackgroundColor))
                // Set the action button
                builder.setSecondaryToolbarViews(remoteViews, clickableIDs , pendingIntent)
                builder.setShowTitle(true)
                builder.addMenuItem(mContext.getString(R.string.mealAdd), pendingIntent)
                builder.setUrlBarHidingEnabled(false)
                val customTabsIntent: CustomTabsIntent = builder.build()
                customTabsIntent.launchUrl(mContext, Uri.parse(recipeString))
            }
        }
        alertDialogBuilder.setNegativeButton("Lieber nicht") { dialog, _ ->
            dialog.dismiss()
        }

        val message = if(!input){ mContext.getString(R.string.externalLinkInfo)} else { mContext.getString(R.string.externalLinkInfo) + mContext.getString(
            R.string.desiredMeal)}

        val alert = alertDialogBuilder.create()
        alert.setTitle(mContext.getString(R.string.externalLinkTitle))
        alert.setMessage(message)
        alert.setIcon(R.drawable.ic_info)
        alert.setView(inputText)
        alert.show()
    }
}