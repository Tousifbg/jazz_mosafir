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
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.savvi.rangedatepicker.CalendarPickerView
import pk.mosafir.travsol.R
import pk.mosafir.travsol.adapter.AirportListAdapter
import pk.mosafir.travsol.databinding.FragmentOneWayBinding
import pk.mosafir.travsol.interfaces.AirportSelector
import pk.mosafir.travsol.model.RecentAirportModal
import pk.mosafir.travsol.response.GeneralFlightResponse
import pk.mosafir.travsol.ui.base.FlightBaseFragment
import pk.mosafir.travsol.ui.book_flight.BookFlightFragment.Companion.mOffersList
import pk.mosafir.travsol.ui.book_flight.BookFlightFragment.Companion.mOffersListArrive
import pk.mosafir.travsol.ui.book_hotel.BookHotelFragment.Companion.flightBindingModel
import pk.mosafir.travsol.utils.*
import pk.mosafir.travsol.webview.WebViewActivity
import java.text.SimpleDateFormat
import java.util.*

class OneWayFragment : FlightBaseFragment(), View.OnClickListener, AirportSelector {
    private lateinit var _binding: FragmentOneWayBinding
    private val binding get() = _binding

    private val mOffersList2 = mutableListOf<GeneralFlightResponse>()
    private lateinit var airportListAdapter: AirportListAdapter
    private lateinit var airportListAdapterArrive: AirportListAdapter
    private lateinit var bottomSheetDialog: BottomSheetDialog

    private var adult = 1
    private var children = 0
    private var infant = 0

    private lateinit var dialog: Dialog
    private lateinit var dialogArrival: Dialog
    private lateinit var datePickerDialog: Dialog

    private lateinit var done: Button

    private var arrivedCity = ""
    private var cabinClass = ""
    private var arrivedCityCode = ""
    private var departCity = ""
    private var departCityCode = ""

    private var finalDate = ""
    private var finalMonth = ""
    private var finalYear = ""

    private var depart: Boolean = true
    private var economy: Boolean = false
    private var business: Boolean = false

    private lateinit var dialogTitle: TextView

    private lateinit var tvAdult: TextView
    private lateinit var tvChild: TextView
    private lateinit var tvInfant: TextView

    private lateinit var plusAdult: TextView
    private lateinit var plusChild: TextView
    private lateinit var plusInfant: TextView

    private lateinit var minusAdult: TextView
    private lateinit var minusChild: TextView
    private lateinit var minusInfant: TextView
    private val lp = WindowManager.LayoutParams()

