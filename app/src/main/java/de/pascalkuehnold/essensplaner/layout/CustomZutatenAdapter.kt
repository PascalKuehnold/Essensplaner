package de.pascalkuehnold.essensplaner.layout

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import de.pascalkuehnold.essensplaner.R
import de.pascalkuehnold.essensplaner.activities.GerichtEditierenActivity
import de.pascalkuehnold.essensplaner.dataclasses.Zutat
import java.util.*
import kotlin.collections.ArrayList

class CustomZutatenAdapter(context: Context, zutaten: ArrayList<String>, callback: View.OnClickListener): BaseAdapter(), ListAdapter {
    private val mZutaten = zutaten
    private val mContext = context
    private val mCallback = callback
    private var isChecked = false

    override fun getCount(): Int {
        return mZutaten.size
    }

    override fun getItem(position: Int): Any {
        return mZutaten[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    @SuppressLint("InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var view = convertView
        if(view == null){
            val inflater: LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.custom_list_item_zutat, null)
        }

        val row = view?.findViewById<LinearLayout>(R.id.rowClick)
        row?.setOnClickListener(mCallback)
        row?.tag = position

        val zutatenName = view?.findViewById<TextView>(R.id.zutatenName)
        if (zutatenName != null) {
            zutatenName.text = mZutaten[position].removePrefix("`").capitalize(Locale.getDefault())
        }

        val imageViewZutatChecked = view?.findViewById<ImageView>(R.id.imageViewCheckedZutat)
        if (imageViewZutatChecked != null) {
                imageViewZutatChecked.visibility = View.INVISIBLE
        }


        val btnZutatBearbeiten = view?.findViewById<Button>(R.id.btnZutatBearbeiten)
        btnZutatBearbeiten?.setOnClickListener {
            if(mContext is GerichtEditierenActivity){
                Zutat.createChangeZutatDialog(mContext, mZutaten, position, this)
            }

        }

        val btnDeleteZutat = view?.findViewById<Button>(R.id.btnDeleteZutat)
        btnDeleteZutat?.setOnClickListener{
            if(mContext is GerichtEditierenActivity){
                Zutat.deleteZutat(mContext, mZutaten, position, this)
            }
        }

        return view
    }


}