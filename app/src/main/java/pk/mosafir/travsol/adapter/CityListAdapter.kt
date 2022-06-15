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
import pk.mosafir.travsol.response.DiscoverPakistanCity
import java.util.*

class CityListAdapter(
    private val subString: String,
    private val offers: List<DiscoverPakistanCity>,
    private val citySelector: CitySelector
) :
    RecyclerView.Adapter<CityListAdapter.OfferViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CityListAdapter.OfferViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_city_list, parent, false)
        return OfferViewHolder(view)
    }

    override fun onBindViewHolder(holder: CityListAdapter.OfferViewHolder, position: Int) {
        val string = SpannableString(offers[position].destination_title)
        val regex =
            offers[position].destination_title!!.lowercase(Locale.getDefault()).indexOf(subString) //Regex(subString, RegexOption.IGNORE_CASE)
        val length = subString.length

        val end = regex + length
        if (regex != -1)
            string.setSpan(StyleSpan(Typeface.BOLD), regex, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        holder.cityName.text = string
        holder.itemView.setOnClickListener {
            citySelector.selected(
                offers[position].destination_title,
                offers[position].destination_id.toString()
            )
        }
    }

    override fun getItemCount(): Int = offers.size

    inner class OfferViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cityName: TextView = itemView.findViewById(R.id.cityName)
    }
}