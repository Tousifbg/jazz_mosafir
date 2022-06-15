package pk.mosafir.travsol.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import pk.mosafir.travsol.response.TourLocation

@Dao
interface TourLocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTourCities(offers: List<TourLocation>)

    @Query("SELECT * FROM tour_location WHERE tour_title like :id order by city_name asc limit 10")
    suspend fun getTourLocationString(id: String): List<TourLocation>
}