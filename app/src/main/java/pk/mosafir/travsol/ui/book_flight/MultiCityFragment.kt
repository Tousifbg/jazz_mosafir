package pk.mosafir.travsol.ui.book_flight

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import pk.mosafir.travsol.R
import pk.mosafir.travsol.adapter.MultiCityAdapter
import pk.mosafir.travsol.databinding.FragmentMultiCityBinding
import pk.mosafir.travsol.model.RecentAirportModal
import pk.mosafir.travsol.ui.base.FlightBaseFragment
import pk.mosafir.travsol.ui.book_flight.BookFlightFragment.Companion.mOffersList
import pk.mosafir.travsol.utils.*
import pk.mosafir.travsol.webview.WebViewActivity

class MultiCityFragment : FlightBaseFragment(), View.OnClickListener {
    //<editor-fold desc="variables">
    private lateinit var _binding: FragmentMultiCityBinding
    private val binding get() = _binding

    private var cabinClass = ""

    private lateinit var bottomSheetDialog: BottomSheetDialog
    private val offersList = mutableListOf<Int>()
    private lateinit var multiCityAdapter: MultiCityAdapter
    private var adult = 1
    private var children = 0
    private var infant = 0
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

    private lateinit var done: Button
    private lateinit var radioGroup: RadioGroup
    //</editor-fold>

