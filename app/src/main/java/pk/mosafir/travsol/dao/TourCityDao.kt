package pk.mosafir.travsol.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import pk.mosafir.travsol.response.DiscoverPakistanCity
import pk.mosafir.travsol.response.Response
import pk.mosafir.travsol.response.TourCity

@Dao
interface TourCityDao {
    @Query("SELECT * FROM discover_pakistan_city")
    suspend fun getCities(): List<DiscoverPakistanCity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCities(offers: List<DiscoverPakistanCity>)

    @Query("SELECT * FROM discover_pakistan_city WHERE destination_id=:id")
    suspend fun getCityId(id: Long): DiscoverPakistanCity

    @Query("SELECT * FROM discover_pakistan_city WHERE destination_title like :name")
    suspend fun getCityLike(name: String): List<DiscoverPakistanCity>
}