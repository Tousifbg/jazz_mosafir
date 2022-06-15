package pk.mosafir.travsol.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pk.mosafir.travsol.R
import pk.mosafir.travsol.model.CancelBookingModal

class CancelBookingAdapter(
    private val offers: List<CancelBookingModal>,
) :
    RecyclerView.Adapter<CancelBookingAdapter.OfferViewHolder>() {
    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CancelBookingAdapter.OfferViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cancel_booking_item, parent, false)
        this.context = parent.context
        return OfferViewHolder(view)
    }


    override fun onBindViewHolder(holder:CancelBookingAdapter.OfferViewHolder, position: Int) {
        val offer = offers[position]
        holder.title.text = offer.bookingId
        holder.city.text = offer.bookingType
    }

    override fun getItemCount(): Int = offers.size

    inner class OfferViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.booking_id)!!
        val city = itemView.findViewById<TextView>(R.id.cancel_type)!!
    }
}