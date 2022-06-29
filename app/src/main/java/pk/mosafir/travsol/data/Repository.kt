package pk.mosafir.travsol.data

import android.util.Log
import org.json.JSONArray
import pk.mosafir.travsol.dao.*
import pk.mosafir.travsol.model.*
import pk.mosafir.travsol.network.ApiInterface
import pk.mosafir.travsol.response.*
import pk.mosafir.travsol.utils.*

class Repository(
    private val api: ApiInterface,
    private val offersDao: OffersDao,
    private val userDetailDao: UserDetailDao,
    private val tourCityDao: TourCityDao,
    private val tourLocationDao: TourLocationDao,
    private val hotelLocationDao: HotelLocationDao,
    private val airportDao: AirportDao
) {
    private var citiesList = ArrayList<DiscoverPakistanCity>()
    private var mOffersList = ArrayList<Offer>()

    suspend fun getOffersList(): Response<List<Offer>>? {
        mOffersList = offersDao.getOffers() as ArrayList<Offer>
        return try {
            if (mOffersList.size > 0) {
                Response.Success(mOffersList)
            } else {
                mOffersList.clear()
                for (i in 1..200) {
                    if (i % 3 == 0) {
                        val offer = Offer(
                            i.toLong(),
                            "Going to Murree",
                            "Murree",
                            "https://mosafir.pk/assets/images/starting.png",
                            "https://www.mosafir.pk/m_assets/images/ban_mob.png",
                            "PKR",
                            "PKR",
                            "17000",
                            "15000"
                        )
                        mOffersList.add(offer)
                    } else {
                        val offer = Offer(
                            i.toLong(),
                            "Going to murree",
                            "Murree",
                            "https://mosafir.pk/assets/images/starting.png",
                            "https://mosafir.pk/upload/thumbnail/tours_images/Malam-Jabba-winters.jpeg",
                            "PKR",
                            "PKR",
                            "17000",
                            "15000"
                        )
                        mOffersList.add(offer)
                    }
                }
                offersDao.insertOffers(mOffersList)
                Response.Success(offersDao.getOffers())
            }
        } catch (e: Exception) {
            Response.Error(e.message)
        }
    }


    //Discover pakistan data
    suspend fun putRecentCity(id: String) {
        if (!loggedIn)
            api.putCitiesRecentAsync(
                TourPutRecentModel(
                    getTempKey(),
                    userDetailDao.getUserDetail().user_id.toString(),
                    id
                )
            )
        else
            api.putCitiesRecentAsync(
                TourPutRecentModel(
                    getTempKey(),
                    "0",
                    id
                )
            )
    }

    suspend fun getCitiesRecent(): Response<List<DiscoverPakistanCity>> {
        return try {
            getTourCities()
            if (loggedIn)
                Response.Success(
                    api.getCitiesRecentAsync(
                        TourKeyModel(
                            getTempKey(),
                            userDetailDao.getUserDetail().user_id.toString()
                        )
                    ).discover_pakistan_cities
                )
            else
                Response.Success(
                    api.getCitiesRecentAsync(
                        TourKeyModel(
                            getTempKey(),
                            "0"
                        )
                    ).discover_pakistan_cities
                )
        } catch (ex: Exception) {
            Response.Error("123" + ex.message)
        }
    }

    private suspend fun getTourCities() {
        try {
            citiesList = tourCityDao.getCities() as ArrayList<DiscoverPakistanCity>
            if (citiesList.isEmpty()) {
                citiesList =
                    api.getCitiesAsync().discover_pakistan_cities as ArrayList<DiscoverPakistanCity>
                tourCityDao.insertCities(citiesList)
            }
        } catch (ex: Exception) {
        }
    }


    //Hotel functions
    suspend fun putRecentHotel(id: String) {
        if (loggedIn)
            api.putHotelRecentAsync(
                TourPutRecentModel(
                    getTempKey(),
                    userDetailDao.getUserDetail().user_id.toString(),
                    id
                )
            )
        else
            api.putHotelRecentAsync(
                TourPutRecentModel(
                    getTempKey(),
                    "0",
                    id
                )
            )
    }

    suspend fun getHotelCities(string: String?): Response<List<HotelLocation>> {
        return try {
            addHotelCitiesToDb()
            return if (loggedIn)
                Response.Success(
                    api.getHotelRecentLocationAsync(
                        HotelKeyModel(
                            getTempKey(),
                            userDetailDao.getUserDetail().user_id.toString(), string!!
                        )
                    ).hotel_locations
                )
            else
                Response.Success(
                    api.getHotelRecentLocationAsync(
                        HotelKeyModel(
                            getTempKey(),
                            "0", string!!
                        )
                    ).hotel_locations
                )
        } catch (e: Exception) {
            Response.Error(e.message)
        }
    }

    private suspend fun addHotelCitiesToDb() {
        var hotelCities = hotelLocationDao.getHotelLocations()
        if (hotelCities.isEmpty()) {
            try {
                hotelCities = api.getHotelLocationAsync().hotel_locations
                hotelLocationDao.insertHotelLocation(hotelCities)
            } catch (e: Exception) {
            }
        }
    }

    //Airport Functions
    suspend fun getAirport(
        airportItem: JSONArray,
        flag: Int
    ): Response<List<GeneralFlightResponse>> {
        val list =
            if (loggedIn)
                api.getAirportRecentAsync(
                    TourKeyModel(
                        getTempKey(),
                        userDetailDao.getUserDetail().user_id.toString()
                    )
                )
            else
                api.getAirportRecentAsync(
                    TourKeyModel(
                        getTempKey(),
                        "0"
                    )
                )
        val listFly = ArrayList<GeneralFlightResponse>()
        try {
            when (flag) {
                0 -> {
                    for (i in list.fly_from_10) {
                        listFly.add(GeneralFlightResponse(i.id, i.fly_from))
                    }
                }
                1 -> {
                    for (i in list.fly_to_10) {
                        listFly.add(GeneralFlightResponse(i.id, i.fly_from))
                    }
                }
            }
            setAirportDatabase(airportItem)
            return Response.Success(listFly)
        } catch (ex: java.lang.Exception) {
            return Response.Error(ex.toString())
        }
    }

    suspend fun putRecentAirport(model: RecentAirportModal) {
        if(loggedIn)
            model.user_id = userDetailDao.getUserDetail().user_id.toString()
        api.putAirportRecentAsync(model)

    }

    private suspend fun setAirportDatabase(airportItem: JSONArray) {
        val airportList = airportDao.getAirports() as ArrayList<FlyFrom10>
        if (airportList.isEmpty()) {
            for (i in 0 until airportItem.length()) {
                var airportLItem: FlyFrom10
                val objects = airportItem.getJSONObject(i)
                airportLItem = FlyFrom10(
                    null,
                    objects.getString("code") + ", " + objects.getString("name") + ", " + objects.getString(
                        "city"
                    ) + ", " + objects.getString("state") + ", " + objects.getString("country")
                )
                airportList.add(airportLItem)
            }
            airportDao.insertAirports(airportList)
        }
    }

    suspend fun putData() {
        if (loggedIn)
            api.putFTokenAsync(
                FirebaseToken(
                    userDetailDao.getUserDetail().user_id.toLong(),
                    getTempKey(),
                    getFirebaseToken()
                )
            )
        else
            api.putFTokenAsync(
                FirebaseToken(
                    0,
                    getTempKey(),
                    getFirebaseToken()
                )
            )
    }

    suspend fun checkUser(mobile: String, countryCode: String): Response<String> {
        return try {
            val login = LoginModel("", mobile, "+$countryCode")
            val userCheckResponse = api.checkUserResponse(login)
            if (userCheckResponse.Status_code == "1") {
                temp_key = userCheckResponse.accessToken
            }
            Response.Success(userCheckResponse.Status_code)

        } catch (e: Exception) {
            Response.Error("error" + e.message)
        }
    }

    //

    suspend fun checkSocialUser(socialLoginModel: SocialLoginModel): Response<String?> {
        return try {
            val userCheckResponse = api.checkUserSocialResponse(socialLoginModel)
            userCheckResponse!!.user_details!!.auth_type = socialLoginModel.oauth_type
            userDetailDao.insertUserDetail(userCheckResponse.user_details)
            //temp_key = userCheckResponse.user_details.token.toString()
            Response.Success(userCheckResponse.Status_code)
        } catch (e: Exception) {
            Response.Error("error" + e.message)
        }
    }

    //meh
    //for login
    suspend fun validateOTP(mobile: String, temp_key: String): Response<String> {
        return try {
            val userCheckResponse: UserDetailTable = api.checkOTP(OtpModel(mobile, temp_key))
            if (userCheckResponse.message == "Login Successfull") {
                userDetailDao.insertUserDetail(userCheckResponse.user_details)
                userCheckResponse.user_details.token?.let { loggedInUser(it) }
                putData()
                Response.Success("1")
            } else {
                Response.Success("2")
            }
        } catch (e: Exception) {
            Response.Error(e.toString())
        }
    }

    //for register
    suspend fun checkUserRegister(
        name: String?,
        mobile: String,
        countryCode: String
    ): Response<String> {
        return try {
            val login = LoginModel(name, mobile, "+$countryCode")
            val userCheckResponse = api.checkUserResponseRegister(login)
            if (userCheckResponse.Status_code == "1") {
                temp_key = userCheckResponse.accessToken
            }
            Response.Success(userCheckResponse.Status_code)

        } catch (e: Exception) {
            Response.Error("error" + e.message)
        }
    }

    //for register
    suspend fun validateOTPRegister(mobile: String, temp_key: String): Response<String> {
        return try {
            val userCheckResponse: UserDetailTable =
                api.checkOTPRegister(OtpModel(mobile, temp_key))
            if (userCheckResponse.message == "Login Successfull") {
                loggedInUser("0")
                putData()
                Response.Success("1")
            } else {
                Response.Success("2")
            }
        } catch (e: Exception) {
            Response.Error(e.toString())
        }
    }

    //Trip Location
    suspend fun getRecentTripLocation(): Response<List<TourLocation>> {
        return try {
            getTourLocations()
            return if(loggedIn) Response.Success(
                api.getTourRecentLocationAsync(
                    TourKeyModel(
                        getTempKey(),
                        userDetailDao.getUserDetail().user_id.toString()
                    )
                ).tours_and_cities
            )
            else{
                Response.Success(
                    api.getTourRecentLocationAsync(
                        TourKeyModel(
                            getTempKey(),
                            "0"
                        )
                    ).tours_and_cities
                )
            }
        } catch (e: Exception) {
            Response.Error(e.message)
        }
    }

//    suspend fun putRecentTripLocation(name: String) {
//        api.putTourRecentAsync(
//            TourPutRecentModel(
//                getTempKey(),
//                userDetailDao.getUserDetail().user_id.toString(),
//                name
//            )
//        )
//    }

    private suspend fun getTourLocations() {
        try {
            val locations = api.getTourLocationAsync().tours_and_cities
            tourLocationDao.insertTourCities(locations)
        } catch (e: Exception) {
        }
    }
}