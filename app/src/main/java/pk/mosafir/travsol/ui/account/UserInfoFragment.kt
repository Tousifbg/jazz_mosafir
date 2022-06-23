package pk.mosafir.travsol.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import pk.mosafir.travsol.R
import pk.mosafir.travsol.databinding.FragmentUserInfoBinding
import pk.mosafir.travsol.ui.MainActivity
import pk.mosafir.travsol.viewmodel.UserInfoViewModel


class UserInfoFragment : Fragment() {
    private lateinit var _binding: FragmentUserInfoBinding
    private val binding get() = _binding

    private val viewModel: UserInfoViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserInfoBinding.inflate(inflater, container, false)

        viewModel.getUserInfoData()
        viewModel.userInfoData.observe(viewLifecycleOwner, {
            binding.userInfoBinding = it

            binding.userName.text = binding.userInfoBinding!!.full_name
        })
        binding.back.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, LoggedInFragment(), "MY_FRAGMENT")
            transaction.commit()
            MainActivity.runningFragment = ""
        }
     return binding.root
    }
}