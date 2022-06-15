package pk.mosafir.travsol.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import pk.mosafir.travsol.data.Repository
import kotlinx.coroutines.launch
import pk.mosafir.travsol.dao.TourCityDao
import pk.mosafir.travsol.dao.TourLocationDao
import pk.mosafir.travsol.response.*

class PlanTourViewModel(private val repository: Repository, private val tourCityDao: TourLocationDao) : BaseViewModel() {
    private val _tourCityList = MutableLiveData<List<TourLocation>?>()

    val tourCityList: LiveData<List<TourLocation>?>
        get() = _tourCityList

    private val _tourSearchCityList = MutableLiveData<List<TourLocation>?>()

    val tourSearchCityList: MutableLiveData<List<TourLocation>?>
        get() = _tourSearchCityList
    init {
        isLoading.value = true
        fetchOffers()
    }

    fun fetchOffers() {
        viewModelScope.launch {
            when (val response = repository.getRecentTripLocation()) {
                is Response.Success -> _tourCityList.value = response.data
                is Response.Error -> error.value = response.message
            }
            isLoading.value = false
        }
    }
    fun getTourLocation(string: String){
        viewModelScope.launch {
            tourSearchCityList.value = tourCityDao.getTourLocationString(string)
        }
    }
}