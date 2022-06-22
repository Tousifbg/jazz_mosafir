package pk.mosafir.travsol.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pk.mosafir.travsol.dao.TourCityDao
import pk.mosafir.travsol.data.Repository
import pk.mosafir.travsol.response.DiscoverPakistanCity
import pk.mosafir.travsol.response.Offer
import pk.mosafir.travsol.response.Response

class OffersViewModel(private val repository: Repository, private val tourCityDao: TourCityDao) : BaseViewModel() {
    private val _hotelCityList = MutableLiveData<List<DiscoverPakistanCity>?>()

    val hotelCityList: MutableLiveData<List<DiscoverPakistanCity>?>
        get() = _hotelCityList

    private val _searchCityList = MutableLiveData<List<DiscoverPakistanCity>?>()

    val searchCityList: MutableLiveData<List<DiscoverPakistanCity>?>
        get() = _searchCityList


    private val _offers = MutableLiveData<List<Offer>?>()

    val offers: MutableLiveData<List<Offer>?>
        get() = _offers

    init {
        isLoading.value = true
        fetchOffers()
        fetchCities()
    }

    fun getSelectedLocation(name: String) {
        viewModelScope.launch {
            searchCityList.value = tourCityDao.getCityLike(name)
        }
    }
    fun putRecentLocation(id: String) {
        viewModelScope.launch {
            repository.putRecentCity(id)
        }
    }


    fun putData() {
        viewModelScope.launch {
            repository.putData()
        }
    }

    private fun fetchOffers() {
        viewModelScope.launch {
            when (val response = repository.getOffersList()) {
                is Response.Success -> _offers.value = response.data
                is Response.Error -> error.value = response.message
            }
            isLoading.value = false
        }
    }
    private fun fetchCities() {
        viewModelScope.launch {
            when (val response = repository.getCitiesRecent()) {
                is Response.Success -> _hotelCityList.value = response.data
                is Response.Error -> error.value = response.message
            }
            isLoading.value = false
        }
    }

}