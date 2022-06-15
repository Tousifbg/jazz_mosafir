package pk.mosafir.travsol.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.savvi.rangedatepicker.CalendarPickerView
import pk.mosafir.travsol.R
import pk.mosafir.travsol.interfaces.AirportSelector
import pk.mosafir.travsol.response.GeneralFlightResponse
import pk.mosafir.travsol.ui.book_flight.BookFlightFragment
import pk.mosafir.travsol.ui.book_flight.MultiCityFragment.Companion.airportArrived
import pk.mosafir.travsol.ui.book_flight.MultiCityFragment.Companion.airportArrivedCode
import pk.mosafir.travsol.ui.book_flight.MultiCityFragment.Companion.airportDepart
import pk.mosafir.travsol.ui.book_flight.MultiCityFragment.Companion.airportDepartCode
import pk.mosafir.travsol.ui.book_flight.MultiCityFragment.Companion.dateTravel
import pk.mosafir.travsol.ui.book_flight.MultiCityFragment.Companion.dateTravelForApi
import pk.mosafir.travsol.ui.book_flight.MultiCityFragment.Companion.destinationCount
import pk.mosafir.travsol.viewmodel.FlightViewModel
import java.text.SimpleDateFormat
import java.util.*

class MultiCityAdapter(
    private val viewModel: FlightViewModel,
    private val offers: List<Int>,
    private val viewLifecycleOwner: LifecycleOwner,
    private val offersList: MutableList<GeneralFlightResponse>
) : RecyclerView.Adapter<MultiCityAdapter.OfferViewHolder>(), AirportSelector {
    private lateinit var datePickerDialog: Dialog
    lateinit var airportListAdapter: AirportListAdapter
    private var depart: Boolean = false
    private lateinit var dialog: Dialog
    private var pos = 0
    private lateinit var dialogTitle:TextView
    private var finalDate = ""
    private var finalMonth = ""
    private var finalYear = ""
    val lp = WindowManager.LayoutParams()
    private var flag = 0

    private val mOffersList = mutableListOf<GeneralFlightResponse>()
    private val mOffersList2 = mutableListOf<GeneralFlightResponse>()
    private lateinit var holderExtra: MultiCityAdapter.OfferViewHolder

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MultiCityAdapter.OfferViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_multy_city, parent, false)
        mOffersList.clear()
        for (m in offersList) {
            mOffersList.add(m)
        }
        dialog = Dialog(parent.context, R.style.full_screen_dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        datePickerDialog = Dialog(parent.context, R.style.full_screen_dialog)
        datePickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        lp.copyFrom(dialog.window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        showDialog()
        datePickerDialog()
        return OfferViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MultiCityAdapter.OfferViewHolder, @SuppressLint("RecyclerView") position: Int) {
        destinationCount = itemCount
        holder.flightNo.text = "Flight " + (position + 1)
        holder.checkOut.setOnClickListener {
            holderExtra = holder
            pos = position
            datePickerDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
            datePickerDialog.show()
            datePickerDialog.window!!.attributes = lp
        }
        holder.desireLocation.setOnClickListener {
            depart = true
            holderExtra = holder
            pos = position
            flag = 0
            showDialog()
            dialogTitle.text = "Departure City"
            dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
            dialog.show()
            dialog.window!!.attributes = lp
        }
        holder.arrivalLocation.setOnClickListener {
            depart = false
            holderExtra = holder
            flag = 1
            pos = position
            showDialog()
            dialogTitle.text = "Arrival City"
            dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
            dialog.show()
            dialog.window!!.attributes = lp
        }
    }

    override fun getItemCount(): Int = offers.size

    inner class OfferViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val desireLocation: ConstraintLayout = itemView.findViewById(R.id.desire_location)
        val arrivalLocation: ConstraintLayout = itemView.findViewById(R.id.check_in)
        val checkOut: ConstraintLayout = itemView.findViewById(R.id.check_out)

        var departCityCode: TextView = itemView.findViewById(R.id.departure_city_code)
        var arrivedCityCode: TextView = itemView.findViewById(R.id.arrived_city_code)

        val flightNo: TextView = itemView.findViewById(R.id.flight_no)
        var departCity: TextView = itemView.findViewById(R.id.departure_city)
        var arrivedCity: TextView = itemView.findViewById(R.id.arrived_city)
        var startDater: TextView = itemView.findViewById(R.id.start_dater)
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun showDialog() {
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.cutom_dialog_location)
        val recyclerView = dialog.findViewById<RecyclerView>(R.id.recyclerView)
        val location = dialog.findViewById<EditText>(R.id.location)
        airportListAdapter = AirportListAdapter("", offersList, this)
        dialogTitle = dialog.findViewById(R.id.title)

        viewModel.fetchOffers(flag)
        viewModel.airportList.observe(viewLifecycleOwner, {
            with(BookFlightFragment.mOffersList) {
                clear()
                it?.let { it1 -> addAll(it1) }
                airportListAdapter.notifyDataSetChanged()
            }
        })

        location.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            @SuppressLint("NotifyDataSetChanged")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.isEmpty()) {
                    airportListAdapter = AirportListAdapter("", offersList, this@MultiCityAdapter)
                    recyclerView.adapter = airportListAdapter
                } else {
                    try {

                        mOffersList2.clear()
                        airportListAdapter.notifyDataSetChanged()
                        airportListAdapter = AirportListAdapter(
                            s.toString().lowercase(Locale.getDefault()),
                            mOffersList2,
                            this@MultiCityAdapter
                        )
                        recyclerView.adapter = airportListAdapter
                        viewModel.getSelectedAirport("%${s}%")
                        viewModel.searchAirportList.observe(viewLifecycleOwner, {
                            with(mOffersList2) {
                                clear()
                                it?.let { it1 -> addAll(it1) }
                                airportListAdapter.notifyDataSetChanged()
                            }
                        })
                    } catch (e: Exception) {
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        recyclerView.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = airportListAdapter
        }
        val cancel = dialog.findViewById<TextView>(R.id.cancel)
        cancel.setOnClickListener {
            dialog.dismiss()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun selected(model: GeneralFlightResponse) {
        val apName = model.fly_from!!.split(',')[1]
        val apCode = model.fly_from.split(',')[0]
        dialog.dismiss()
        if (depart) {
            holderExtra.departCity.text = apName
            holderExtra.departCityCode.text = apCode
            airportDepart[pos] = apName
            airportDepartCode[pos] = apCode
            depart = false
            dialogTitle.text = "Arrival City"
            dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
            holderExtra.arrivalLocation.performClick()
            dialog.window!!.attributes = lp
        } else {
            holderExtra.arrivedCity.text = apName
            holderExtra.arrivedCityCode.text = apCode
            airportArrived[pos] = apName
            airportArrivedCode[pos] = apCode
            holderExtra.checkOut.performClick()
            datePickerDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
            datePickerDialog.show()
            datePickerDialog.window!!.attributes = lp
        }
    }

    @SuppressLint( "SetTextI18n", "WeekBasedYear", "NewApi")
    @RequiresApi(Build.VERSION_CODES.N)
    private fun datePickerDialog() {
        datePickerDialog.setCancelable(false)
        datePickerDialog.setContentView(R.layout.cutom_dialog_datepicker_oneway)
        val nextYear = Calendar.getInstance()
        nextYear.add(Calendar.YEAR, 10)
        val calendar: CalendarPickerView = datePickerDialog.findViewById(R.id.calendar_view)

        val apply: Button = datePickerDialog.findViewById(R.id.cancel)

        apply.setOnClickListener {
            holderExtra.startDater.text = "$finalMonth $finalDate, $finalYear"
            dateTravel[pos] = "$finalMonth+$finalDate%2C+$finalYear"
            dateTravelForApi[pos] = "$finalMonth $finalDate, $finalYear"
            datePickerDialog.cancel()
        }

        calendar.init(
            Date(), nextYear.time,
            SimpleDateFormat("MMMM YYYY", Locale.getDefault())
        ).inMode(CalendarPickerView.SelectionMode.SINGLE)

        calendar.setOnDateSelectedListener(object : CalendarPickerView.OnDateSelectedListener {
            @SuppressLint("SetTextI18n", "UseCompatLoadingForColorStateLists")
            override fun onDateSelected(dater: Date?) {
                finalDate = DateFormat.format("dd", dater!!).toString()
                finalMonth = DateFormat.format("MMM", dater).toString()
                finalYear = DateFormat.format("yyyy", dater).toString()
            }

            @SuppressLint("SetTextI18n")
            override fun onDateUnselected(date: Date?) {

            }

        })
    }


}