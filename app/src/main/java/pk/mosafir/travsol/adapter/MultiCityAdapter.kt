package pk.mosafir.travsol.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.text.format.DateFormat
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.savvi.rangedatepicker.CalendarPickerView
import pk.mosafir.travsol.CustomDialog
import pk.mosafir.travsol.R
import pk.mosafir.travsol.interfaces.AirportSelector
import pk.mosafir.travsol.response.GeneralFlightResponse
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
    private val viewLifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<MultiCityAdapter.OfferViewHolder>(), AirportSelector {
    private lateinit var datePickerDialog: Dialog
    private var depart: Boolean = false
    private lateinit var dialog: CustomDialog
    private lateinit var dialogArrival: CustomDialog
    private var pos = 0
    private var finalDate = ""
    private var finalMonth = ""
    private var finalYear = ""
    private val lp = WindowManager.LayoutParams()
    private var flag = 0
    private lateinit var holderExtra: MultiCityAdapter.OfferViewHolder
    private lateinit var context:Context
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MultiCityAdapter.OfferViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_multy_city, parent, false)
        context = parent.context
        datePickerDialog = Dialog(parent.context, R.style.full_screen_dialog)
        datePickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        lp.copyFrom(datePickerDialog.window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        return OfferViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MultiCityAdapter.OfferViewHolder, @SuppressLint("RecyclerView") position: Int) {
        destinationCount = itemCount
        holder.flightNo.text = "Flight " + (position + 1)
        holder.checkOut.setOnClickListener {
            holderExtra = holder
            pos = position
            datePickerDialog()
            //datePickerDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
            datePickerDialog.show()
            datePickerDialog.window!!.attributes = lp
        }
        holder.desireLocation.setOnClickListener {
            depart = true
            holderExtra = holder
            pos = position
            flag = 0
            dialog = CustomDialog("Departure City", context, this@MultiCityAdapter, viewModel, viewLifecycleOwner, R.style.full_screen_dialog)
            showDialog(dialog)
        }
        holder.arrivalLocation.setOnClickListener {
            depart = false
            holderExtra = holder
            flag = 1
            pos = position
            dialogArrival = CustomDialog("Arrival City", context, this@MultiCityAdapter, viewModel, viewLifecycleOwner, R.style.full_screen_dialog)
            showDialog(dialogArrival)
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


    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    override fun selected(model: GeneralFlightResponse) {
        val apName = model.fly_from!!.split(',')[1]
        val apCode = model.fly_from.split(',')[0]
        if (depart) {
            dialog.dismiss()
            holderExtra.departCity.text = apName
            holderExtra.departCityCode.text = apCode
            airportDepart[pos] = apName
            airportDepartCode[pos] = apCode
            holderExtra.arrivalLocation.performClick()
        } else {
            dialogArrival.dismiss()
            holderExtra.arrivedCity.text = apName
            holderExtra.arrivedCityCode.text = apCode
            airportArrived[pos] = apName
            airportArrivedCode[pos] = apCode
            datePickerDialog()
            holderExtra.checkOut.performClick()
//            datePickerDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
            datePickerDialog.show()
            datePickerDialog.window?.attributes = lp
        }
    }
    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun showDialog(dialogue: CustomDialog) {
        //dialogue.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogue.initialize()
        dialogue.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialogue.show()
        dialogue.window?.attributes = lp
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