package pk.mosafir.travsol.ui.plan_tour

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.koin.androidx.viewmodel.ext.android.viewModel
import pk.mosafir.travsol.R
import pk.mosafir.travsol.R.drawable.*
import pk.mosafir.travsol.adapter.CityLocationListAdapter
import pk.mosafir.travsol.adapter.HotelListAdapter
import pk.mosafir.travsol.databinding.FragmentPlanTourBinding
import pk.mosafir.travsol.interfaces.CitySelector
import pk.mosafir.travsol.response.TourLocation
import pk.mosafir.travsol.ui.book_hotel.BookHotelFragment
import pk.mosafir.travsol.ui.book_hotel.BookHotelFragment.Companion.tourBindingModal
import pk.mosafir.travsol.ui.home.HomeFragment
import pk.mosafir.travsol.utils.*
import pk.mosafir.travsol.viewmodel.PlanTourViewModel
import pk.mosafir.travsol.webview.WebViewActivity
import java.util.*

class PlanTourFragment : Fragment(), CitySelector, View.OnClickListener {
    private lateinit var _binding: FragmentPlanTourBinding
    private val binding get() = _binding
    private lateinit var dialog: Dialog
    private lateinit var dateDialog: Dialog
    private val mOffersList = mutableListOf<TourLocation>()
    private val mOffersList2 = mutableListOf<TourLocation>()
    private var adult = 1
    private var year: Int = 2021
    private var selectedMonth = 0
    private var defaultYear: Int = 2021
    val lp = WindowManager.LayoutParams()

    private lateinit var jan: TextView
    private lateinit var feb: TextView
    private lateinit var march: TextView
    private lateinit var april: TextView
    private lateinit var may: TextView
    private lateinit var june: TextView
    private lateinit var july: TextView
    private lateinit var auguest: TextView
    private lateinit var september: TextView
    private lateinit var october: TextView
    private lateinit var november: TextView
    private lateinit var december: TextView

    private var children = 0
    private var infant = 0
    private lateinit var done: Button

    private lateinit var tvAdult: TextView
    private lateinit var tvChild: TextView
    private lateinit var tvInfant: TextView

    private lateinit var plusAdult: TextView
    private lateinit var plusChild: TextView
    private lateinit var plusInfant: TextView

    private lateinit var minusAdult: TextView
    private lateinit var minusChild: TextView
    private lateinit var minusInfant: TextView

    private lateinit var bottomSheetDialog: BottomSheetDialog
    private var cityAdapter = CityLocationListAdapter("",mOffersList, this)

