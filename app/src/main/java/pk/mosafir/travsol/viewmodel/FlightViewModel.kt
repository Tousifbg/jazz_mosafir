package pk.mosafir.travsol.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import pk.mosafir.travsol.data.Repository
import pk.mosafir.travsol.response.Response
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import pk.mosafir.travsol.adapter.AirportListAdapter
import pk.mosafir.travsol.dao.AirportDao
import pk.mosafir.travsol.dao.TourCityDao
import pk.mosafir.travsol.model.RecentAirportModal
import pk.mosafir.travsol.response.FlyFrom10
import pk.mosafir.travsol.response.GeneralFlightResponse
import pk.mosafir.travsol.utils.loadJSONFromAssets
import kotlin.properties.ReadOnlyProperty

class FlightViewModel(
    private val repository: Repository,
    @SuppressLint("StaticFieldLeak") private val context: Context,
    private val airportDao: AirportDao
) : BaseViewModel() {
    private val _airportList = MutableLiveData<List<GeneralFlightResponse>?>()

    val airportList: LiveData<List<GeneralFlightResponse>?>
        get() = _airportList

    private val _searchAirportList = MutableLiveData<List<GeneralFlightResponse>?>()

    val searchAirportList: MutableLiveData<List<GeneralFlightResponse>?>
        get() = _searchAirportList

    private val _searchAirport = MutableLiveData<String?>()

    val searchAirport: MutableLiveData<String?>
        get() = _searchAirport

    private val _searchAirportArrive = MutableLiveData<String?>()

    val searchAirportArrive: MutableLiveData<String?>
        get() = _searchAirportArrive

    init {
        isLoading.value = true
//        fetchOffers()
    }

    fun getSelectedAirport(string: String) {
        viewModelScope.launch {
            searchAirportList.value = airportDao.getAirportLike(string)
        }
    }

    fun putAirportRecent(model: RecentAirportModal) {
        viewModelScope.launch {
            repository.putRecentAirport(model)
        }
    }

    fun fetchOffers(flag: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                when (val response = repository.getAirport(
                    JSONArray(context.loadJSONFromAssets("airports.json")),
                    0
                )) {

                    is Response.Success -> _airportList.value = response.data
                    is Response.Error -> error.value = response.message
                }
                isLoading.value = false
            }
        }
    }

    fun fetchOffersArrival(flag: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                when (val response = repository.getAirport(
                    JSONArray(context.loadJSONFromAssets("airports.json")),
                    1
                )) {

                    is Response.Success -> _airportList.value = response.data
                    is Response.Error -> error.value = response.message
                }
                isLoading.value = false
            }
        }
    }

}