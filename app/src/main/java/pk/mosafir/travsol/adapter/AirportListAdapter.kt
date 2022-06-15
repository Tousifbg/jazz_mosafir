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
import pk.mosafir.travsol.interfaces.AirportSelector
import pk.mosafir.travsol.response.GeneralFlightResponse
import java.util.*

class AirportListAdapter(
    private val search: String?,
    private val offers: List<GeneralFlightResponse>,
    private val airportSelector: AirportSelector
) :

    RecyclerView.Adapter<AirportListAdapter.OfferViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AirportListAdapter.OfferViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_flight_airport_selection, parent, false)
        return OfferViewHolder(view)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder:AirportListAdapter.OfferViewHolder, position: Int) {
        val oneWayModel = offers[position]
        val code = oneWayModel.fly_from!!.split(',')
        val string = SpannableString(code[1])
        val regex =
            code[1].lowercase(Locale.getDefault()).indexOf(search!!)
        val length = search.length

        val end = regex + length
        if (regex != -1)
            string.setSpan(StyleSpan(Typeface.BOLD), regex, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        holder.airportName.text = " ,$string"
        holder.airportCode.text = code[0]
        holder.itemView.setOnClickListener {
            airportSelector.selected(oneWayModel)//selected(oneWayModel)
        }
    }

    override fun getItemCount(): Int = offers.size

    inner class OfferViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val airportName: TextView = itemView.findViewById(R.id.airport)
        val airportCode: TextView = itemView.findViewById(R.id.airportCode)
    }
}