package pk.mosafir.travsol.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import pk.mosafir.travsol.R
import pk.mosafir.travsol.databinding.FragmentLoggedInBinding
import pk.mosafir.travsol.ui.MainActivity
import pk.mosafir.travsol.ui.home.HomeFragment
import pk.mosafir.travsol.utils.loggedIn
import pk.mosafir.travsol.utils.loggedOutUser

class LoggedInFragment : Fragment() {
    private lateinit var _binding: FragmentLoggedInBinding
    private val binding get() = _binding
    private var mAuth: FirebaseAuth? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoggedInBinding.inflate(inflater, container, false)
        binding.back.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, HomeFragment(), "MY_FRAGMENT")
            transaction.commit()
        }
        mAuth = FirebaseAuth.getInstance()
        binding.userProfile.setOnClickListener {

        }
        binding.hotelBookings.setOnClickListener {

        }
        binding.tourBookings.setOnClickListener {

        }
        binding.flightBookings.setOnClickListener {

        }
        binding.logout.setOnClickListener {
            loggedIn = false
            loggedOutUser()
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, HomeFragment(), "MY_FRAGMENT")
            try {
                mAuth!!.signOut()
            } catch (e: Exception) {
            }
            transaction.commit()
        }
        return binding.root
    }
}