package pk.mosafir.travsol.network

import pk.mosafir.travsol.model.*
import pk.mosafir.travsol.response.*
import retrofit2.http.*

interface ApiInterface {

    //Discover pakistan end points
    @GET("discover_pakistan_cities")
    suspend fun getCitiesAsync(): DiscoverPakistanResponse

    @POST("discover_pakistan_search")
    suspend fun getCitiesRecentAsync(@Body tourKeyModel: TourKeyModel): DiscoverPakistanResponse

    @POST("discover_pakistan_search_store")
    suspend fun putCitiesRecentAsync(@Body tourKeyModel: TourPutRecentModel)

    //Airport related end points
    @POST("recent_airport_searches")
    suspend fun getAirportRecentAsync(@Body tourKeyModel: TourKeyModel): AirportList

    @POST("recent_airport_searches_store")
    suspend fun putAirportRecentAsync(@Body tourKeyModel: RecentAirportModal)

    //Tour Location end points
    @GET("tour_location_api")
    suspend fun getTourLocationAsync(): TourLocationResponse

    @POST("recent_tour_searches")
    suspend fun getTourRecentLocationAsync(@Body tourKeyModel: TourKeyModel): TourLocationResponse

    @POST("recent_tour_searches_store")
    suspend fun putTourRecentAsync(@Body tourKeyModel: TourPutRecentModel)

    //hotel location end points
    @GET("hotel_location_api")
    suspend fun getHotelLocationAsync(): HotelLocationResponse

    @POST("recent_hotel_searches")
    suspend fun getHotelRecentLocationAsync(@Body tourKeyModel: HotelKeyModel): HotelLocationResponse

    @POST("recent_hotel_searches_store")
    suspend fun putHotelRecentAsync(@Body tourKeyModel: TourPutRecentModel)

    //Login Responses
    @POST("mobile_login")
    suspend fun checkOTP(@Body mobile_otp: OtpModel): UserDetailTable

    @POST("check_mobile")
    suspend fun checkUserResponse(@Body loginModel: LoginModel): UserCheckResponse

    @POST("social_login")
    suspend fun checkUserSocialResponse(@Body socialLoginModel: SocialLoginModel): UserCheckResponse

    //Register Responses
    @POST("register_user")
    suspend fun checkUserResponseRegister(@Body loginModel: LoginModel): UserCheckResponse

    @POST("verify_user")
    suspend fun checkOTPRegister(@Body mobile_otp: OtpModel): UserDetailTable

    //Firebase Token Update
    @Headers("Authentication: ")
    @POST("firebase_token_update")
    suspend fun putFTokenAsync(@Body firebaseToken: FirebaseToken)

    companion object {
        const val BASE_URL = "http://portal.mosafir.pk/api/"
        const val REQUEST_TIMEOUT = 30L
    }
}