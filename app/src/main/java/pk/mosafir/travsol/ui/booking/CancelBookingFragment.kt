package pk.mosafir.travsol.ui.booking

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import pk.mosafir.travsol.adapter.CancelBookingAdapter
import pk.mosafir.travsol.databinding.FragmentCancelBookingBinding
import pk.mosafir.travsol.model.CancelBookingModal
@SuppressLint("NotifyDataSetChanged")
class CancelBookingFragment : Fragment() {
    private lateinit var _binding: FragmentCancelBookingBinding
    private val binding get() = _binding
    private var cancelList = mutableListOf<CancelBookingModal>()
    private var adapterCancel = CancelBookingAdapter(cancelList)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCancelBookingBinding.inflate(inflater, container, false)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = adapterCancel
        }
        getCancelBookings()
        return binding.root
    }

    private fun getCancelBookings() {
        cancelList.add(
            CancelBookingModal(
                "aqwsde",
                "Tour"
            )
        )
        cancelList.add(
            CancelBookingModal(
                "gftryh",
                "Tour"
            )
        )
        cancelList.add(
            CancelBookingModal(
                "juhtyb",
                "Tour"
            )
        )
        cancelList.add(
            CancelBookingModal(
                "aqwsde",
                "Tour"
            )
        )
        cancelList.add(
            CancelBookingModal(
                "gftryh",
                "Tour"
            )
        )
        cancelList.add(
            CancelBookingModal(
                "juhtyb",
                "Tour"
            )
        )
        cancelList.add(
            CancelBookingModal(
                "aqwsde",
                "Tour"
            )
        )
        cancelList.add(
            CancelBookingModal(
                "gftryh",
                "Tour"
            )
        )
        cancelList.add(
            CancelBookingModal(
                "juhtyb",
                "Tour"
            )
        )
        adapterCancel.notifyDataSetChanged()
    }
}