    private lateinit var radioGroup: RadioGroup
    private var flag = 0

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOneWayBinding.inflate(inflater, container, false)
        bottomSheetDialog = BottomSheetDialog(requireContext())
        dialog = Dialog(requireActivity(), R.style.full_screen_dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogArrival = Dialog(requireActivity(), R.style.full_screen_dialog)
        dialogArrival.requestWindowFeature(Window.FEATURE_NO_TITLE)
        datePickerDialog = Dialog(requireActivity(), R.style.full_screen_dialog)
        datePickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        lp.copyFrom(bottomSheetDialog.window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        binding.oneWayBinding = flightBindingModel
        binding.travellers.setOnClickListener {
            bottomSheetDialog.window!!.attributes.windowAnimations = R.style.BottomAnimation
            bottomSheetDialog.show()
        }
        binding.departure.setOnClickListener {
            flag = 0
            dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
            depart = true
            dialogTitle.text = "Departure City"
            dialog.show()
            dialog.window!!.attributes = lp
        }
        binding.arrival.setOnClickListener {
            flag = 1
            depart = false
            dialogArrival.window!!.attributes.windowAnimations = R.style.DialogAnimation
            dialogTitle.text = "Arrival City"
            dialogArrival.show()
            dialogArrival.window!!.attributes = lp
        }
        binding.date.setOnClickListener {
            datePickerDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
            datePickerDialog.show()
            datePickerDialog.window!!.attributes = lp
        }
        if (loggedIn) {
            binding.signIn.invisible()
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
                else -> {
                    viewModel.putAirportRecent(
                        RecentAirportModal(
                            getTempKey(),
                            "" + getUserId(),
                            "oneWay",
                            "$departCityCode ,$departCity",
                            "$arrivedCityCode ,$arrivedCity",
                            "$finalMonth $finalDate, $finalYear",
                            "",
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
        binding.signIn.setOnClickListener(this)
        showBottomSheetDialog()
        showDialog()
        showDialogArrival()
        datePickerDialog()
        return binding.root
    }

    @SuppressLint("NewApi", "CutPasteId", "WeekBasedYear")
    @RequiresApi(Build.VERSION_CODES.N)
    private fun datePickerDialog() {
        datePickerDialog.setCancelable(false)
        datePickerDialog.setContentView(R.layout.cutom_dialog_datepicker_oneway)
        val nextYear = Calendar.getInstance()
        nextYear.add(Calendar.YEAR, 10)
        val calendar = datePickerDialog.findViewById<CalendarPickerView>(R.id.calendar_view)

        val apply = datePickerDialog.findViewById<Button>(R.id.cancel)

        apply.setOnClickListener(this)

        calendar.init(
            Date(), nextYear.time,
            SimpleDateFormat("MMMM YYYY", Locale.getDefault())
        ).inMode(CalendarPickerView.SelectionMode.SINGLE)

        calendar.setOnDateSelectedListener(object : CalendarPickerView.OnDateSelectedListener {
            @SuppressLint("SetTextI18n", "UseCompatLoadingForColorStateLists")
            override fun onDateSelected(date: Date?) {
                finalDate = DateFormat.format("dd", date!!).toString()
                finalMonth = DateFormat.format("MMM", date).toString()
                finalYear = DateFormat.format("yyyy", date).toString()
            }

            @SuppressLint("SetTextI18n")
            override fun onDateUnselected(date: Date?) {

            }

        })
    }

    private fun showBottomSheetDialog() {
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_flight)
        radioGroup = bottomSheetDialog.findViewById(R.id.radioGroup)!!
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.economy) {
                economy = true
                business = false
            } else if (checkedId == R.id.business) {
                economy = false
                business = true
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
            R.id.cancel -> {
                binding.startDater.text = "$finalMonth $finalDate, $finalYear"
                datePickerDialog.cancel()
                flightBindingModel.fromDate = "$finalMonth $finalDate, $finalYear"
                bottomSheetDialog.window!!.attributes.windowAnimations = R.style.BottomAnimation
                bottomSheetDialog.show()
            }
            R.id.done -> {
                var travels = "${adult + children + infant} Traveller(s)/"
                bottomSheetDialog.dismiss()
                if (economy) {
                    travels = "${adult + children + infant} Traveller(s)/Economy"
                } else if (business) {
                    travels = "${adult + children + infant} Traveller(s)/Business"
                }
                binding.travellersTv.text = travels
                flightBindingModel.traveller = travels
            }
            R.id.signIn -> {
                val url = when {
                    !loggedIn -> {
                        "https://mosafir.pk/mobile/Flights/user_login"
                    }
                    else -> {
                        "https://mosafir.pk/mobile/Admin/dashboard"
                    }
                }
                val intent = Intent(requireContext(), WebViewActivity::class.java)
                intent.putExtra("url", url)
                startActivity(intent)
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
        }
    }

    override fun selected(model: GeneralFlightResponse) {
        val apName = model.fly_from!!.split(',')[1]
        val apCode = model.fly_from.split(',')[0]
        if (depart) {
            binding.departureCity.text = apName
            binding.departureCityCode.text = apCode
            flightBindingModel.from = apName
            flightBindingModel.fromCode = apCode
            departCity = apName
            departCityCode = apCode
            dialog.dismiss()
            binding.arrival.callOnClick()
        } else {
            binding.arrivedCity.text = apName
            binding.arrivedCityCode.text = apCode
            arrivedCity = apName
            arrivedCityCode = apCode
            flightBindingModel.to = apName
            flightBindingModel.toCode = apCode
            dialogArrival.dismiss()
            datePickerDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
            datePickerDialog.show()
            datePickerDialog.window!!.attributes = lp
        }

    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun showDialog() {
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.cutom_dialog_location)
        val recyclerView = dialog.findViewById<RecyclerView>(R.id.recyclerView)
        val location = dialog.findViewById<EditText>(R.id.location)
        mOffersList.clear()
        airportListAdapter = AirportListAdapter("", mOffersList, this)
        airportListAdapter.notifyDataSetChanged()

        recyclerView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = airportListAdapter
        }


        viewModel.fetchOffers(0)
        viewModel.airportList.observe(viewLifecycleOwner, {
            with(mOffersList) {
                clear()
                it?.let { it1 -> addAll(it1) }
                airportListAdapter.notifyDataSetChanged()
            }
        })
        dialogTitle = dialog.findViewById(R.id.title)
        location.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.isEmpty()) {
                    airportListAdapter = AirportListAdapter("", mOffersList, this@OneWayFragment)
                    recyclerView.adapter = airportListAdapter
                } else {

                    mOffersList2.clear()
                    airportListAdapter.notifyDataSetChanged()
                    airportListAdapter = AirportListAdapter(
                        s.toString().lowercase(Locale.getDefault()),
                        mOffersList2,
                        this@OneWayFragment
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
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        val cancel = dialog.findViewById<TextView>(R.id.cancel)
        cancel.setOnClickListener {
            dialog.dismiss()
        }
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun showDialogArrival() {
        dialogArrival.setCancelable(false)
        dialogArrival.setContentView(R.layout.cutom_dialog_location)
        val recyclerView = dialogArrival.findViewById<RecyclerView>(R.id.recyclerView)
        val location = dialogArrival.findViewById<EditText>(R.id.location)
        mOffersListArrive.clear()
        airportListAdapterArrive = AirportListAdapter("", mOffersListArrive, this)
        airportListAdapterArrive.notifyDataSetChanged()

        recyclerView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = airportListAdapterArrive
        }


        viewModel.fetchOffersArrival(1)
        viewModel.airportList.observe(viewLifecycleOwner, {
            with(mOffersListArrive) {
                clear()
                it?.let { it1 -> addAll(it1) }
                airportListAdapterArrive.notifyDataSetChanged()
            }
        })
        dialogTitle = dialogArrival.findViewById(R.id.title)
        location.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.isEmpty()) {
                    airportListAdapterArrive = AirportListAdapter("", mOffersListArrive, this@OneWayFragment)
                    recyclerView.adapter = airportListAdapterArrive
                } else {

                    mOffersList2.clear()
                    airportListAdapterArrive.notifyDataSetChanged()
                    airportListAdapterArrive = AirportListAdapter(
                        s.toString().lowercase(Locale.getDefault()),
                        mOffersList2,
                        this@OneWayFragment
                    )
                    recyclerView.adapter = airportListAdapterArrive
                    viewModel.getSelectedAirport("%${s}%")
                    viewModel.searchAirportList.observe(viewLifecycleOwner, {
                        with(mOffersList2) {
                            clear()
                            it?.let { it1 -> addAll(it1) }
                            airportListAdapterArrive.notifyDataSetChanged()
                        }
                    })
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        val cancel = dialogArrival.findViewById<TextView>(R.id.cancel)
        cancel.setOnClickListener {
            dialogArrival.dismiss()
        }
    }

    private fun getUrl(): String {
        val url = "https://mosafir.pk/mobile/Flights/listing?one_way_trip=1" +
                "&origin=$departCityCode%2C$departCity" +
                "&destination=$arrivedCityCode%2C$arrivedCity" +
                "&preferredDepDate=$finalMonth+$finalDate%2C+$finalYear" +
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