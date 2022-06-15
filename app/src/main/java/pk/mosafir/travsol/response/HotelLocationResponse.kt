package pk.mosafir.travsol.response

import androidx.room.Entity
import androidx.room.PrimaryKey

data class HotelLocationResponse(
    val hotel_locations: List<HotelLocation> = listOf()
)

@Entity(tableName = "hotel_location")
data class HotelLocation(
    val hotel_id: Int = 0,
    val hotel_name: String?,
    val country_code: String?,
    val city_code: String?,
    val city_name: String?,
    val city_id:Int = 0,
    @PrimaryKey(autoGenerate = true) val pid: Int = 0
)