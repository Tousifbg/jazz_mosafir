package pk.mosafir.travsol.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import pk.mosafir.travsol.R
import pk.mosafir.travsol.response.Offer
import pk.mosafir.travsol.webview.WebViewActivity

class OfferAdapter(
    private val offers: List<Offer>,
) :
    RecyclerView.Adapter<OfferAdapter.OfferViewHolder>() {
    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferAdapter.OfferViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.offer_card_item, parent, false)
        this.context = parent.context
        return OfferViewHolder(view)
    }
    override fun onBindViewHolder(holder:OfferAdapter.OfferViewHolder, position: Int) {
        val offer = offers[position]
        holder.title.text = offer.title
        holder.starting.load(offer.starting)
        holder.city.text = offer.city
        holder.viewLocation.load(offer.viewLocation)

        holder.sCurrency.text = offer.s_currency
        holder.sPrice.text = offer.s_price
        holder.fCurrency.text = offer.f_currency
        holder.fPrice.text = offer.f_price

        holder.itemView.setOnClickListener {
            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra("url", "https://mosafir.pk/Tours/details/680")
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = offers.size

    inner class OfferViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val city: TextView = itemView.findViewById(R.id.city)
        val starting: ImageView = itemView.findViewById(R.id.starting)
        val viewLocation: ImageView = itemView.findViewById(R.id.location_view)
        val sCurrency: TextView = itemView.findViewById(R.id.s_currency)
        val sPrice: TextView = itemView.findViewById(R.id.s_price)
        val fCurrency: TextView = itemView.findViewById(R.id.f_currency)
        val fPrice: TextView = itemView.findViewById(R.id.f_price)
    }
}