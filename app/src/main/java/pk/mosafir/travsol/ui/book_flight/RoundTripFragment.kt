package pk.mosafir.travsol.ui.book_flight

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.savvi.rangedatepicker.CalendarPickerView
import pk.mosafir.travsol.CustomDialog
import pk.mosafir.travsol.R
import pk.mosafir.travsol.adapter.AirportListAdapter
import pk.mosafir.travsol.databinding.FragmentRoundTripBinding
import pk.mosafir.travsol.interfaces.AirportSelector
import pk.mosafir.travsol.model.RecentAirportModal
import pk.mosafir.travsol.response.GeneralFlightResponse
import pk.mosafir.travsol.ui.MainActivity
import pk.mosafir.travsol.ui.account.AccountFragment
import pk.mosafir.travsol.ui.account.LoggedInFragment
import pk.mosafir.travsol.ui.base.FlightBaseFragment
import pk.mosafir.travsol.ui.book_flight.BookFlightFragment.Companion.mOffersList
import pk.mosafir.travsol.ui.book_hotel.BookHotelFragment.Companion.roundBindingModel
import pk.mosafir.travsol.utils.*
import pk.mosafir.travsol.webview.WebViewActivity
import java.text.SimpleDateFormat
import java.util.*

class RoundTripFragment : FlightBaseFragment(), View.OnClickListener, AirportSelector {
    private lateinit var _binding: FragmentRoundTripBinding
    private val binding get() = _binding

    private val lp = WindowManager.LayoutParams()
    private lateinit var dialogArrival: CustomDialog
    private var cabinClass = ""

    //travellers
    private var adult = 1
    private var children = 0
    private var infant = 0

    //flight details
    private var arrivedCity = ""
    private var arrivedCityCode = ""
    private var departCity = ""
    private var departCityCode = ""

    //starting date
    private var finalDateStart = ""
    private var finalMonthStart = ""
    private var finalYearStart = ""

    //ending date
    private var finalYearEnd = ""
    private var finalDateEnd = ""
    private var finalMonthEnd = ""

    private lateinit var done: Button
    private var depart: Boolean = true
    private var economy: Boolean = false
    private var business: Boolean = false

    private lateinit var tvAdult: TextView
    private lateinit var tvChild: TextView
    private lateinit var tvInfant: TextView

    private lateinit var plusAdult: TextView
    private lateinit var plusChild: TextView
    private lateinit var plusInfant: TextView

    private lateinit var minusAdult: TextView
    private lateinit var minusChild: TextView
    private lateinit var minusInfant: TextView

    private lateinit var radioGroup: RadioGroup

    private lateinit var dialog: CustomDialog
    private lateinit var datePickerDialog: Dialog
    private lateinit var bottomSheetDialog: BottomSheetDialog

    private var flag = 0
    private lateinit var transaction: FragmentTransaction
    private lateinit var selectedFragment: Fragment

