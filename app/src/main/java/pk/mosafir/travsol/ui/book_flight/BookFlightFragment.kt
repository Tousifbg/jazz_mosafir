package pk.mosafir.travsol.ui.book_flight

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel
import pk.mosafir.travsol.R
import pk.mosafir.travsol.databinding.FragmentBookFlightBinding
import pk.mosafir.travsol.response.GeneralFlightResponse
import pk.mosafir.travsol.ui.home.HomeFragment
import pk.mosafir.travsol.viewmodel.FlightViewModel

class BookFlightFragment : Fragment() {
    private lateinit var _binding: FragmentBookFlightBinding
    private val binding get() = _binding
    private val titles = arrayOf("Round Trip", "One Way", "Multi City")
    public val viewModel: FlightViewModel by viewModel()

    companion object {
        val mOffersListArrive = mutableListOf<GeneralFlightResponse>()
        val mOffersList = mutableListOf<GeneralFlightResponse>()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookFlightBinding.inflate(inflater, container, false)
        binding.viewPager.adapter = ViewPagerFragmentAdapter(requireActivity())
        binding.topToolBar.back.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            transaction.replace(R.id.nav_host_fragment, HomeFragment(), "MY_FRAGMENT")
            transaction.commit()
        }
//        viewModel.airportList.observe(viewLifecycleOwner, {
//            with(mOffersList) {
//                clear()
//                it?.let { it1 -> addAll(it1) }
//            }
//        })

        binding.topToolBar.title.text = "Flight Search"
        TabLayoutMediator(
            binding.tabLayout, binding.viewPager
        ) { tab: TabLayout.Tab, position: Int ->
            tab.text = titles[position]
        }.attach()
        return binding.root
    }

    private class ViewPagerFragmentAdapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> RoundTripFragment()
                1 -> OneWayFragment()
                2 -> MultiCityFragment()
                else -> HomeFragment()
            }
        }

        override fun getItemCount(): Int {
            return 3
        }
    }
}