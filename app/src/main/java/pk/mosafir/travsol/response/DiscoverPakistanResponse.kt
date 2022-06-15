package pk.mosafir.travsol.response

import androidx.room.Entity
import androidx.room.PrimaryKey

data class DiscoverPakistanResponse(
    val discover_pakistan_cities: List<DiscoverPakistanCity> = listOf()
)

@Entity(tableName = "discover_pakistan_city")
data class DiscoverPakistanCity(
    @PrimaryKey val destination_id: Int = 0,
    val destination_map_link: String?,
    val destination_title: String?,
    val nearest_airport: String?
)