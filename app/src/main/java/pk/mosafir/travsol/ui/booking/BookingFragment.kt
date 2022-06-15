package pk.mosafir.travsol.ui.booking

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import pk.mosafir.travsol.databinding.FragmentBookingBinding
import pk.mosafir.travsol.ui.home.HomeFragment

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class BookingFragment : Fragment() {
    private lateinit var _binding: FragmentBookingBinding
    private val binding get() = _binding
    private val titles = arrayOf("Active", "Cancelled")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingBinding.inflate(inflater, container, false)
        binding.userImage.clipToOutline = true
        binding.viewPager.adapter = ViewPagerFragmentAdapter(requireActivity())
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
            when (position) {
                0 -> return ActiveBookingFragment()
                1 -> return CancelBookingFragment()
            }
            return HomeFragment()
        }

        override fun getItemCount(): Int {
            return 2
        }
    }
}