    fun openFragment(selectedFragment: Fragment) {
        transaction = MainActivity.fragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, selectedFragment).addToBackStack(null)
        transaction.commit()

    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRoundTripBinding.inflate(inflater, container, false)
        bottomSheetDialog = BottomSheetDialog(requireContext())
        datePickerDialog = Dialog(requireActivity(), R.style.full_screen_dialog)
        datePickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding.roundWayBinding = roundBindingModel
        if (loggedIn) {
            binding.signIn.invisible()
        }
        lp.copyFrom(bottomSheetDialog.window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT

        binding.signIn.setOnClickListener(this)
        binding.departure.setOnClickListener(this)
        binding.arrival.setOnClickListener(this)
        binding.startDate.setOnClickListener {
            datePickerDialog()
            //datePickerDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
            datePickerDialog.show()
            datePickerDialog.window!!.attributes = lp
        }
        binding.dateEnd.setOnClickListener {
            datePickerDialog()
            //datePickerDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
            datePickerDialog.show()
            datePickerDialog.window!!.attributes = lp
        }
        binding.travellers.setOnClickListener {
            showBottomSheetDialog()
            bottomSheetDialog.window!!.attributes.windowAnimations = R.style.BottomAnimation
            bottomSheetDialog.show()
        }
        binding.search.setOnClickListener {
            when {
                binding.departureCity.text.toString().isEmpty() -> {
                    binding.error.visible()
                    binding.error.text = "The Location field is required"
                }
                binding.arrivedCity.text.toString().isEmpty() -> {
                    binding.error.visible()
                    binding.error.text = "The Location field is required"
                }
                binding.startDater.text.toString().isEmpty() -> {
                    binding.error.visible()
                    binding.error.text = "The Departure Date field is required"
                }
                binding.endDate.text.toString().isEmpty() -> {
                    binding.error.visible()
                    binding.error.text = "The Arrival Date field is required"
                }
                else -> {
                    viewModel.putAirportRecent(
                        RecentAirportModal(
                            getTempKey(),
                            "0",
                            "roundTrip",
                            "$departCityCode ,$departCity",
                            "$arrivedCityCode ,$arrivedCity",
                            "$finalMonthStart $finalDateStart, $finalYearStart",
                            "$finalMonthEnd $finalDateEnd, $finalYearEnd",
                            "$adult",
                            "$children",
                            "$infant",
                            cabinClass
                        )
                    )
                    binding.error.invisible()
                    val intent = Intent(requireContext(), WebViewActivity::class.java)
                    intent.putExtra("url", getUrl())
                    requireActivity().startActivity(intent)
                }
            }
        }
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onResume() {
        super.onResume()
    }
    @SuppressLint("NewApi", "WeekBasedYear", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.N)
    private fun datePickerDialog() {
        datePickerDialog.setCancelable(false)
        datePickerDialog.setContentView(R.layout.cutom_dialog_datepicker)
        val nextYear = Calendar.getInstance()
        nextYear.add(Calendar.YEAR, 10)
        val calendar = datePickerDialog.findViewById<CalendarPickerView>(R.id.calendar_view)
        val title = datePickerDialog.findViewById<TextView>(R.id.title)
        title.text = "Select Date Range"
        val exit = datePickerDialog.findViewById<TextView>(R.id.exit)
        val clear = datePickerDialog.findViewById<TextView>(R.id.clear)

        val dateTv = datePickerDialog.findViewById<TextView>(R.id.dateTv)
        val daysTv = datePickerDialog.findViewById<TextView>(R.id.days)
        dateTv.gone()
        daysTv.gone()
        val apply = datePickerDialog.findViewById<Button>(R.id.apply)
        apply.isEnabled = false

        apply.setOnClickListener(this)
        exit.setOnClickListener(this)
        clear.setOnClickListener(this)

        calendar.init(
            Date(), nextYear.time,
            SimpleDateFormat("MMMM YYYY", Locale.getDefault())
        ).inMode(CalendarPickerView.SelectionMode.RANGE)

        calendar.setOnDateSelectedListener(object : CalendarPickerView.OnDateSelectedListener {
            @SuppressLint("SetTextI18n", "UseCompatLoadingForColorStateLists")
            override fun onDateSelected(date: Date?) {
                try {
                    val dates = calendar.selectedDates
                    val count = dates.count()
                    val startDate = dates[0]

                    finalDateStart = DateFormat.format("dd", startDate!!).toString()
                    finalMonthStart = DateFormat.format("MMM", startDate).toString()
                    finalYearStart = DateFormat.format("yyyy", startDate).toString()

                    if (count > 1) {
                        for (date_v in dates) {
                            finalDateEnd = DateFormat.format("dd", date_v).toString()
                            finalMonthEnd = DateFormat.format("MMM", date_v).toString()
                            finalYearEnd = DateFormat.format("yyyy", date_v).toString()
                        }
                        dateTv.text =
                            "$finalMonthStart $finalDateStart - $finalMonthEnd $finalDateEnd"
                        val nights = count - 1
                        daysTv.text = " , $nights Night(s)"

                        apply.isEnabled = true
                        apply.backgroundTintList =
                            resources.getColorStateList(R.color.colorRed, requireContext().theme)
                    } else {
                        dateTv.text = "Select Checkout Date"
                        apply.isEnabled = false
                        apply.backgroundTintList =
                            resources.getColorStateList(R.color.colorGray, requireContext().theme)
                    }
                } catch (e: Exception) {
                    requireContext().toast(e.message.toString())
                }
            }

            @SuppressLint("SetTextI18n")
            override fun onDateUnselected(date: Date?) {
                dateTv.text = "Select Checkout Date"
                daysTv.text = ""
            }

        })
        val cancel = datePickerDialog.findViewById<TextView>(R.id.cancel)
        cancel.setOnClickListener {
            datePickerDialog.dismiss()
        }
    }

    private fun showBottomSheetDialog() {
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_flight)
        radioGroup = bottomSheetDialog.findViewById(R.id.radioGroup)!!
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when {
                checkedId != R.id.economy -> {
                    if (checkedId == R.id.business) {
                        economy = false
                        business = true
                    }
                }
                else -> {
                    economy = true
                    business = false
                }
            }
        }

        tvAdult = bottomSheetDialog.findViewById(R.id.tv_adult)!!
        tvChild = bottomSheetDialog.findViewById(R.id.tv_child)!!
        tvInfant = bottomSheetDialog.findViewById(R.id.tv_infant)!!

        tvAdult.text = adult.toString()
        tvChild.text = children.toString()
        tvInfant.text = infant.toString()

        done = bottomSheetDialog.findViewById(R.id.done)!!
        plusAdult = bottomSheetDialog.findViewById(R.id.plus_adult)!!
        plusChild = bottomSheetDialog.findViewById(R.id.plus_child)!!
        plusInfant = bottomSheetDialog.findViewById(R.id.plus_infant)!!

        minusAdult = bottomSheetDialog.findViewById(R.id.minus_adult)!!
        minusChild = bottomSheetDialog.findViewById(R.id.minus_child)!!
        minusInfant = bottomSheetDialog.findViewById(R.id.minus_infant)!!

