package pk.mosafir.travsol.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.viewmodel.ext.android.viewModel
import pk.mosafir.travsol.R
import pk.mosafir.travsol.databinding.FragmentLoggedInBinding
import pk.mosafir.travsol.ui.MainActivity.Companion.logoutSocial
import pk.mosafir.travsol.ui.MainActivity.Companion.runningFragment
import pk.mosafir.travsol.ui.home.HomeFragment
import pk.mosafir.travsol.utils.loggedIn
import pk.mosafir.travsol.utils.loggedOutUser
import pk.mosafir.travsol.utils.toast
import pk.mosafir.travsol.viewmodel.LoggedInViewModel

class LoggedInFragment : Fragment() {
    private lateinit var _binding: FragmentLoggedInBinding
    private val binding get() = _binding
    private var mAuth: FirebaseAuth? = null
    private val viewModel: LoggedInViewModel by viewModel()

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
        viewModel.getUserData()
        viewModel.userData.observe(viewLifecycleOwner, { details ->
            binding.loggedInBinding = details
            binding.loggedInBinding!!.profile_image?.let {
                Glide.with(requireContext())
                    .load(it)
                    .into(binding.userImage)
            }
        })

        binding.myProfile.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(
                R.id.nav_host_fragment,
                UserInfoFragment()
            )
            transaction.commit()
            runningFragment = "loggedin"
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
            viewModel.deleteUserData()
            loggedIn = false
            loggedOutUser()
            logoutSocial()
            requireContext().toast("Logged out successfully")

            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, HomeFragment(), "MY_FRAGMENT")
            transaction.commit()
        }
        return binding.root
    }
}
