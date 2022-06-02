package de.pascalkuehnold.essensplaner.handler

import android.content.Context
import android.content.DialogInterface
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import de.pascalkuehnold.essensplaner.R
import de.pascalkuehnold.essensplaner.database.AppDatabase
import de.pascalkuehnold.essensplaner.database.EinkaufslisteDatabase
import de.pascalkuehnold.essensplaner.database.WochenplanerDatabase
import de.pascalkuehnold.essensplaner.database.WochenplanerVeggieDatabase

/**
 * Created by Kalle on 6/2/2022.
 */
class DatabaseHandler(context: Context, applicationContext: Context) {
    val mContext = context
    val applicationContext = applicationContext


    fun deleteAllDatabases(){
       createDeleteAllDatabasesAlert()
    }

    private fun createDeleteAllDatabasesAlert(){
        val alert = AlertDialog.Builder(mContext)
        alert.setTitle(R.string.deleteDataFromPhone)
        alert.setMessage(R.string.deleteDataFromPhoneMessage)
        alert.setPositiveButton(R.string.delete) { _: DialogInterface, _: Int ->
            AppDatabase.getDatabase(applicationContext).gerichtDao().delete()
            WochenplanerDatabase.getDatabase(applicationContext).wochenGerichteDao().delete()
            WochenplanerVeggieDatabase.getDatabase(applicationContext).wochenGerichteVeggieDao().delete()
            EinkaufslisteDatabase.getDatabase(applicationContext).einkaufslisteDao().delete()
            Toast.makeText(mContext, R.string.allDataDeletedText, Toast.LENGTH_LONG).show()
        }
        alert.setNegativeButton(R.string.cancel) { dialog: DialogInterface, _: Int ->
            dialog.cancel()
        }
        alert.show()
    }

}