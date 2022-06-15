package pk.mosafir.travsol.ui.base

import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import pk.mosafir.travsol.viewmodel.FlightViewModel

open class FlightBaseFragment : Fragment() {
    val viewModel: FlightViewModel by viewModel()
}