package pk.mosafir.travsol.ui.account

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import pk.mosafir.travsol.R
import pk.mosafir.travsol.databinding.FragmentLoggedInBinding
import pk.mosafir.travsol.ui.MainActivity
import pk.mosafir.travsol.ui.home.HomeFragment
import pk.mosafir.travsol.utils.loggedIn
import pk.mosafir.travsol.utils.loggedOutUser
import pk.mosafir.travsol.viewmodel.LoggedInViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import pk.mosafir.travsol.response.UserDetails
import pk.mosafir.travsol.utils.toast

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
        viewModel.userData.observe(viewLifecycleOwner, Observer{ details ->
            binding.loggedInBinding = details
            binding.userName.text = details.full_name.toString()

            binding.loggedInBinding!!.profile_image?.let {
                Glide.with(requireContext())
                    .load(it)
                    .into(binding.userImage)
                requireContext().toast("image exist")
                /*     if (details.auth_type.isNullOrBlank()){
                requireContext().toast("image not exist")
                Log.e("auth",details.auth_type.toString())
                binding.userName.text = details.full_name.toString()

            }else{
                binding.loggedInBinding!!.profile_image?.let {
                    Glide.with(requireContext())
                        .load(it)
                        .into(binding.userImage)
                    requireContext().toast("image exist")
                }
                Log.e("auth",details.auth_type.toString())
                binding.userName.text = details.full_name.toString()
            }*/
            }
        })

        //go to my profile fragment
        binding.myProfile.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(
                R.id.nav_host_fragment,
                UserInfoFragment()
            ).addToBackStack(null)
            transaction.commit()
        }

        //go back by button in toolbar
        binding.back.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(
                R.id.nav_host_fragment,
                HomeFragment()
            )
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

        //logout
        binding.logout.setOnClickListener {
            viewModel.deleteUserData()
            loggedIn = false
            loggedOutUser()
            requireContext().toast("Logged out successfully")
            MainActivity.logoutSocial()
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, HomeFragment(), "MY_FRAGMENT")
            transaction.commit()
        }
        return binding.root
    }

}