    private val viewModel: PlanTourViewModel by viewModel()

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanTourBinding.inflate(inflater, container, false)
        binding.topToolBar.back.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, HomeFragment(), "MY_FRAGMENT")
            transaction.commit()
        }
        bottomSheetDialog = BottomSheetDialog(requireContext())
        dialog = Dialog(requireActivity(), R.style.full_screen_dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dateDialog = Dialog(requireActivity(), R.style.full_screen_dialog)
        dateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding.tourBinding = tourBindingModal
        viewModel.tourCityList.observe(viewLifecycleOwner, {
            with(mOffersList) {
                clear()
                it?.let { it1 -> addAll(it1) }
                cityAdapter.notifyDataSetChanged()
            }
        })
        lp.copyFrom(bottomSheetDialog.window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT

        binding.departureCity.setOnClickListener {
            showDialog()
            dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
            dialog.show()
            dialog.window!!.attributes = lp
        }
        binding.topToolBar.title.text = "Plan Tour"
        binding.checkIn.setOnClickListener {
            datePickerDialog()
            //dateDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
            dateDialog.show()
            dateDialog.window!!.attributes = lp

        }
        binding.travellers.setOnClickListener {
            showBottomSheetDialog()
            bottomSheetDialog.window!!.attributes.windowAnimations = R.style.BottomAnimation
            bottomSheetDialog.show()

        }
        binding.switcher.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.departureCity.isEnabled = false
                binding.arrivedCity.isEnabled = false
                binding.departureCity.text = "All Tours"
                binding.arrivedCity.text = "All Tours"
            } else {
                binding.departureCity.isEnabled = true
                binding.arrivedCity.isEnabled = true
                binding.departureCity.text = ""
                binding.arrivedCity.text = ""
            }
        }

        binding.search.setOnClickListener {
            when {
                binding.switcher.isChecked -> {
                    val url =
                        "https://mosafir.pk/Tours/listing?tourFacility=&adult=$adult&children=$children&infant=$infant&all_tours=all_tours"
                    val intent = Intent(requireContext(), WebViewActivity::class.java)
                    intent.putExtra("url", url)
                    requireActivity().startActivity(intent)
                }
                (binding.departureCity.text.toString()).isEmpty() -> {
                    binding.error.visible()
                    binding.error.text = "The Location or Category field is required"
                }
                (binding.arrivedCity.text.toString()).isEmpty() -> {
                    binding.error.visible()
                    binding.error.text = "The Tour Date field is required"
                }
                else -> {
                    val location = binding.departureCity.text.toString()
                    val url =
                        "https://mosafir.pk/mobile/Tours/listing?tourFacility=$location&tourDepDate=$selectedMonth%2F$year&adult=$adult&children=$children&infant=$infant"
                    val intent = Intent(requireContext(), WebViewActivity::class.java)
                    intent.putExtra("url", url)
                    requireActivity().startActivity(intent)

                }
            }
        }
        if (loggedIn) {
            binding.signIn.invisible()
        }



        binding.signIn.setOnClickListener(this)
        return binding.root
    }

    @SuppressLint(
        "NewApi", "SimpleDateFormat", "WeekBasedYear", "SetJavaScriptEnabled",
        "SetTextI18n", "ResourceAsColor", "UseCompatLoadingForDrawables",
        "UseCompatLoadingForColorStateLists"
    )
    @RequiresApi(Build.VERSION_CODES.N)
    private fun datePickerDialog() {
        dateDialog.setCancelable(false)
        dateDialog.setContentView(R.layout.cutom_dialog_monthpicker)
        val yearView = dateDialog.findViewById<TextView>(R.id.year)

        jan = dateDialog.findViewById(R.id.jan)
        feb = dateDialog.findViewById(R.id.feb)
        march = dateDialog.findViewById(R.id.march)
        april = dateDialog.findViewById(R.id.april)
        may = dateDialog.findViewById(R.id.may)
        june = dateDialog.findViewById(R.id.june)
        july = dateDialog.findViewById(R.id.july)
        auguest = dateDialog.findViewById(R.id.auguest)
        september = dateDialog.findViewById(R.id.september)
        october = dateDialog.findViewById(R.id.october)
        november = dateDialog.findViewById(R.id.november)
        december = dateDialog.findViewById(R.id.december)

        jan.setOnClickListener(this)
        feb.setOnClickListener(this)
        march.setOnClickListener(this)
        april.setOnClickListener(this)
        may.setOnClickListener(this)
        june.setOnClickListener(this)
        july.setOnClickListener(this)
        auguest.setOnClickListener(this)
        september.setOnClickListener(this)
        october.setOnClickListener(this)
        november.setOnClickListener(this)
        december.setOnClickListener(this)

        val previous = dateDialog.findViewById<ImageView>(R.id.previous)
        val next = dateDialog.findViewById<ImageView>(R.id.next)

        var month = Calendar.getInstance().get(Calendar.MONTH)
        when {
            month + 1 > 12 -> {
                month = 1
            }
        }
        specialize(month)
        previous.setOnClickListener {
            when {
                year > defaultYear -> {
                    year--
                    yearView.text = "Year $year"
                }
                else -> {
                    previous.background = ContextCompat.getDrawable(
                        requireContext(),
                        background_black_light_circle
                    )
                }
            }
            when (year) {
                defaultYear -> {
                    specialize(month)
                }
                else -> {
                    normalView()
                }
            }
        }
        next.setOnClickListener {
            year++
            yearView.text = "Year $year"
            previous.background = ContextCompat.getDrawable(
                requireContext(),
                background_black_circle
            )
            when (year) {
                defaultYear -> {
                    specialize(month)
                }
                else -> {
                    normalView()
                }
            }
        }
        year = Calendar.getInstance().get(Calendar.YEAR)
        defaultYear = year
        yearView.text = "Year $year"
        val cancel = dateDialog.findViewById<TextView>(R.id.cancel)
        cancel.setOnClickListener {
            dateDialog.dismiss()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun normalView() {
        jan.normalize(jan)
        feb.normalize(feb)
        march.normalize(march)
        april.normalize(april)
        may.normalize(may)
        june.normalize(june)
        july.normalize(july)
        auguest.normalize(auguest)
        september.normalize(september)
        october.normalize(october)
        november.normalize(november)
        december.normalize(december)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun specialize(month: Int) {
        when (year) {
            defaultYear -> {
                when (month + 1) {
                    1 -> {
                        jan.special(jan)
                    }
                    2 -> {
                        jan.isClickable = false
                        jan.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        feb.special(feb)
                    }
                    3 -> {
                        jan.isClickable = false
                        jan.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        feb.isClickable = false
                        feb.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        march.special(march)
                    }
                    4 -> {
                        jan.isClickable = false
                        jan.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        feb.isClickable = false
                        feb.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        march.isClickable = false
                        march.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        april.special(april)
                    }
                    5 -> {
                        jan.isClickable = false
                        jan.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        feb.isClickable = false
                        feb.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        march.isClickable = false
                        march.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        april.isClickable = false
                        april.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        may.special(may)
                    }
                    6 -> {
                        jan.isClickable = false
                        jan.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        feb.isClickable = false
                        feb.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        march.isClickable = false
                        march.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        april.isClickable = false
                        april.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        may.isClickable = false
                        may.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )

                        june.special(june)
                    }
                    7 -> {
                        jan.isClickable = false
                        jan.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        feb.isClickable = false
                        feb.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        march.isClickable = false
                        march.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        april.isClickable = false
                        april.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        may.isClickable = false
                        may.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        june.isClickable = false
                        june.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        july.special(july)
                    }
                    8 -> {
                        jan.isClickable = false
                        jan.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        feb.isClickable = false
                        feb.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        march.isClickable = false
                        march.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        april.isClickable = false
                        april.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        may.isClickable = false
                        may.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        june.isClickable = false
                        june.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        july.isClickable = false
                        july.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )

                        auguest.special(auguest)
                    }
                    9 -> {
                        jan.isClickable = false
                        jan.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        feb.isClickable = false
                        feb.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        march.isClickable = false
                        march.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        april.isClickable = false
                        april.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        may.isClickable = false
                        may.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        june.isClickable = false
                        june.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        july.isClickable = false
                        july.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        auguest.isClickable = false
                        auguest.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        september.special(september)
                    }
                    10 -> {
                        jan.isClickable = false
                        jan.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        feb.isClickable = false
                        feb.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        march.isClickable = false
                        march.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        april.isClickable = false
                        april.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        may.isClickable = false
                        may.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        june.isClickable = false
                        june.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        july.isClickable = false
                        july.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        auguest.isClickable = false
                        auguest.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        september.isClickable = false
                        september.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        october.special(october)
                    }
                    11 -> {
                        jan.isClickable = false
                        jan.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        feb.isClickable = false
                        feb.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        march.isClickable = false
                        march.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        april.isClickable = false
                        april.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        may.isClickable = false
                        may.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        june.isClickable = false
                        june.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        july.isClickable = false
                        july.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        auguest.isClickable = false
                        auguest.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        september.isClickable = false
                        september.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        october.isClickable = false
                        october.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        november.special(november)
                    }
                    12 -> {
                        jan.isClickable = false
                        jan.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        feb.isClickable = false
                        feb.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        march.isClickable = false
                        march.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        april.isClickable = false
                        april.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        may.isClickable = false
                        may.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        june.isClickable = false
                        june.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        july.isClickable = false
                        july.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        auguest.isClickable = false
                        auguest.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        september.isClickable = false
                        september.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        october.isClickable = false
                        october.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        november.isClickable = false
                        november.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorGrayDark
                            )
                        )
                        december.special(december)
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showDialog() {

        dialog.setCancelable(false)
        dialog.setContentView(R.layout.cutom_dialog_location)
        val recyclerView = dialog.findViewById<RecyclerView>(R.id.recyclerView)
        val location = dialog.findViewById<EditText>(R.id.location)
        tourBindingModal.city.let {
            location.setText(it)
        }
        //location.setText(tourBindingModal.city)
        if (tourBindingModal.city!!.isEmpty()) {
            viewModel.fetchOffers()
            viewModel.tourCityList.observe(viewLifecycleOwner, {
                with(mOffersList) {
                    clear()
                    it?.let { it1 -> addAll(it1) }
                    cityAdapter.notifyDataSetChanged()
                }
            })
            cityAdapter = CityLocationListAdapter(
                "",
                mOffersList,
                this@PlanTourFragment
            )
        } else {
            viewModel.getTourLocation(tourBindingModal.city!!)
            cityAdapter = CityLocationListAdapter(
                location.text.toString().lowercase(Locale.getDefault()),
                mOffersList2,
                this@PlanTourFragment
            )
            viewModel.tourSearchCityList.observe(viewLifecycleOwner, {
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
                        cityAdapter = CityLocationListAdapter("", mOffersList, this@PlanTourFragment)
                        recyclerView.adapter = cityAdapter
                    }
                    else -> {
                        mOffersList2.clear()
                        cityAdapter.notifyDataSetChanged()
                        cityAdapter = CityLocationListAdapter(
                            s.toString().lowercase(Locale.getDefault()),
                            mOffersList2,
                            this@PlanTourFragment
                        )
                        recyclerView.adapter = cityAdapter
                        viewModel.getTourLocation("%${s}%")
                        viewModel.tourSearchCityList.observe(viewLifecycleOwner, {
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

    private fun showBottomSheetDialog() {

        bottomSheetDialog.setContentView(R.layout.bottom_sheet_tour_plan)

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
        checkDullType()
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun selected(city: String?, id:String) {
        tourBindingModal.city = city!!
        binding.departureCity.text = city
        dialog.dismiss()
        showDialog()
        datePickerDialog()
//        dateDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dateDialog.show()
        dateDialog.window!!.attributes = lp
    }

    fun proceed(){
        binding.arrivedCity.text = "$selectedMonth/$year"
        dateDialog.dismiss()
        tourBindingModal.checkinDate = "$selectedMonth/$year"
        showBottomSheetDialog()
        bottomSheetDialog.window!!.attributes.windowAnimations = R.style.BottomAnimation
        bottomSheetDialog.show()
    }

    @SuppressLint("SetTextI18n")
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.jan -> {
                selectedMonth = 1
                proceed()
            }
            R.id.feb -> {
                selectedMonth = 2
                proceed()
            }
            R.id.march -> {
                selectedMonth = 3
                proceed()
            }
            R.id.april -> {
                selectedMonth = 4
                proceed()
            }
            R.id.may -> {
                selectedMonth = 5
                proceed()
            }
            R.id.june -> {
                selectedMonth = 6
                proceed()
            }
            R.id.july -> {
                selectedMonth = 7
                proceed()
            }
            R.id.auguest -> {
                selectedMonth = 8
                proceed()
            }
            R.id.september -> {
                selectedMonth = 9
                proceed()
            }
            R.id.october -> {
                selectedMonth = 10
                proceed()
            }
            R.id.november -> {
                selectedMonth = 11
                proceed()
            }
            R.id.december -> {
                selectedMonth = 12
                proceed()
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
            R.id.done -> {
                binding.travelersDetail.text = "${adult + children + infant} Traveller(s)"
                tourBindingModal.stayers = "${adult + children + infant} Traveller(s)"
                bottomSheetDialog.dismiss()
            }
            R.id.minus_adult -> {
                if (adult > 1) {
                    adult--
                    tvAdult.text = "$adult"
                }
                checkDullType()
            }
            R.id.minus_child -> {
                if (children > 0) {
                    children--
                    tvChild.text = "$children"
                }
                checkDullType()
            }

            R.id.minus_infant -> {
                if (infant > 0) {
                    infant--
                    tvInfant.text = "$infant"
                }
                checkDullType()
            }
            R.id.plus_adult -> {
                if (adult < 10) {
                    adult++
                    tvAdult.text = "$adult"
                }
                checkDullType()
            }
            R.id.plus_child -> {
                if (children < 10) {
                    children++
                    tvChild.text = "$children"
                }
                checkDullType()
            }
            R.id.plus_infant -> {
                if (infant < 1) {
                    infant++
                    tvInfant.text = "$infant"
                }
                checkDullType()
            }
        }
    }

    private fun checkDullType() {
        //adult
        when (adult) {
            10 -> {
                minusAdult.background =
                    ContextCompat.getDrawable(requireContext(), sqaure_red_round_corner)
                plusAdult.background =
                    ContextCompat.getDrawable(requireContext(), sqaure_red_round_corner_dull)
            }
            1 -> {
                minusAdult.background =
                    ContextCompat.getDrawable(requireContext(), sqaure_red_round_corner_dull)
                plusAdult.background =
                    ContextCompat.getDrawable(requireContext(), sqaure_red_round_corner)
            }
            else -> {
                plusAdult.background =
                    ContextCompat.getDrawable(requireContext(), sqaure_red_round_corner)
                minusAdult.background =
                    ContextCompat.getDrawable(requireContext(), sqaure_red_round_corner)
            }
        }
        //children
        when (children) {
            10 -> {
                minusChild.background =
                    ContextCompat.getDrawable(requireContext(), sqaure_red_round_corner)
                plusChild.background =
                    ContextCompat.getDrawable(requireContext(), sqaure_red_round_corner_dull)
            }
            0 -> {
                minusChild.background =
                    ContextCompat.getDrawable(requireContext(), sqaure_red_round_corner_dull)
                plusChild.background =
                    ContextCompat.getDrawable(requireContext(), sqaure_red_round_corner)
            }
            else -> {
                plusChild.background =
                    ContextCompat.getDrawable(requireContext(), sqaure_red_round_corner)
                minusChild.background =
                    ContextCompat.getDrawable(requireContext(), sqaure_red_round_corner)
            }
        }
        //infant
        when (infant) {
            1 -> {
                minusInfant.background =
                    ContextCompat.getDrawable(requireContext(), sqaure_red_round_corner)
                plusInfant.background =
                    ContextCompat.getDrawable(requireContext(), sqaure_red_round_corner_dull)
            }
            0 -> {
                minusInfant.background =
                    ContextCompat.getDrawable(requireContext(), sqaure_red_round_corner_dull)
                plusInfant.background =
                    ContextCompat.getDrawable(requireContext(), sqaure_red_round_corner)
            }
            else -> {
                plusInfant.background =
                    ContextCompat.getDrawable(requireContext(), sqaure_red_round_corner)
                minusInfant.background =
                    ContextCompat.getDrawable(requireContext(), sqaure_red_round_corner)
            }
        }
    }
}