        plusAdult.setOnClickListener(this)
        plusChild.setOnClickListener(this)
        plusInfant.setOnClickListener(this)

        minusAdult.setOnClickListener(this)
        minusChild.setOnClickListener(this)
        minusInfant.setOnClickListener(this)

        done.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.done -> {
                var travs = "${adult + children + infant} Traveller(s)/"
                bottomSheetDialog.dismiss()
                when {
                    economy -> {
                        travs = "${travs}/Economy"
                    }
                    business -> {
                        travs =
                            "$travs/Business"
                    }
                }
                binding.travellersTv.text = travs
                roundBindingModel.traveller = travs
            }
            R.id.signIn -> {
                selectedFragment = if (loggedIn) {
                    LoggedInFragment()
                } else {
                    AccountFragment()
                }
                openFragment(selectedFragment)
            }
            R.id.minus_adult -> {
                if (adult > 1) {
                    adult--
                    tvAdult.text = "$adult"
                }
            }
            R.id.minus_child -> {
                if (children > 0) {
                    children--
                    tvChild.text = "$children"
                }
            }
            R.id.minus_infant -> {
                if (infant > 0) {
                    infant--
                    tvInfant.text = "$infant"
                }
            }
            R.id.plus_adult -> {
                if (adult < 10) {
                    adult++
                    tvAdult.text = "$adult"
                }
            }
            R.id.plus_child -> {
                if (children < 10) {
                    children++
                    tvChild.text = "$children"
                }
            }
            R.id.plus_infant -> {
                if (infant < 10) {
                    infant++
                    tvInfant.text = "$infant"
                }
            }
            R.id.arrival -> {
                flag = 1
                depart = false
                dialogArrival = CustomDialog("Arrival City", requireContext(), this@RoundTripFragment, viewModel, viewLifecycleOwner, R.style.full_screen_dialog)
                showDialog(dialogArrival)            }
            R.id.departure -> {
                flag = 0
                depart = true
                dialog = CustomDialog("Departure City", requireContext(), this@RoundTripFragment, viewModel, viewLifecycleOwner, R.style.full_screen_dialog)
                showDialog(dialog)
                //dialog.window!!.attributes = lp
            }
            R.id.exit -> {
                datePickerDialog.cancel()
            }
            R.id.clear -> {
                datePickerDialog.cancel()
            }
            R.id.apply -> {
                binding.startDater.text = "$finalMonthStart $finalDateStart, $finalYearStart"
                binding.endDate.text = "$finalMonthEnd $finalDateEnd, $finalYearEnd"

                roundBindingModel.fromDate = "$finalMonthStart $finalDateStart, $finalYearStart"
                roundBindingModel.toDate = "$finalMonthEnd $finalDateEnd, $finalYearEnd"

                datePickerDialog.cancel()
                showBottomSheetDialog()
                bottomSheetDialog.window!!.attributes.windowAnimations = R.style.BottomAnimation
                bottomSheetDialog.show()
            }
        }
    }
    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun showDialog(dialogue: CustomDialog) {
//        dialogue.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogue.initialize()
        dialogue.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialogue.show()
        dialogue.window?.attributes = lp
    }
    @RequiresApi(Build.VERSION_CODES.N)
    override fun selected(model: GeneralFlightResponse) {
        val apName = model.fly_from!!.split(',')[1]
        val apCode = model.fly_from.split(',')[0]
        when {
            depart -> {
                binding.departureCity.text = apName
                binding.departureCityCode.text = apCode
                departCity = apName
                departCityCode = apCode
                roundBindingModel.from = apName
                roundBindingModel.fromCode = apCode
                dialog.dismiss()
                binding.arrival.callOnClick()
            }
            else -> {
                binding.arrivedCity.text = apName
                binding.arrivedCityCode.text = apCode
                arrivedCity = apName
                arrivedCityCode = apCode
                roundBindingModel.to = apName
                roundBindingModel.toCode = apCode
                dialogArrival.dismiss()
                datePickerDialog()
                //datePickerDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
                datePickerDialog.show()
                datePickerDialog.window?.attributes = lp
            }
        }
    }

    private fun getUrl(): String {
        val url = "https://mosafir.pk/mobile/Flights/listing?round_trip=1" +
                "&origin=$departCityCode%2C$departCity" +
                "&destination=$arrivedCityCode%2C$arrivedCity" +
                "&preferredDepDate=$finalMonthStart+$finalDateStart%2C+$finalYearStart" +
                "&PreferredRetDate=$finalMonthEnd+$finalDateEnd%2C+$finalYearEnd" +
                "&adult=$adult" +
                "&children=$children" +
                "&infant=$infant"
        return when {
            economy -> {
                cabinClass = "Economy"
                "$url&cabinClass=Economy"
            }
            business -> {
                cabinClass = "Business"
                "$url&cabinClass=Business"
            }
            else -> url
        }
    }
}