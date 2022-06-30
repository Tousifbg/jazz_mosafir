package pk.mosafir.travsol

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowManager
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pk.mosafir.travsol.adapter.AirportListAdapter
import pk.mosafir.travsol.interfaces.AirportSelector
import pk.mosafir.travsol.response.GeneralFlightResponse
import pk.mosafir.travsol.ui.book_flight.BookFlightFragment
import pk.mosafir.travsol.utils.invisible
import pk.mosafir.travsol.utils.toast
import pk.mosafir.travsol.viewmodel.FlightViewModel
import java.util.*

class CustomDialog(
    val title: String,
    context: Context,
    var airportSelector: AirportSelector,
    val viewModel: FlightViewModel,
    val viewLifecycleOwner: LifecycleOwner,
    themeResId: Int
) : Dialog(context, themeResId) {
    init {
        initialize()
    }
    private val lp = WindowManager.LayoutParams()
    lateinit var loader: ProgressBar
    lateinit var location: EditText
    private var airportListAdapter: AirportListAdapter =
        AirportListAdapter("", BookFlightFragment.mOffersList, airportSelector)
    private var airportListAdapterArrive =
        AirportListAdapter("", BookFlightFragment.mOffersListArrive, airportSelector)
    private val mOffersList2 = mutableListOf<GeneralFlightResponse>()
    fun initialize() {
//        lp.copyFrom(window?.attributes)
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT
//        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        setCancelable(false)
        setContentView(R.layout.cutom_dialog_location)
        loader = findViewById(R.id.loader)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        location = findViewById(R.id.location)
        val dialogTitle = findViewById<TextView>(R.id.title)
        dialogTitle.text = title
        viewModelObserver()
        if (title != "Arrival City") {
            viewModel.fetchOffers()
            recyclerView.apply {
                layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = airportListAdapter
            }
        } else {
            viewModel.fetchOffersArrival()
            recyclerView.apply {
                layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = airportListAdapterArrive
            }
        }
        location.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.isEmpty()) {
                    if (title == "Arrival City") {
                        airportListAdapterArrive =
                            AirportListAdapter(
                                "",
                                BookFlightFragment.mOffersListArrive,
                                airportSelector
                            )
                        recyclerView.adapter = airportListAdapterArrive
                    } else {
                        airportListAdapter =
                            AirportListAdapter(
                                "",
                                BookFlightFragment.mOffersList,
                                airportSelector
                            )
                        recyclerView.adapter = airportListAdapter
                    }
                } else {

                    if (title == "Arrival City") {
                        mOffersList2.clear()
                        airportListAdapterArrive.notifyDataSetChanged()
                        airportListAdapterArrive = AirportListAdapter(
                            s.toString().lowercase(Locale.getDefault()),
                            mOffersList2,
                            airportSelector
                        )
                        recyclerView.adapter = airportListAdapterArrive
                    } else {
                        mOffersList2.clear()
                        airportListAdapter.notifyDataSetChanged()
                        airportListAdapter = AirportListAdapter(
                            s.toString().lowercase(Locale.getDefault()),
                            mOffersList2,
                            airportSelector
                        )
                        recyclerView.adapter = airportListAdapter
                    }
                    viewModel.getSelectedAirport("%${s}%")
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        val cancel = findViewById<TextView>(R.id.cancel)
        cancel.setOnClickListener {
            dismiss()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun viewModelObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner, {
            if (!it)
                loader.invisible()
        })
        viewModel.error.observe(viewLifecycleOwner, {
            context.toast(it)
        })
        viewModel.searchAirportList.observe(viewLifecycleOwner, {
            with(mOffersList2) {
                clear()
                it?.let { it1 -> addAll(it1) }
                try {
                    if(title=="Arrival City")
                        airportListAdapterArrive.notifyDataSetChanged()
                    else
                        airportListAdapter.notifyDataSetChanged()
                } catch (e: Exception) {
                }
            }
        })
        viewModel.airportList.observe(viewLifecycleOwner, {
            with(BookFlightFragment.mOffersList) {
                clear()
                it?.let { it1 -> addAll(it1) }
                try {
                    airportListAdapter.notifyDataSetChanged()
                } catch (e: Exception) {
                }
            }
        })
        viewModel.airportListArrival.observe(viewLifecycleOwner, {
            with(BookFlightFragment.mOffersListArrive) {
                clear()
                it?.let {
                        it1 -> addAll(it1)
                }
                try {
                    airportListAdapterArrive.notifyDataSetChanged()
                } catch (e: Exception) {
                }
            }
        })
    }
}