package pk.mosafir.travsol.ui.book_hotel

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
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.savvi.rangedatepicker.CalendarPickerView
import org.koin.androidx.viewmodel.ext.android.viewModel
import pk.mosafir.travsol.R
import pk.mosafir.travsol.adapter.HotelListAdapter
import pk.mosafir.travsol.adapter.HotelRoomAdapter
import pk.mosafir.travsol.databinding.FragmentBookHotelBinding
import pk.mosafir.travsol.formbinding.FlightBinding
import pk.mosafir.travsol.formbinding.HotelBinding
import pk.mosafir.travsol.interfaces.CitySelector
import pk.mosafir.travsol.response.HotelLocation
import pk.mosafir.travsol.ui.home.HomeFragment
import pk.mosafir.travsol.utils.*
import pk.mosafir.travsol.viewmodel.BookHotelViewModel
import pk.mosafir.travsol.webview.WebViewActivity
import java.text.SimpleDateFormat
import java.util.*

class BookHotelFragment : Fragment(), CitySelector, View.OnClickListener {
    private lateinit var _binding: FragmentBookHotelBinding
    private val binding get() = _binding
    private var hotelId = ""
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var dialog: Dialog
    private lateinit var datePickerDialog: Dialog

    private val mOffersList = mutableListOf<HotelLocation>()
    private val mOffersList2 = mutableListOf<HotelLocation>()
    private val dummyList = mutableListOf<Int>()
    private val lp = WindowManager.LayoutParams()

    private var adult = 1

    private lateinit var plusAdult: TextView
    private lateinit var tvAdult: TextView
    private lateinit var minusAdult: TextView

    private lateinit var hotelRoomAdapter: HotelRoomAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var done: Button

    private var finalDateStart = ""
    private var finalMonthStart = ""
    private var finalYearStart = ""

    private var finalYearEnd = ""
    private var finalDateEnd = ""
    private var finalMonthEnd = ""

    private var checkInDate = ""
    private var checkOutDate = ""

    private var departCity = ""
    private var urlLink = ""
    private var cityAdapter = HotelListAdapter("", mOffersList, this)
    private val viewModel: BookHotelViewModel by viewModel()