    //<editor-fold desc="OnCreateView">
    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMultiCityBinding.inflate(inflater, container, false)
        offersList.add(1)
        offersList.add(1)
        multiCityAdapter = MultiCityAdapter(viewModel, offersList, viewLifecycleOwner, mOffersList)
        binding.multyFlight.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = multiCityAdapter
        }

        bottomSheetDialog = BottomSheetDialog(requireContext())
        showBottomSheetDialog()
        binding.persons.setOnClickListener {
            bottomSheetDialog.window!!.attributes.windowAnimations = R.style.BottomAnimation
            bottomSheetDialog.show()
        }

        if (loggedIn) {
            binding.signIn.invisible()
        }
        binding.search.setOnClickListener {
            var error = false
            when (destinationCount) {
                1 -> {
                    when {
                        airportArrived[0].isEmpty() -> {
                            binding.error.visible()
                            binding.error.text = "The Location field is required"
                            error = true
                        }
                        airportDepart[0].isEmpty() -> {
                            binding.error.visible()
                            binding.error.text = "The Location field is required"
                            error = true
                        }
                        dateTravel[0].isEmpty() -> {
                            binding.error.visible()
                            binding.error.text = "The Date field is required"
                            error = true
                        }
                    }
                }
                2 -> {
                    when {
                        airportArrived[0].isEmpty() || airportArrived[1].isEmpty() -> {
                            binding.error.visible()
                            binding.error.text = "The Location field is required"
                            error = true
                        }
                        airportDepart[0].isEmpty() || airportDepart[1].isEmpty() -> {
                            binding.error.visible()
                            binding.error.text = "The Location field is required"
                            error = true
                        }
                        dateTravel[0].isEmpty() || dateTravel[1].isEmpty() -> {
                            binding.error.visible()
                            binding.error.text = "The Date field is required"
                            error = true
                        }
                    }
                }
                3 -> {
                    when {
                        airportArrived[0].isEmpty() || airportArrived[1].isEmpty() || airportArrived[2].isEmpty() -> {
                            binding.error.visible()
                            binding.error.text = "The Location field is required"
                            error = true
                        }
                        airportDepart[0].isEmpty() || airportDepart[1].isEmpty() || airportDepart[2].isEmpty() -> {
                            binding.error.visible()
                            binding.error.text = "The Location field is required"
                            error = true
                        }
                        dateTravel[0].isEmpty() || dateTravel[1].isEmpty() || dateTravel[2].isEmpty() -> {
                            binding.error.visible()
                            binding.error.text = "The Date field is required"
                            error = true
                        }
                    }
                }
                4 -> {
                    when {
                        airportArrived[0].isEmpty() || airportArrived[1].isEmpty() || airportArrived[2].isEmpty() || airportArrived[3].isEmpty() -> {
                            binding.error.visible()
                            binding.error.text = "The Location field is required"
                            error = true
                        }
                        airportDepart[0].isEmpty() || airportDepart[1].isEmpty() || airportDepart[2].isEmpty() || airportDepart[3].isEmpty() -> {
                            binding.error.visible()
                            binding.error.text = "The Location field is required"
                            error = true
                        }
                        dateTravel[0].isEmpty() || dateTravel[1].isEmpty() || dateTravel[2].isEmpty() || dateTravel[3].isEmpty() -> {
                            binding.error.visible()
                            binding.error.text = "The Date field is required"
                            error = true
                        }
                    }
                }
                5 -> {
                    when {
                        airportArrived[0].isEmpty() || airportArrived[1].isEmpty() || airportArrived[2].isEmpty() || airportArrived[3].isEmpty() || airportArrived[4].isEmpty() -> {
                            binding.error.visible()
                            binding.error.text = "The Location field is required"
                            error = true
                        }
                        airportDepart[0].isEmpty() || airportDepart[1].isEmpty() || airportDepart[2].isEmpty() || airportDepart[3].isEmpty() || airportDepart[4].isEmpty() -> {
                            binding.error.visible()
                            binding.error.text = "The Location field is required"
                            error = true
                        }
                        dateTravel[0].isEmpty() || dateTravel[1].isEmpty() || dateTravel[2].isEmpty() || dateTravel[3].isEmpty() || dateTravel[4].isEmpty() -> {
                            binding.error.visible()
                            binding.error.text = "The Date field is required"
                            error = true
                        }
                    }
                }
                6 -> {
                    when {
                        airportArrived[0].isEmpty() || airportArrived[1].isEmpty() || airportArrived[2].isEmpty() || airportArrived[3].isEmpty() || airportArrived[4].isEmpty() || airportArrived[5].isEmpty() -> {
                            binding.error.visible()
                            binding.error.text = "The Location field is required"
                            error = true
                        }
                        airportDepart[0].isEmpty() || airportDepart[1].isEmpty() || airportDepart[2].isEmpty() || airportDepart[3].isEmpty() || airportDepart[4].isEmpty() || airportDepart[5].isEmpty() -> {
                            binding.error.visible()
                            binding.error.text = "The Location field is required"
                            error = true
                        }
                        dateTravel[0].isEmpty() || dateTravel[1].isEmpty() || dateTravel[2].isEmpty() || dateTravel[3].isEmpty() || dateTravel[4].isEmpty() || dateTravel[5].isEmpty() -> {
                            binding.error.visible()
                            binding.error.text = "The Date field is required"
                            error = true
                        }
                    }
                }
            }
            if (!error) {
                //region loop to put recent searched airport
                for (i in 0 until destinationCount) {
                    viewModel.putAirportRecent(
                        RecentAirportModal(
                            getTempKey(),
                            "" + getUserId(),
                            "oneWay",
                            "${airportDepartCode[i]} ,${airportDepart[i]}",
                            "${airportArrivedCode[i]} ,${airportArrived[i]}",
                            dateTravelForApi[i],
                            "",
                            "$adult",
                            "$children",
                            "$infant",
                            cabinClass
                        )
                    )
                }
                //endregion
                binding.error.invisible()
                val intent = Intent(requireContext(), WebViewActivity::class.java)
                intent.putExtra("url", getUrl())
                requireActivity().startActivity(intent)
            }
        }
        binding.signIn.setOnClickListener(this)

        binding.lessFlight.setOnClickListener(this)
        binding.plusFlight.setOnClickListener(this)
        return binding.root
    }
    //</editor-fold>

    //<editor-fold desc="onCLick listener">
    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.plusFlight -> {
                val size = offersList.size
                if (size < 6) {
                    offersList.add(1)
                    multiCityAdapter.notifyDataSetChanged()
                    binding.multyFlight.smoothScrollToPosition(offersList.size - 1)
                }
            }
            R.id.signIn -> {
                val url: String = when {
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
            R.id.lessFlight -> {
                val size = offersList.size
                if (size > 2) {
                    offersList.removeAt(size - 1)
                    multiCityAdapter.notifyDataSetChanged()
                    binding.multyFlight.smoothScrollToPosition(offersList.size - 1)
                }
            }
            R.id.done -> {
                binding.travellersTv.text = "${adult + children + infant} Traveller(s)/"
                bottomSheetDialog.dismiss()
                if (economy) {
                    binding.travellersTv.text =
                        "${adult + children + infant} Traveller(s)/Economy"
                } else if (business) {
                    binding.travellersTv.text =
                        "${adult + children + infant} Traveller(s)/Business"
                }
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
    //</editor-fold>

    //<editor-fold desc="bottom sheet dialog">
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
    //</editor-fold>

    //<editor-fold desc="Url generator">
    private fun getUrl(): String {
        var url = "https://mosafir.pk/mobile/Flights/listing?multiple_destination=1" +
                "&destination_counter=$destinationCount" +
                "&adult=$adult&children=$children&infant=$infant" +
                "&origin=${airportDepartCode[0]}%2C+${airportDepart[0]}" +
                "&destination=${airportArrivedCode[0]}%2C+${airportArrived[0]}" +
                "&preferredDepDate=${dateTravel[0]}"
        for (i in 1 until destinationCount) {
            url =
                "$url&flyFrom_1=&multiDestFlyFrom%5B%5D=${airportDepartCode[i]}%2C+${airportDepart[i]}" +
                        "&flyTo_1=&multiDestFlyTo%5B%5D=${airportArrivedCode[i]}%2C+${airportArrived[i]}" +
                        "&multiDestDptDate%5B%5D=${dateTravel[i]}"
        }
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
    //</editor-fold>

    //<editor-fold desc="companion objects">
    companion object {
        var destinationCount = 0
        var airportDepart = arrayListOf("", "", "", "", "", "")
        var airportArrived = arrayListOf("", "", "", "", "", "")
        var airportDepartCode = arrayListOf("", "", "", "", "", "")
        var airportArrivedCode = arrayListOf("", "", "", "", "", "")
        var dateTravel = arrayListOf("", "", "", "", "", "")
        var dateTravelForApi = arrayListOf("", "", "", "", "", "")
    }
    //</editor-fold>
}