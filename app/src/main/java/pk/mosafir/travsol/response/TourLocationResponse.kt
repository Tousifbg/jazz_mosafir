package pk.mosafir.travsol.response

import androidx.room.Entity
import androidx.room.PrimaryKey

data class TourLocationResponse(
    val tours_and_cities: List<TourLocation> = listOf()
)

@Entity(tableName = "tour_location")
data class TourLocation(
    val city_name: String?,
    val tour_id: Int?,
    val tour_title: String?,
    val city_id: Int = 0,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)