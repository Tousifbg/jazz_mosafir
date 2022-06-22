package pk.mosafir.travsol.ui.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import pk.mosafir.travsol.R
import pk.mosafir.travsol.databinding.FragmentUserInfoBinding
import pk.mosafir.travsol.viewmodel.UserInfoViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class UserInfoFragment : Fragment() {
    private lateinit var _binding: FragmentUserInfoBinding
    private val binding get() = _binding

    private val viewModel: UserInfoViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserInfoBinding.inflate(inflater, container, false)

        viewModel.getUserInfoData()
        viewModel.userInfoData.observe(viewLifecycleOwner, Observer {
            binding.userInfoBinding = it

            binding.userName.text = binding.userInfoBinding!!.full_name
        })

     return binding.root
    }
}