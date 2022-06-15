package pk.mosafir.travsol.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {
    val isLoading = MutableLiveData<Boolean>()
    val error = MutableLiveData<String>()
}
