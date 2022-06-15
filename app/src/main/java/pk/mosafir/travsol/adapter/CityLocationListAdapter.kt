package pk.mosafir.travsol.adapter

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pk.mosafir.travsol.R
import pk.mosafir.travsol.interfaces.CitySelector
import pk.mosafir.travsol.response.TourLocation
import java.util.*

class CityLocationListAdapter(
    private val subString:String,
    private val offers: List<TourLocation>,
    private val citySelector: CitySelector
) :
    RecyclerView.Adapter<CityLocationListAdapter.OfferViewHolder>() {
    var tempName = ""
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CityLocationListAdapter.OfferViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_city_list, parent, false)
        return OfferViewHolder(view)
    }

    override fun onBindViewHolder(holder: CityLocationListAdapter.OfferViewHolder, position: Int) {
        var destName = ""
        destName = offers[position].city_name!!
        if(offers[position].tour_title!!.isNotEmpty())
            destName = offers[position].tour_title!!
        val coreName = destName
        val string = SpannableString(coreName)
        val regex =
            string.toString().lowercase(Locale.getDefault()).indexOf(subString)
        val length = subString.length
        val end = regex + length
        if (regex != -1)
            string.setSpan(
                StyleSpan(Typeface.BOLD),
                regex,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        holder.cityName.text = string
        holder.itemView.setOnClickListener {
            citySelector.selected(destName, offers[position].city_id.toString())
        }


//        var tempName = offers[position].city_name
//        if(offers[position].tour_title!!.isNotEmpty()){
//            tempName = offers[position].tour_title
//        }
//        holder.cityName.text = tempName
//        holder.itemView.setOnClickListener {
//            citySelector.selected(tempName, offers[position].city_id.toString())
//        }
    }

    override fun getItemCount(): Int = offers.size

    inner class OfferViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cityName: TextView = itemView.findViewById(R.id.cityName)
    }
}