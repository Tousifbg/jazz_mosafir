package pk.mosafir.travsol.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import pk.mosafir.travsol.response.*

@Dao
interface HotelLocationDao {
    @Query("SELECT * FROM hotel_location")
    suspend fun getHotelLocations(): List<HotelLocation>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHotelLocation(offers: List<HotelLocation>)

    @Query("SELECT * FROM hotel_location WHERE city_name like :id or hotel_name like :id order by city_name asc limit 10")
    suspend fun getHotelLocationString(id: String): List<HotelLocation>
}