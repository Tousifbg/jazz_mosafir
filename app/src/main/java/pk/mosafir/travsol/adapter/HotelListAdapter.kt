package pk.mosafir.travsol.adapter

import android.annotation.SuppressLint
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
import pk.mosafir.travsol.response.HotelLocation
import java.util.*

class HotelListAdapter(
    private val subString: String,
    private val offers: List<HotelLocation>,
    private val citySelector: CitySelector
) :
    RecyclerView.Adapter<HotelListAdapter.OfferViewHolder>() {
    var tempName = ""
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HotelListAdapter.OfferViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_city_list, parent, false)
        return OfferViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HotelListAdapter.OfferViewHolder, position: Int) {
        var destName = ""
        destName = offers[position].city_name!!
        if (offers[position].hotel_name!!.isNotEmpty()) {
             if ((position == 0|| offers[position].city_name!! != tempName) &&offers[position].hotel_name!!.contains(offers[position].city_name!!) ) {
                tempName = offers[position].city_name!!
                 destName =  offers[position].city_name!!
            } else {
                 destName = offers[position].hotel_name!!
            }
        }
        var coreName = destName
        if (!destName.contains("Pakistan")) {
            coreName = "$destName , Pakistan"
        }
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

    }

    override fun getItemCount(): Int = offers.size

    inner class OfferViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cityName: TextView = itemView.findViewById(R.id.cityName)
    }
}