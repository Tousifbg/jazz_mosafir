package pk.mosafir.travsol.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import pk.mosafir.travsol.data.Repository
import pk.mosafir.travsol.response.Response
import kotlinx.coroutines.launch
import pk.mosafir.travsol.dao.HotelLocationDao
import pk.mosafir.travsol.response.HotelLocation

class BookHotelViewModel(private val repository: Repository,private val hotelLocationDao: HotelLocationDao) : BaseViewModel() {
    private val _hotelCityList = MutableLiveData<List<HotelLocation>?>()

    val hotelCityList: LiveData<List<HotelLocation>?>
        get() = _hotelCityList

    private val _hotelCitySearch = MutableLiveData<List<HotelLocation>?>()

    val hotelCitySearch: MutableLiveData<List<HotelLocation>?>
        get() = _hotelCitySearch
    init {
        isLoading.value = true
//        fetchOffers()
    }

    fun putRecentSearches(id: String) {
        viewModelScope.launch {
            repository.putRecentHotel(id)
        }
    }
    fun getSelectedHotels(string: String){
        viewModelScope.launch {
            hotelCitySearch.value = hotelLocationDao.getHotelLocationString(string)
        }
    }
    fun fetchOffers(string: String) {
        viewModelScope.launch {
            when (val response = repository.getHotelCities(string)) {
                is Response.Success -> _hotelCityList.value = response.data
                is Response.Error -> error.value = response.message
            }
            isLoading.value = false
        }
    }
}