    companion object {
        var hotelBindingModal: HotelBinding = HotelBinding()
        var tourBindingModal: HotelBinding = HotelBinding()
        var flightBindingModel: FlightBinding = FlightBinding()
        var roundBindingModel: FlightBinding = FlightBinding()
        var rooms = 0
        var adults = arrayOf(1, 0, 0, 0, 0, 0)
        var childList = arrayOf(0, 0, 0, 0, 0, 0)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookHotelBinding.inflate(inflater, container, false)
        dialog = Dialog(requireActivity(), R.style.full_screen_dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        datePickerDialog = Dialog(requireActivity(), R.style.full_screen_dialog)
        datePickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        hotelRoomAdapter = HotelRoomAdapter(dummyList)
        bottomSheetDialog = BottomSheetDialog(requireContext())
        binding.hotelBinding = hotelBindingModal

        lp.copyFrom(bottomSheetDialog.window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT

        binding.desireLocation.setOnClickListener {
            showDialog()
            dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
            dialog.show()
            dialog.window!!.attributes = lp
        }
        binding.checkIn.setOnClickListener {
            datePickerDialog()
            //datePickerDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
            datePickerDialog.show()
            datePickerDialog.window!!.attributes = lp
        }
        binding.checkOut.setOnClickListener {
            datePickerDialog()
            //datePickerDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
            datePickerDialog.show()
            datePickerDialog.window!!.attributes = lp
        }
        binding.topToolBar.back.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            transaction.replace(R.id.nav_host_fragment, HomeFragment(), "MY_FRAGMENT")
            transaction.commit()
        }
        if (loggedIn) {
            binding.signIn.invisible()
        }
        binding.signIn.setOnClickListener(this)
        binding.persons.setOnClickListener {
            showBottomSheetDialog()
            bottomSheetDialog.window!!.attributes.windowAnimations = R.style.BottomAnimation
            bottomSheetDialog.show()
        }
        binding.topToolBar.title.text = "Search Hotels"
        binding.search.setOnClickListener {

            when {
                binding.departureCity.text.toString().isEmpty() -> {
                    binding.error.visible()
                    binding.error.text = "The Location or Category field is required"
                }
                binding.checkInDate.text.isEmpty() -> {
                    binding.error.visible()
                    binding.error.text = "The Check In field is required"
                }
                binding.checkOutDate.text.toString().isEmpty() -> {
                    binding.error.visible()
                    binding.error.text = "The Check In field is required"
                }
                else -> {

                    hotelId.let { viewModel.putRecentSearches(it) }

                    val intent = Intent(requireContext(), WebViewActivity::class.java)
                    intent.putExtra("url", urlLink)
                    requireActivity().startActivity(intent)

                }
            }
        }




        return binding.root
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
                    when {
                        count > 1 -> {
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
                            apply.backgroundTintList = resources.getColorStateList(
                                R.color.colorRed,
                                requireContext().theme
                            )
                        }
                        else -> {
                            dateTv.text = "Select Checkout Date"
                            apply.isEnabled = false
                            apply.backgroundTintList = resources.getColorStateList(
                                R.color.colorGray,
                                requireContext().theme
                            )
                        }
                    }
                    //requireContext().toast("$count/$date/$dateInt/$monthI")
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

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun showDialog() {
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.cutom_dialog_location)
        val recyclerView = dialog.findViewById<RecyclerView>(R.id.recyclerView)
        val location = dialog.findViewById<EditText>(R.id.location)
        if (hotelBindingModal.city!!.isNotEmpty()) {
            if (!hotelBindingModal.city!!.contains("Pakistan")) {
                location.setText("${hotelBindingModal.city} , Pakistan")
            } else {
                location.setText(hotelBindingModal.city)
            }
        }
        val title = dialog.findViewById<TextView>(R.id.title)
        title.text = "City/Hotel Name"
        if (hotelBindingModal.city!!.isEmpty()) {
            viewModel.fetchOffers(hotelBindingModal.city!!)
            viewModel.hotelCityList.observe(viewLifecycleOwner, {
                with(mOffersList) {
                    clear()
                    it?.let { it1 -> addAll(it1) }
                    cityAdapter.notifyDataSetChanged()
                }
            })
            cityAdapter = HotelListAdapter(
                "",
                mOffersList,
                this@BookHotelFragment
            )
        } else {
            viewModel.getSelectedHotels(hotelBindingModal.city!!)
            cityAdapter = HotelListAdapter(
                location.text.toString().lowercase(Locale.getDefault()),
                mOffersList2,
                this@BookHotelFragment
            )
            viewModel.hotelCitySearch.observe(viewLifecycleOwner, {
                with(mOffersList2) {
                    clear()
                    it?.let { it1 -> addAll(it1) }
                    cityAdapter.notifyDataSetChanged()
                }
            })
        }
        recyclerView.adapter = cityAdapter
        location.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                when {
                    s!!.isEmpty() -> {
                        cityAdapter = HotelListAdapter("", mOffersList, this@BookHotelFragment)
                        recyclerView.adapter = cityAdapter
                    }
                    else -> {
                        mOffersList2.clear()
                        cityAdapter.notifyDataSetChanged()
                        cityAdapter = HotelListAdapter(
                            s.toString().lowercase(Locale.getDefault()),
                            mOffersList2,
                            this@BookHotelFragment
                        )
                        recyclerView.adapter = cityAdapter
                        viewModel.getSelectedHotels("%${s}%")
                        viewModel.hotelCitySearch.observe(viewLifecycleOwner, {
                            with(mOffersList2) {
                                clear()
                                it?.let { it1 -> addAll(it1) }
                                cityAdapter.notifyDataSetChanged()
                            }
                        })

                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        recyclerView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = cityAdapter
        }
        val cancel = dialog.findViewById<TextView>(R.id.cancel)
        cancel.setOnClickListener {
            dialog.dismiss()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showBottomSheetDialog() {
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_hotel)
        dummyList.clear()
        dummyList.add(1)
        hotelRoomAdapter.notifyDataSetChanged()
        done = bottomSheetDialog.findViewById(R.id.done)!!
        plusAdult = bottomSheetDialog.findViewById(R.id.plus_adult)!!
        minusAdult = bottomSheetDialog.findViewById(R.id.minus_adult)!!
        recyclerView = bottomSheetDialog.findViewById(R.id.recyclerView)!!
        done.setOnClickListener {

        }
        recyclerView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = hotelRoomAdapter
        }
        tvAdult = bottomSheetDialog.findViewById(R.id.tv_adult)!!

        plusAdult.setOnClickListener(this)
        minusAdult.setOnClickListener(this)

        done.setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    override fun selected(city: String?, id: String) {
        hotelId = id
        hotelBindingModal.city = city
        if (!city!!.contains("Pakistan"))
            binding.departureCity.text = "${hotelBindingModal.city}, Pakistan"
        else
            binding.departureCity.text = hotelBindingModal.city
        departCity = city
        dialog.dismiss()
        datePickerDialog()
//        datePickerDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        datePickerDialog.show()
        //datePickerDialog.window!!.attributes = lp
        showDialog()
        datePickerDialog.window?.attributes = lp
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.exit -> {
                datePickerDialog.cancel()
            }
            R.id.clear -> {
                datePickerDialog.cancel()
            }
            R.id.done -> {
                bottomSheetDialog.cancel()
                rooms = tvAdult.text.toString().toInt()
                var total =
                    adults[0] + adults[1] + adults[2] + adults[3] + adults[4] + adults[5] + childList[0] + childList[1]
                +childList[2] + childList[3] + childList[4] + childList[5]

                if (total == 0) {
                    total = rooms
                }
                binding.travellersTv.text = "$total Person(s) , $rooms Room(s)"
                hotelBindingModal.stayers = "$total Person(s) , $rooms Room(s)"
                createLink()
            }
            R.id.apply -> {
                binding.checkInDate.text = "$finalMonthStart $finalDateStart, $finalYearStart"
                binding.checkOutDate.text = "$finalMonthEnd $finalDateEnd, $finalYearEnd"
                hotelBindingModal.checkinDate = "$finalMonthStart $finalDateStart, $finalYearStart"
                hotelBindingModal.checkOutDate = "$finalMonthEnd $finalDateEnd, $finalYearEnd"
                checkInDate = binding.checkInDate.text.toString()
                checkOutDate = binding.checkInDate.text.toString()
                datePickerDialog.cancel()
                showBottomSheetDialog()
                bottomSheetDialog.window!!.attributes.windowAnimations = R.style.BottomAnimation
                bottomSheetDialog.show()
            }
            R.id.cancel -> {
                datePickerDialog.cancel()
            }
            R.id.minus_adult -> {
                if (adult > 1) {
                    adult--
                    tvAdult.text = "$adult"
                    dummyList.removeAt(0)
                    hotelRoomAdapter.notifyDataSetChanged()
                }
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
            R.id.plus_adult -> {
                if (adult < 6) {
                    adult++
                    tvAdult.text = "$adult"
                    dummyList.add(1)
                    hotelRoomAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun createLink() {
        urlLink =
            "https://www.mosafir.pk/mobile/Hotels/listing?hotelDestination=$departCity&dest_code=&checkIn=$finalMonthStart+$finalDateStart%2C+$finalYearStart&checkOut=$finalMonthEnd+$finalDateEnd%2C+$finalYearEnd" +
                    "&rooms=$rooms&adults[]=${adults[0]}&childs[]=${childList[0]}&adults[]=${adults[1]}&childs[]=${childList[1]}&adults[]=${adults[2]}" +
                    "&childs[]=${childList[2]}&adults[]=${adults[3]}&childs[]=${childList[3]}&adults[]=${adults[4]}&childs[]=${childList[4]}&adults[]=${adults[5]}&childs[]=${childList[5]}"
    }
}