package pk.mosafir.travsol.ui.base

import android.widget.TextView
import androidx.fragment.app.Fragment
import pk.mosafir.travsol.R

abstract class BaseFragment : Fragment() {
    protected val tvTitle: TextView? by lazy {
        activity?.findViewById(R.id.tvTitle)
    }
    protected abstract fun setTitle(title: String)
}